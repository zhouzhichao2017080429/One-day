package zzc.one_day;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.LinkedList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

public final class One_day extends JavaPlugin {

    List<World> worlds;
    public static FileConfiguration config;
    String printout_flag;
    String remind_flag;
    double raise_time;
    double fall_time;
    double day_time;
    double night_time;


    BukkitRunnable client = new BukkitRunnable() {
        public void run() {
            Calendar calendar = Calendar.getInstance();

            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            long minu = calendar.get(Calendar.MINUTE);
            double hm = hour+minu/60.0;
            int ticks = time_map(hm);
            System.out.println(hour+" "+minu+" "+hm+" "+ticks);
            setTime(worlds, ticks);

            //整10分报时
            String current_time = hour + ":" + minu;
            if(minu<10){current_time = hour + ":0" + minu;}
            if((minu>=10) && (minu%10==0)){
                if(printout_flag=="true"){print_out(current_time);}
                if(remind_flag=="true"){remind(current_time);};
            }

            //整点报时
            if(minu==0){
                for(Player p:getServer().getOnlinePlayers()){
                    p.sendTitle(hour + " : 00", "整点报时",  20,  20,  20);
                }
            }

        }
    };

    public int time_map(double hm) {
        int ticks = 0;
        if(hm<=raise_time){hm=hm+24;}
        double relative_day = (hm-raise_time)/day_time;
        double relative_night = (hm-fall_time)/night_time;
        if(relative_day>=0 && relative_day<=1){ticks = (int) (12000*relative_day);}
        if(relative_night>=0 && relative_night<=1){ticks = (int) (12000+12000*relative_night);}
        return ticks;
    }


    @Override
    public void onEnable() {
        config = this.getConfig();
        List<World> configWorlds = new LinkedList<>();
        configWorlds.add(this.getServer().getWorld("world"));
        worlds = configWorlds;
        System.out.println("§6轻量化时间同步插件已启动..");
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("gamerule doDaylightCycle false");
        this.client.runTaskTimer(this, 0L, 1200L);

        createConfig();

        loadConfig();

        // 提取和输出translation关键字中的指令和值
        printout_flag = config.getString("printout");
        remind_flag = config.getString("remind");
//        System.out.println(printout_flag);
//        System.out.println(remind_flag);
        String raise_time_str = config.getString("raise_time");
        String fall_time_str = config.getString("fall_time");
        String[] rt_spt = raise_time_str.split(":");
        String[] ft_spt = fall_time_str.split(":");
//        System.out.println(raise_time_str);
//        System.out.println(fall_time_str);
        raise_time = Integer.parseInt(rt_spt[0])+Integer.parseInt(rt_spt[1])/60;
        fall_time = Integer.parseInt(ft_spt[0])+Integer.parseInt(ft_spt[1])/60;
        day_time = fall_time - raise_time;
        night_time = 24 - day_time;

    }

    public void setTime(List<World> worlds, int tick) {
        String s = "time set " + tick;
        a_command(s);
    }

    public void print_out(String s) {
        System.out.println(s);
    }

    public void remind(String s) {
        for(Player p:getServer().getOnlinePlayers()){
            p.sendMessage(s);
        }
    }

    public void a_command(String s) {
        CommandSender sender = Bukkit.getConsoleSender();
        sender.getServer().dispatchCommand(sender, s);

    }

    private void createConfig() {
        // 获取插件数据文件夹
        File dataFolder = getDataFolder();

        // 如果插件数据文件夹不存在，创建它
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // 获取配置文件对象
        File configFile = new File(dataFolder, "config.yml");

        // 如果配置文件不存在，从资源文件中复制默认配置
        if (!configFile.exists()) {
            try (InputStream inputStream = getResource("config.yml")) {
                Files.copy(inputStream, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig() {
        // 加载config.yml文件
        config = getConfig();
        // 保存默认的config.yml文件到插件数据文件夹中（如果config.yml不存在）
        saveDefaultConfig();
    }

}
