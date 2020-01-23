package com.bluecreeper111.prizearena.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.action.ArenaStarted;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;

public class Api {

	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', Main.prefix) + "§r ";
	}

	public static String color(String message) {
		return message.replaceAll("&", "§");
	}

	public static void noPermission(CommandSender p) {
		p.sendMessage(Api.getPrefix() + "§cYou do not have permission to do this!");
	}

	public static void incorrectSyntax(CommandSender sender, String syntax, boolean console) {
		if (console) {
			sender.sendMessage(Api.getPrefix() + "§cIncorrect syntax! Try: " + syntax);
		} else {
			Player p = (Player) sender;
			p.sendMessage(Api.getPrefix() + "§cIncorrect syntax! Try: " + syntax);
		}
	}

	public static void saveFile(YamlConfiguration config, File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void notPlayer(CommandSender sender) {
		sender.sendMessage(getPrefix() + "§cYou are not a player!");
	}

	public static void wipePlayers(List<UUID> ids, String arena) {
		for (UUID id : ids) {
			Player p = Bukkit.getPlayer(id);
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.getInventory().setContents(ArenaStarted.playerItems.get(p.getUniqueId()));
			p.getInventory().setArmorContents(ArenaStarted.playerItemsArmor.get(p.getUniqueId()));
			ArenaStarted.playerItems.remove(p.getUniqueId());
			ArenaStarted.playerItemsArmor.remove(p.getUniqueId());
			Location loc = ArenaStarted.playerLoc.get(p.getUniqueId());
			p.teleport(loc);
			ArenaStarted.playerLoc.remove(p.getUniqueId());
			PrizeJoinCMD.joinedPlayers.remove(p.getUniqueId());
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setFireTicks(0);
			p.getActivePotionEffects().clear();
		}
		ids.clear();
	}

	public static void removePlayers(List<UUID> ids, String arena) {
		for (UUID id : ids) {
			Player p = Bukkit.getPlayer(id);
			PrizeJoinCMD.joinedPlayers.remove(p.getUniqueId());
		}
		ids.clear();
	}

}
