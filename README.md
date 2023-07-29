# One day


**轻量化真实世界时间天气同步插件**

**MCBBS论坛:**

https://www.mcbbs.net/thread-1374813-1-1.html

**作用:**

本插件可用于服务器内时间与真实世界时间同步，及天气与真实世界天气同步    

  
优点：

**1. 轻量：**    插件大小小于**6kb**，不会增加服务器计算负担   

**2. 极易上手：** 安装插件后重启服务器即可，无需任何操作      


![时间同步](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/5c1eda30-332f-480e-b9da-d9e279ec6ce9)
![天气同步](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/709a8869-6420-416c-af08-0e2713a15bfa)




![194600umhsn5yqq3semgsh](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/76284387-e959-4d70-b139-0052cc6d6115)

![234852gjh7a7gs79ap23s7](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/7a2d1597-974d-4d56-aa6a-a0a3e34ea305)


```
package zzc.one_day;

import java.io.*;
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
import zzc.one_day.Metrics;

public final class One_day extends JavaPlugin {

    List<World> worlds;
    public static FileConfiguration config;
    String printout_flag;
    String remind_flag;
    String time_flag;
    String weather_flag;
    String city_code;
    double raise_time;
    double fall_time;
    double day_time;
    double night_time;
    WeatherApiClient weather;
    CommandSender sender = Bukkit.getConsoleSender();

    BukkitRunnable client = new BukkitRunnable() {
        public void run() {

            Calendar calendar = Calendar.getInstance();

            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            long minu = calendar.get(Calendar.MINUTE);
            double hm = hour + minu / 60.0;
            int ticks = time_map(hm);
//            System.out.println(hour+" "+minu+" "+hm+" "+ticks);
            if(time_flag=="true") {
                setTime(worlds, ticks);

                //整10分报时
                String current_time = hour + ":" + minu;
                if (minu < 10) {
                    current_time = hour + ":0" + minu;
                }
                if ((minu >= 10) && (minu % 10 == 0)) {
                    if (printout_flag == "true") {
                        print_out(current_time);
                    }
                    if (remind_flag == "true") {
                        remind(current_time);
                    }
                    ;
                }

                //整点报时
                if (minu == 0) {
                    for (Player p : getServer().getOnlinePlayers()) {
                        p.sendTitle(hour + " : 00", "整点报时", 20, 20, 20);
                    }
                }
            }
            if(weather_flag=="true") {
                String current_wather = weather.get_weather();
                System.out.println("今日天气为：" + current_wather);
                if(current_wather.contains("雨")){System.out.println("雨");sender.getServer().dispatchCommand(sender,"rain world storm");}
                if(current_wather.contains("晴")){System.out.println("晴");sender.getServer().dispatchCommand(sender,"rain world sun");}
            }
        }
    };

    public int time_map(double hm) {
        int ticks = 0;
        if(hm<=raise_time){hm=hm+24;}
//        System.out.println("§6hm: §f"+hm);
//        System.out.println("§6raise_time: §f"+raise_time);
//        System.out.println("§6fall_time: §f"+fall_time);
        double relative_day = (hm-raise_time)/day_time;
        double relative_night = (hm-fall_time)/night_time;
        if(relative_day>=0 && relative_day<=1){ticks = (int) (12000*relative_day);}
        if(relative_night>=0 && relative_night<=1){ticks = (int) (12000+12000*relative_night);}
//        System.out.println("§6relative_day: §f"+relative_day);
//        System.out.println("§6relative_night: §f"+relative_night);
        return ticks;
    }


    @Override
    public void onEnable() {
        weather = new WeatherApiClient();

        Metrics metrics = new Metrics(this, 19218);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        config = this.getConfig();
        List<World> configWorlds = new LinkedList<>();
        configWorlds.add(this.getServer().getWorld("world"));
        worlds = configWorlds;
        System.out.println("§6轻量化时间同步插件已启动..");
        sender.sendMessage("gamerule doDaylightCycle false");
        this.client.runTaskTimer(this, 0L, 1200L);

        createConfig();
        loadConfig();
        create_csv();

        // 提取和输出translation关键字中的指令和值
        printout_flag = config.getString("printout");
        remind_flag = config.getString("remind");
        time_flag = config.getString("time-map");
        weather_flag = config.getString("weather-map");
        city_code = config.getString("city_code");
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

    private void create_csv(){
        File targetFile = new File(getDataFolder(), "城市列表.csv");

        // 如果目标文件已经存在，则不需要复制
        if (targetFile.exists()) {
            getLogger().info("data.csv already exists. No need to copy.");
            return;
        }

        // 从resource文件夹获取输入流
        try (InputStream inputStream = getResource("城市列表.csv")) {
            if (inputStream == null) {
                getLogger().warning("Failed to find data.csv in resource folder.");
                return;
            }

            // 创建目标文件的输出流
            try (OutputStream outputStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

```
