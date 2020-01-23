package com.bluecreeper111.prizearena.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.CooldownManager;
import com.bluecreeper111.prizearena.commands.ArenaCMD;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;
import com.bluecreeper111.prizearena.gui.KitGUI;

public class ArenaStarted implements Listener {
	
	public static HashSet<String> arenasInAction = new HashSet<>();
	public static HashMap<UUID, ItemStack[]> playerItems = new HashMap<>();
	public static HashMap<UUID, ItemStack[]> playerItemsArmor = new HashMap<>();
	public static HashMap<UUID, Location> playerLoc = new HashMap<>();
	public KitGUI kitgui = new KitGUI();
	public static HashMap<String, Boolean> pvpOn = new HashMap<>();
	
	public void startArena(String arena) {
		arenasInAction.add(arena);
		try {
			ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.x");
			Main.instance.getConfig().getInt("arenas." + arena + ".time-to-enable-pvp");
			Main.instance.getConfig().getConfigurationSection("kits");
		} catch (Exception e) {
			Bukkit.broadcastMessage(Api.getPrefix() + "§cArena " + arena + " could not be started. Missing some configurations!");
			Bukkit.getLogger().warning("Error with PrizeArena " + arena + ": Not all settings have been set! (Spawn, Enable-PvP Time, Kits)");
			CooldownManager.resetArena.add(arena);
			for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
				if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
					PrizeJoinCMD.joinedPlayers.remove(id);
				}
			}
			return;
		}
		int size = 0;
		for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
			if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
				size++;
				continue;
			}
		}
		if (size < 2) {
			Bukkit.broadcastMessage(Api.getPrefix() + "§cArena §4" + arena + " §ccould not be started. A PrizeArena requires more than 1 player to start!");
			CooldownManager.resetArena.add(arena);
			for (Iterator<UUID> iterator = PrizeJoinCMD.joinedPlayers.keySet().iterator(); iterator.hasNext();) {
				UUID id = iterator.next();
				if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
					iterator.remove();
				}
			}
			return;
		}
		double x = ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.x");
		double y = ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.y");
		double z = ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.z");
		double pitch = ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.pitch");
		double yaw = ArenaCMD.arenas.getDouble("arenas." + arena + ".spawn.yaw");
		String world = ArenaCMD.arenas.getString("arenas." + arena + ".spawn.world");
		Location spawn = new Location(Bukkit.getWorld(world), x, y, z);
		spawn.setPitch((float)pitch);
		spawn.setYaw((float)yaw);
		for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
			Player p = Bukkit.getPlayer(id);
			Inventory inv = p.getInventory();
			playerItems.put(p.getUniqueId(), inv.getContents());
			playerItemsArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());
			playerLoc.put(p.getUniqueId(), p.getLocation());
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.teleport(spawn);
			try {
			p.sendMessage(Api.getPrefix() + ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("arenas." + arena + ".startMessage")));
			} catch (Exception e) {
				p.sendMessage(Api.getPrefix() + "§6Its time to fight! Last one standing wins.");
			}
			p.sendMessage(Api.getPrefix() + "§aYou will receive your kits and PvP will be enabled in: §2" + Main.instance.getConfig().getInt("arenas." + arena + ".time-to-enable-pvp") + " seconds");
			kitgui.openGui(p);
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setFireTicks(0);
			p.getActivePotionEffects().clear();
		}
		pvpOn.put(arena, false);
		ArenaInAction.inAction(arena);
	}
	@EventHandler
	public void leave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId())) {
			if (arenasInAction.contains(PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()))) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				p.getInventory().setContents(ArenaStarted.playerItems.get(p.getUniqueId()));
				p.getInventory().setArmorContents(ArenaStarted.playerItemsArmor.get(p.getUniqueId()));
				ArenaStarted.playerItems.remove(p.getUniqueId());
				ArenaStarted.playerItemsArmor.remove(p.getUniqueId());
				Location loc = playerLoc.get(p.getUniqueId());
				p.teleport(loc);
				playerLoc.remove(p.getUniqueId());
				PrizeJoinCMD.joinedPlayers.remove(p.getUniqueId());
			}
		}
	}

}
