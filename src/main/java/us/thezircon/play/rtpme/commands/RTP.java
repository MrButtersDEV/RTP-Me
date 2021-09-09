package us.thezircon.play.rtpme.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.rtpme.RTPMe;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RTP implements TabExecutor {

    private static final RTPMe PLUGIN = RTPMe.getPlugin(RTPMe.class);

    private int maxX = PLUGIN.getConfig().getInt("MaxX");
    private int maxZ = PLUGIN.getConfig().getInt("MaxZ");

    private int minX = PLUGIN.getConfig().getInt("MinX");
    private int minZ = PLUGIN.getConfig().getInt("MinZ");

    private boolean doForceWorld = PLUGIN.getConfig().getBoolean("ForceWorld.enabled");

    private Location getRTPLocation(World world) {

        if (doForceWorld) {
            world = Bukkit.getWorld(PLUGIN.getConfig().getString("ForceWorld.world"));
        }

        Random random = new Random();
        int x = random.nextInt((maxX - minX) + 1) + minX;
        int z = random.nextInt((maxZ - minZ) + 1) + minZ;

        if (random.nextBoolean()) {
            x=x*-1;
        }

        if (random.nextBoolean()) {
            z=z*-1;
        }

        Block top = world.getHighestBlockAt(x, z);
        return top.getLocation().add(0.5, 1, 0.5);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();

            if (args.length==0 && player.hasPermission("rtpme.rtp")) {

                if (PLUGIN.getConfig().getStringList("BlacklistedWorlds").contains(world.getName())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgBlacklistedWorld")));
                    return false;
                }

                if (PLUGIN.rtpCooldown.containsKey(player) && !player.hasPermission("rtpme.cooldown.bypass")) {
                    int cooldown = PLUGIN.getConfig().getInt("Cooldown");
                    long secondsLeft = (PLUGIN.rtpCooldown.get(player)/1000)+cooldown - (System.currentTimeMillis()/1000);
                    if (secondsLeft>0) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgCooldown").replace("{timeleft}", secondsLeft + "")));
                        return false;
                    }
                }

                String search = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgSearch"));
                player.sendMessage(search);


                Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = getRTPLocation(world);
                        while (!isSafe(loc)) {
                            loc = getRTPLocation(world);
                        }

                        final Location fLoc = loc;

                        Bukkit.getScheduler().runTask(PLUGIN, new Runnable() {
                            @Override
                            public void run() {
                                player.teleport(fLoc, PlayerTeleportEvent.TeleportCause.COMMAND);
                            }
                        });

                        // bStats most common biome.
                        String biome = world.getBiome((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()).name();
                        if (PLUGIN.biomeCounter.containsKey(biome)) {
                            int count = PLUGIN.biomeCounter.get(biome);
                            count++;
                            PLUGIN.biomeCounter.put(biome, count);
                        } else {
                            PLUGIN.biomeCounter.put(biome, 1);
                        }

                        String locMsg = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgLoc"));
                        locMsg = locMsg.replace("{x}", String.valueOf(fLoc.getBlockX()));
                        locMsg = locMsg.replace("{y}", String.valueOf(fLoc.getBlockY()));
                        locMsg = locMsg.replace("{z}", String.valueOf(fLoc.getBlockZ()));
                        player.sendMessage(locMsg);
                    }
                });

                //Cooldown
                long time = System.currentTimeMillis();
                PLUGIN.rtpCooldown.put(player, time);

            } else if (args.length==1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("rtpme.reload")) {
                PLUGIN.reloadConfig();
                String reloadMSG = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgReload"));
                player.sendMessage(reloadMSG);
            } else {
                String msgArgsPerm = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgArgsPerm"));
                player.sendMessage(msgArgsPerm);
            }
        } else {
            if (sender.hasPermission("rtpme.reload") && args.length==1 && args[0].equalsIgnoreCase("reload")) {
                PLUGIN.reloadConfig();
                String reloadMSG = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgReload"));
                sender.sendMessage(reloadMSG);
            } else if (sender.hasPermission("rtpme.rtp.other")) {
                Player target = Bukkit.getPlayer(args[0]);
                World targetWorld = Bukkit.getWorld(args[1]);

                String search = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgSearch"));
                target.sendMessage(search);


                Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, new Runnable() {
                    @Override
                    public void run() {
                        Location loc = getRTPLocation(targetWorld);
                        while (!isSafe(loc)) {
                            loc = getRTPLocation(targetWorld);
                        }

                        final Location fLoc = loc;

                        Bukkit.getScheduler().runTask(PLUGIN, new Runnable() {
                            @Override
                            public void run() {
                                target.teleport(fLoc, PlayerTeleportEvent.TeleportCause.COMMAND);
                            }
                        });

                        // bStats most common biome.
                        String biome = targetWorld.getBiome((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()).name();
                        if (PLUGIN.biomeCounter.containsKey(biome)) {
                            int count = PLUGIN.biomeCounter.get(biome);
                            count++;
                            PLUGIN.biomeCounter.put(biome, count);
                        } else {
                            PLUGIN.biomeCounter.put(biome, 1);
                        }

                        Location pLoc = target.getLocation();
                        String locMsg = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgLoc"));
                        locMsg = locMsg.replace("{x}", String.valueOf(pLoc.getBlockX()));
                        locMsg = locMsg.replace("{y}", String.valueOf(pLoc.getBlockY()));
                        locMsg = locMsg.replace("{z}", String.valueOf(pLoc.getBlockZ()));
                        target.sendMessage(locMsg);
                    }
                });

            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length==1 && sender.hasPermission("rtpme.reload")) {
            return Arrays.asList("reload");
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        if (loc.getBlock().getType().equals(Material.WATER)) {
            return false;
        } else if (loc.getBlock().getType().equals(Material.LAVA)) {
            return false;
        } else if (loc.subtract(0,1,0).getBlock().getType().equals(Material.LAVA)) {
            return false;
        } else if (loc.subtract(0,1,0).getBlock().getType().equals(Material.WATER)) {
            return false;
        } else if (loc.add(0,2,0).getBlock().getType().equals(Material.WATER)) {
            return false;
        } else if (loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).name().contains("ocean")) {
            return false;
        } else return !loc.add(0, 2, 0).getBlock().getType().equals(Material.LAVA);
    }
}
