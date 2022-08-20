package zzc.one_day;

import java.util.Calendar;
import java.util.List;
import java.util.LinkedList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;

public final class One_day extends JavaPlugin {

    List<World> worlds;
    Configuration config;
    long tickUTC = 0L;
    BukkitRunnable client = new BukkitRunnable() {
        public void run() {
            double tick;
            Calendar calendar = Calendar.getInstance();
//            long tick = (long)18000 + (calendar.get(Calendar.HOUR_OF_DAY) * 1000 + calendar.get(Calendar.MINUTE) * 1000 / 60) + One_day.this.tickUTC;
//
//            if (tick >= 24000L) {
//                tick -= 24000L;
//            }
            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            say("hour: " + hour);
            long minu = calendar.get(Calendar.MINUTE);

//            tick = (hour-6) * 666.666 + minu * 666.666 / 60 + One_day.this.tickUTC;
//            if (hour<6) {
//                tick = (12000 + hour * 2000 + minu * 2000 / 60) + One_day.this.tickUTC;
//            }

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

    public void a_command(String s) {
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
            if (this.tickUTC > 24000L) {
                this.tickUTC -= 24000L;
            }

            if (this.tickUTC == 24000L) {
                this.tickUTC = 0L;
            }

            if (this.tickUTC <= 0L) {
                this.tickUTC += 24000L;
            }
//            long tick = (long)18000 + (calendar.get(Calendar.HOUR_OF_DAY) * 1000 + calendar.get(Calendar.MINUTE) * 1000 / 60) + this.tickUTC;
//            if (tick >= 24000L) {
//                tick -= 24000L;
//            }
            long hour = calendar.get(Calendar.HOUR_OF_DAY);
            long minu = calendar.get(Calendar.MINUTE);

//            tick = (hour-6) * 666.666 + minu * 666.666 / 60 + One_day.this.tickUTC;
//            if (hour<6) {
//                tick = (12000 + hour * 2000 + minu * 2000 / 60) + One_day.this.tickUTC;
//            }

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
