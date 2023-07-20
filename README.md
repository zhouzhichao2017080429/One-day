# One day
One day plugin for Minecraft


One day - 轻量化真实世界时间同步

for more information:
https://www.mcbbs.net/thread-1374813-1-1.html

本插件可用于服务器内时间与真实世界时间同步

>>优点

1. 轻量：    插件大小小于5kb，不会增加服务器计算负担     
2. 极易上手：安装插件后重启服务器即可，无需任何操作
3. 方便：    如需调整时间，一个指令即可                          
4. 兼容：    兼容多世界插件，同步多个世界的时间          

![194600umhsn5yqq3semgsh](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/76284387-e959-4d70-b139-0052cc6d6115)

![234852gjh7a7gs79ap23s7](https://github.com/zhouzhichao2017080429/One-day/assets/73045175/7a2d1597-974d-4d56-aa6a-a0a3e34ea305)


package zzc.one_day;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class One_day extends JavaPlugin {

    List<World> worlds;
    Configuration config;
    long tickUTC = 0L;
    BukkitRunnable client = new BukkitRunnable() {  //BukkitRunnable是一个定时器，每隔一定时间让后台执行语句，时间同步的原理就是让服务器定时获取现实世界时间，用现实世界时间设置游戏内时间
        public void run() {
            double tick;
            Calendar calendar = Calendar.getInstance();  //java自带的获取主机的时间，对应的是现实世界时间

            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            say("hour: " + hour);
            long minu = calendar.get(Calendar.MINUTE);



            int a = (int)hour - 21;
            if (a<0){
                a += 24;
            }

            tick = 12000 + a * 1200 + minu * 1200 / 60 + One_day.this.tickUTC;
            if (hour>=7 && hour<21) {
                tick = ((hour-7) * 857.14 + minu * 857.14 / 60) + One_day.this.tickUTC;
            }

            setTime(worlds, (int)tick);  //上面的步骤从现实世界时间中提取时、分，并转换为minecraft中的tick；当前句子将服务器内所有世界时间设置为现实世界时间

            String current_time = "summer time:  " + hour + ":" + minu;
            if(minu<10){
                current_time = "summer time:  " + hour + ":0" + minu;
            }
            if((minu>=10) && (minu%10==0)){
                say(current_time);
                broadcast(current_time);
            }

            if(minu==0){
                for(Player p:getServer().getOnlinePlayers()){
                    p.sendTitle(hour + " : 00", "summer time",  20,  20,  20);
                }
            }

        }
    };

    @Override
    public void onEnable() {
        config = this.getConfig();
        List<World> configWorlds = new LinkedList<>();
        configWorlds.add(this.getServer().getWorld("world"));
        worlds = configWorlds;
        getCommand("one_day").setExecutor(this);
        getLogger().info("one_day build by zzc!");
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("gamerule doDaylightCycle false");
        this.client.runTaskTimer(this, 0L, 1200L);
    }

    public void setTime(List<World> worlds, int tick) {
        String s = "time set " + tick;
        a_command(s);
    }

    public void say(String s) {
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(s);
    }

    public void broadcast(String s) {
        for(Player p:getServer().getOnlinePlayers()){
            p.sendMessage(s);
        }
    }
    

    public void a_command(String s) {  //获取服务器后台，执行句子对应的指令
        CommandSender sender = Bukkit.getConsoleSender();
        sender.getServer().dispatchCommand(sender, s);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        double tick;
        Calendar calendar = Calendar.getInstance();
        if (args.length < 2) {
            return false;
        } else {
            this.tickUTC = (Long.parseLong(args[0]) - (long)calendar.get(Calendar.HOUR_OF_DAY)) * 1000L + (Long.parseLong(args[1]) - (long)calendar.get(Calendar.MINUTE)) * 1000L / 60L;
            if (this.tickUTC > 24000L) { //24000L对应0点，0点比较特殊，上一刻是昨天，下一刻是今天；实现冬至夏至时间的原理是：比如现实世界白天12小时，晚上12小时，我把这白天12小时映射为游戏里一天中超过一半的时间，我把这晚上12小时映射为游戏里小于一半的时间
                this.tickUTC -= 24000L;
            }

            if (this.tickUTC == 24000L) {
                this.tickUTC = 0L;
            }

            if (this.tickUTC <= 0L) {
                this.tickUTC += 24000L;
            }
            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            long minu = calendar.get(Calendar.MINUTE);



            int a = (int)hour - 21;
            if (a<0){
                a += 24;
            }

            tick = 12000 + a * 1200 + minu * 1200 / 60 + One_day.this.tickUTC;
            if (hour>=7 && hour<21) {
                tick = ((hour-7) * 857.14 + minu * 857.14 / 60) + One_day.this.tickUTC;
            }

            setTime(worlds, (int)tick);

            String current_time = "summer time:  " + hour + ":" + minu;
            say(current_time);

            return true;
        }
    }
}
