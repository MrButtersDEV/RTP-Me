package us.thezircon.play.rtpme.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.thezircon.play.rtpme.RTPMe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import static com.google.common.net.HttpHeaders.USER_AGENT;

public class VersionChk {

    private static final RTPMe PLUGIN = RTPMe.getPlugin(RTPMe.class);

    private static final Logger log = Logger.getLogger("Minecraft");

    public static void checkVersion(String name, int id) throws Exception { //https://api.spigotmc.org/legacy/update.php?resource=76103"

        String msgPrefix = ChatColor.translateAlternateColorCodes('&', "&5[RTP-ME]");

        String url = "https://api.spigotmc.org/legacy/update.php?resource="+id;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        PLUGIN.getServer().getConsoleSender().sendMessage(msgPrefix+" Checking for new verison...");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        String spigotVerison = response.toString();
        String ver = Bukkit.getServer().getPluginManager().getPlugin(name).getDescription().getVersion();
        if (spigotVerison.equals(ver)) {
            PLUGIN.getServer().getConsoleSender().sendMessage(msgPrefix + " " + ChatColor.DARK_GREEN + "Plugin is up-to-date.");
        } else {
            PLUGIN.getServer().getConsoleSender().sendMessage(msgPrefix + ChatColor.RED + " UPDATE FOUND: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/"+id+"/");
            PLUGIN.getServer().getConsoleSender().sendMessage(msgPrefix + ChatColor.GOLD + " Version: " + ChatColor.GREEN + response.toString() + ChatColor.AQUA + " Using Version: " + ChatColor.DARK_AQUA + ver);
            PLUGIN.UP2Date = false;
        }
    }

    public static void noConnection(){
        String msgPrefix = ChatColor.translateAlternateColorCodes('&', "&5[RTP-ME]");
        PLUGIN.getServer().getConsoleSender().sendMessage(msgPrefix + " " + ChatColor.LIGHT_PURPLE + "Cannot check for update's - No internet connection!");
    }

}
