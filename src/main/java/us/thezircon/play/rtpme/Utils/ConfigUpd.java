package us.thezircon.play.rtpme.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import sun.plugin.PluginURLJarFileCallBack;
import us.thezircon.play.rtpme.RTPMe;

import java.io.File;
import java.io.IOException;

public class ConfigUpd {

    private static final RTPMe PLUGIN = RTPMe.getPlugin(RTPMe.class);

    private File config = new File(PLUGIN.getDataFolder(), "config.yml");
    private YamlConfiguration conf = YamlConfiguration.loadConfiguration(config);
    private int ver;

    public ConfigUpd(int ver) {
        this.ver = ver;
    }

    public boolean up2Date() {
        if (conf.contains("confVersion")) {
            int curVer = conf.getInt("confVersion");
            if (curVer==ver) {
                return true;
            } else {
                System.out.println("[RTP-ME] Updating from " + curVer + " to " + ver);
                update();
            }
        } else { // Legacy Config
            updateLegacy();
            return false;
        }
        return false;
    }

    public void updateLegacy() {
        conf.set("confVersion", ver);
        try {
            conf.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update();
    }

    public void update() {
        switch (ver) {
            case 1:
                System.out.println("[RTP-ME] Invalid/Outdated Config Version!!");
            case 2:
                conf.set("MinX", 500);
                conf.set("MinZ", 500);
                System.out.println("[RTP-ME] Applying config updates for version 2");
            case 3:
                conf.set("ForceWorld.enabled", false);
                System.out.println("[RTP-ME] While Applying config updates for version 3 \"ForceWorld.enabled\" was set to false to maintain previous functionality");
                System.out.println("[RTP-ME] If you would like this to change and force the user to rtp in preferred world please set to \"true\"");
                conf.set("ForceWorld.world", "world");
                System.out.println("[RTP-ME] Applying config updates for version 3");

                conf.set("confVersion", ver);
                break;
            default:
                System.out.println("[RTP-ME] Unable to update config");
        }
        try {
            conf.save(config);
            System.out.println("[RTP-ME] Applied all config updates!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
