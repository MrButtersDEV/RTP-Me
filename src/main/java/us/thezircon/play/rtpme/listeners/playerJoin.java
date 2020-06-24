package us.thezircon.play.rtpme.listeners;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.thezircon.play.rtpme.RTPMe;

public class playerJoin implements Listener {

    private static final RTPMe PLUGIN = RTPMe.getPlugin(RTPMe.class);

    String msgPrefix = ChatColor.translateAlternateColorCodes('&', "&5[RTP-ME]");
    String msgUpdate = ChatColor.translateAlternateColorCodes('&', "&6➤ &eClick &6&lHERE&e to view the latest version.");

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("rtpme.notifyupdate") && !PLUGIN.UP2Date){
            String ver = Bukkit.getServer().getPluginManager().getPlugin("RTP-Me").getDescription().getVersion();
            player.sendMessage(msgPrefix + " " + ChatColor.YELLOW + "Version: " + ChatColor.RED + ver + ChatColor.YELLOW + " is not up to date. Please check your console on next startup or reload.");

            TextComponent message = new TextComponent(msgUpdate);
            message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/79718/" ) );
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click to open on spigot!" ).create() ) );
            player.spigot().sendMessage( message );
        }

    }

}