package us.thezircon.play.rtpme;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.rtpme.Utils.Metrics;
import us.thezircon.play.rtpme.Utils.VersionChk;
import us.thezircon.play.rtpme.commands.RTP;
import us.thezircon.play.rtpme.listeners.playerJoin;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public final class RTPMe extends JavaPlugin {

    public HashMap<Player, Long> rtpCooldown = new HashMap<>();
    public HashMap<String, Integer> biomeCounter = new HashMap<>();
    public boolean UP2Date = true;

    @Override
    public void onEnable() {
        // Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Commands
        getCommand("rtp").setExecutor(new RTP());

        // Listeners
        getServer().getPluginManager().registerEvents(new playerJoin(), this);

        // bStats
        Metrics metrics = new Metrics(this, 7738);
        metrics.addCustomChart(new Metrics.AdvancedPie("most_teleported_to_biomes", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                return biomeCounter;
            }
        }));

        //Version Check
        String pluginName = this.getName();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    VersionChk.checkVersion(pluginName, 79718);
                } catch (UnknownHostException e) {
                    VersionChk.noConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();

    }
}
