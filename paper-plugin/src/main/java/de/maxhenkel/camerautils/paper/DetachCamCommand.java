package de.maxhenkel.camerautils.paper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetachCamCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public DetachCamCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void register(JavaPlugin plugin) {
        DetachCamCommand command = new DetachCamCommand(plugin);
        plugin.getCommand("detachcam").setExecutor(command);
        plugin.getCommand("detachcam").setTabCompleter(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /detachcam set <x> <y> <z> <pitch> <yaw>");
            sender.sendMessage("       /detachcam set <player> <x> <y> <z> <pitch> <yaw>");
            sender.sendMessage("       /detachcam setall");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("setall")) {
            return handleSetAll(sender);
        } else if (subCommand.equals("set")) {
            return handleSet(sender, args);
        } else {
            sender.sendMessage("Unknown subcommand. Use 'set' or 'setall'.");
            return true;
        }
    }

    private boolean handleSetAll(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        double x = player.getLocation().getX();
        double y = player.getLocation().getY() + player.getEyeHeight();
        double z = player.getLocation().getZ();
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        for (Player target : Bukkit.getOnlinePlayers()) {
            CameraUtilsPlugin.sendDetach(target, x, y, z, pitch, yaw);
        }

        sender.sendMessage("Detached camera for all players at " + formatCoords(x, y, z, pitch, yaw));
        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (args.length == 6) {
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                float pitch = Float.parseFloat(args[4]);
                float yaw = Float.parseFloat(args[5]);

                for (Player target : Bukkit.getOnlinePlayers()) {
                    CameraUtilsPlugin.sendDetach(target, x, y, z, pitch, yaw);
                }

                sender.sendMessage("Detached camera for all players at " + formatCoords(x, y, z, pitch, yaw));
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number format. Use: /detachcam set <x> <y> <z> <pitch> <yaw>");
                return true;
            }
        } else if (args.length == 7) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("Player not found: " + args[1]);
                return true;
            }

            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                float pitch = Float.parseFloat(args[5]);
                float yaw = Float.parseFloat(args[6]);

                CameraUtilsPlugin.sendDetach(target, x, y, z, pitch, yaw);

                sender.sendMessage("Detached camera for " + target.getName() + " at " + formatCoords(x, y, z, pitch, yaw));
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number format. Use: /detachcam set <player> <x> <y> <z> <pitch> <yaw>");
                return true;
            }
        } else {
            sender.sendMessage("Usage: /detachcam set <x> <y> <z> <pitch> <yaw>");
            sender.sendMessage("       /detachcam set <player> <x> <y> <z> <pitch> <yaw>");
            return true;
        }
    }

    private String formatCoords(double x, double y, double z, float pitch, float yaw) {
        return String.format("(%.2f, %.2f, %.2f) pitch=%.2f yaw=%.2f", x, y, z, pitch, yaw);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("set");
            completions.add("setall");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            completions.add("~");
        } else if (args.length >= 3 && args.length <= 6 && args[0].equalsIgnoreCase("set")) {
            completions.add("~");
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
