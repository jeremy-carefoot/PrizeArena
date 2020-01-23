package com.bluecreeper111.prizearena.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.PrizeCommand;

public class PAKitCMD extends PrizeCommand {
	
	public PAKitCMD() {
		super("pakit", "prizearena.kit", false);
	}
	
	public void execute(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if (args.length == 2 && args[0].equalsIgnoreCase("setitems")) {
			String kit = args[1];
			if (!p.hasPermission("prizearena.kit.setitems")) {
				Api.noPermission(p);
				return;
			} else {
				if (plugin.getConfig().isSet("kits." + kit)) {
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					for (int i = 0; i < 36; i++) {
						ItemStack item = p.getInventory().getItem(i);
						if (item == null || item.getType() == Material.AIR) { items.add(null); continue; }
						items.add(item);
					}
					ItemStack[] add = items.toArray(new ItemStack[items.size()]);
					ArenaCMD.arenas.set("kits." + kit + ".items", add);
					ArenaCMD.arenas.set("kits." + kit + ".armor.helmet", p.getInventory().getHelmet() != null ? p.getInventory().getHelmet() : "air");
					ArenaCMD.arenas.set("kits." + kit + ".armor.chestplate", p.getInventory().getChestplate() != null ? p.getInventory().getChestplate() : "air");
					ArenaCMD.arenas.set("kits." + kit + ".armor.leggings", p.getInventory().getLeggings() != null ? p.getInventory().getLeggings() : "air");
					ArenaCMD.arenas.set("kits." + kit + ".armor.boots", p.getInventory().getBoots() != null ? p.getInventory().getBoots() : "air");
					Api.saveFile(ArenaCMD.arenas, ArenaCMD.arena);
					p.sendMessage(Api.getPrefix() + "§aItems have been set for kit §2" + kit);
				} else {
					p.sendMessage(Api.getPrefix() + "§cThat kit was not found! Make sure its in the config.yml!");
					return;
				}
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			String kits = "";
			for (String kit : plugin.getConfig().getConfigurationSection("kits").getKeys(false)) {
				kits = kits + kit + ", ";
			}
			if (p.hasPermission("prizearena.kit.list")) {
				p.sendMessage(Api.getPrefix() + "§6List of current kits: §e" + kits);
				return;
			} else {
				Api.noPermission(p);
				return;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("view")) {
			String kit = args[1];
			if (p.hasPermission("prizearena.kit.view")) {
				if (plugin.getConfig().isSet("kits." + kit)) {
					String defaultkit = "";
					String cost = "";
					String displayname = "";
					if (plugin.getConfig().isSet("kits." + kit + ".default")) {
						defaultkit = Boolean.toString(plugin.getConfig().getBoolean("kits." + kit + ".default"));
					} else {
						defaultkit = "§4Not Set";
					}
					if (plugin.getConfig().isSet("kits." + kit + ".cost")) {
						cost = Double.toString(plugin.getConfig().getDouble("kits." + kit + ".cost"));
					} else {
						cost = "§4Not Set";
					}
					if (plugin.getConfig().isSet("kits." + kit + ".displayname")) {
						displayname = plugin.getConfig().getString("kits." + kit + ".displayname");
						displayname = ChatColor.translateAlternateColorCodes('&', displayname);
					} else {
						displayname = "§4Not Set";
					}
					p.sendMessage(Api.getPrefix() + "§6Kit §e" + kit + "§6: \n§eDefault Kit: §7" + defaultkit + "\n§eCost: §7" + cost + "\n§eDisplay Name: §r" + displayname);
					return;
				} else {
					p.sendMessage(Api.getPrefix() + "§cThat kit was not found! Make sure its in the config.yml!");
					return;
				}
			} else {
				Api.noPermission(p);
				return;
			}
		} else {
			Api.incorrectSyntax(p, "/pakit [setitems : view : list] <kit>", false);
			return;
		}
		
	}

}
