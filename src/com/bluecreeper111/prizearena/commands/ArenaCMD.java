package com.bluecreeper111.prizearena.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.PrizeCommand;
import com.bluecreeper111.prizearena.gui.ArenaGUI;

public class ArenaCMD extends PrizeCommand {

	public static File arena = new File(plugin.getDataFolder(), "arenas.yml");
	public static YamlConfiguration arenas = YamlConfiguration.loadConfiguration(arena);
	
	public ArenaCMD() {
		super("arena", "prizearena.arena", true);
	}

	public void execute(CommandSender sender, Command cmd, String label, String[] args) {
		String result = "";
		String arg = "";

		if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
			result = "create";
			arg = args[1];
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
			result = "delete";
			arg = args[1];
		} else if ((label.equalsIgnoreCase("arenacreate") || label.equalsIgnoreCase("arcreate")) && args.length == 1) {
			result = "create";
			arg = args[0];
		} else if ((label.equalsIgnoreCase("arenadelete") || label.equalsIgnoreCase("ardelete")) && args.length == 1) {
			result = "delete";
			arg = args[0];
		} else if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) {
			result = "setspawn";
			arg = args[1];
		} else if (label.equalsIgnoreCase("arsetspawn") && args.length == 1) {
			result = "setspawn";
			arg = args[0];
		} else if ((args.length == 1 && args[0].equalsIgnoreCase("list") || label.equalsIgnoreCase("arlist"))) {
			result = "list";
		} else if ((args.length == 1 && args[0].equalsIgnoreCase("gui")) || label.equalsIgnoreCase("argui")) {
			result = "gui";
		} else if (args.length == 0) {
			result = "gui";
		} else if (args.length > 1 && args[0].equalsIgnoreCase("checklist")) {
			result = "checklist";
		}
		

		switch (result) {
		case "create":
			String name = arg;
			if (!sender.hasPermission("prizearena.arena.create")) {
				Api.noPermission(sender);
			} else {
				if (arenas.isSet("arenas." + name)) {
					sender.sendMessage(Api.getPrefix() + "§cThe arena " + name + " already exists!");
					return;
				}
				if (!((arenas.getConfigurationSection("arenas") == null)) && arenas.getConfigurationSection("arenas").getKeys(false).size() > 27) {
					sender.sendMessage(Api.getPrefix() + "§cYou have already created the maximum amount of arenas.");
					return;
				}
				arenas.set("arenas." + name, "");
				Api.saveFile(arenas, arena);
				sender.sendMessage(Api.getPrefix() + "§aArena §2" + name + "§a has been created!");
				return;
			}
		case "delete":
			String name2 = arg;
			if (!sender.hasPermission("prizearena.arena.delete")) {
				Api.noPermission(sender);
			} else {
				if (!arenas.isSet("arenas." + name2)) {
					sender.sendMessage(Api.getPrefix() + "§cArena " + name2 + " was not found!");
					return;
				}
				arenas.set("arenas." + name2, null);
				Api.saveFile(arenas, arena);
				sender.sendMessage(Api.getPrefix() + "§cArena §4" + name2 + " §chas been deleted!");
				return;
			}
		case "setspawn":
			String name3 = arg;
			if (!(sender instanceof Player)) {
				Api.notPlayer(sender);
			} else {
				Player p = (Player) sender;
				if (!p.hasPermission("prizearena.arena.setspawn")) {
					Api.noPermission(sender);
				} else {
					if (!arenas.isSet("arenas." + name3)) {
						p.sendMessage(Api.getPrefix() + "§cArena " + name3 + " was not found!");
						return;
					}
					double x = p.getLocation().getX();
					double y = p.getLocation().getY();
					double z = p.getLocation().getZ();
					double yaw = p.getLocation().getYaw();
					double pitch = p.getLocation().getPitch();
					String world = p.getWorld().getName();
					arenas.set("arenas." + name3 + ".spawn.x", x);
					arenas.set("arenas." + name3 + ".spawn.y", y);
					arenas.set("arenas." + name3 + ".spawn.z", z);
					arenas.set("arenas." + name3 + ".spawn.yaw", yaw);
					arenas.set("arenas." + name3 + ".spawn.pitch", pitch);
					arenas.set("arenas." + name3 + ".spawn.world", world);
					Api.saveFile(arenas, arena);
					p.sendMessage(Api.getPrefix() + "§aSpawn for arena §2" + name3 + "§a has been set!");
					return;
				}
			}
		case "list":
			if (!sender.hasPermission("prizearena.arena.list")) {
				Api.noPermission(sender);
			} else {
				if ((arenas.getConfigurationSection("arenas") == null) || arenas.getConfigurationSection("arenas").getKeys(false).isEmpty()) {
					sender.sendMessage(Api.getPrefix() + "§cNo arenas found!");
					return;
				}
				String text = "";
				for (String arena : arenas.getConfigurationSection("arenas").getKeys(false)) {
					text = text + arena + ", ";
				}
				sender.sendMessage(Api.getPrefix() + "§6Arenas:§e " + text);
				return;
			}
		case "gui":
			ArenaGUI argui = new ArenaGUI();
			if (!(sender instanceof Player)) {
				Api.notPlayer(sender);
				return;
			} else {
				Player p = (Player) sender;
				if (!p.hasPermission("prizearena.arena.gui")) {
					Api.noPermission(sender);
					return;
				}
				p.sendMessage(Api.getPrefix() + "§2Opening Arena GUI...");
				argui.openGui(p);
				return;
			}
		case "checklist":
			if (!arenas.isSet("arenas." + args[1])) {
				sender.sendMessage(Api.getPrefix() + "§cThat arena doesn't exist!");
				return;
			} else {
				sender.sendMessage(Api.getPrefix() + "§aChecklist for arena §2" + args[1] + "§a:");
				if (arenas.isSet("arenas." + args[1] + ".spawn.x")) {
					sender.sendMessage("§aSpawn Set: §2Y");
				} else {
					sender.sendMessage("§cSpawn Set: §4N");
				}
				if (plugin.getConfig().isSet("arenas." + args[1] + ".time-to-enable-pvp")) {
					sender.sendMessage("§aEnable-PvP Time Set: §2Y");
				} else {
					sender.sendMessage("§cEnable-PvP Time Set: §4N");
				}
				break;
				
			}
		default:
			Api.incorrectSyntax(sender, "/arena [create | delete | setspawn | list | gui | checklist] <name>", true);
			return;
		}
			

	}

}
