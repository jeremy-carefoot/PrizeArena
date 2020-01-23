package com.bluecreeper111.prizearena.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.commands.ArenaCMD;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;
import com.bluecreeper111.prizearena.gui.KitGUI;

public class ArenaInAction {
	
	public static HashMap<String, Integer> arenaTimer = new HashMap<String, Integer>();
	
	public static void inAction(String arena) {
		long starttime = Main.instance.getConfig().getInt("arenas." + arena + ".time-to-enable-pvp") * 20L;
		int endseconds = Main.instance.getConfig().getInt("arenas." + arena + ".time-to-stop");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
			public void run() {
				for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
					if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
						Player p = Bukkit.getPlayer(id);
						p.sendMessage(Api.getPrefix() + "§4§lPvP Enabled! Go!");
						if (KitGUI.lockedKit.containsKey(id)) {
							String kit = KitGUI.lockedKit.get(id);
							p.closeInventory();
							YamlConfiguration arenas = ArenaCMD.arenas;
							String path = "kits." + kit + ".";
							@SuppressWarnings("unchecked")
							ArrayList<ItemStack> is = (ArrayList<ItemStack>) arenas.get(path + ".items");
							ItemStack[] items = is.toArray(new ItemStack[is.size()]);
							for (ItemStack item : items) {
								if (item == null || item.getType() == Material.AIR) {
									continue;
								}
								p.getInventory().addItem(item);
							}
							p.getInventory()
									.setHelmet(arenas.get(path + ".armor.helmet") != "air"
											? arenas.getItemStack(path + ".armor.helmet")
											: null);
							p.getInventory()
									.setChestplate(arenas.get(path + ".armor.chestplate") != "air"
											? arenas.getItemStack(path + ".armor.chestplate")
											: null);
							p.getInventory()
									.setLeggings(arenas.get(path + ".armor.leggings") != "air"
											? arenas.getItemStack(path + ".armor.leggings")
											: null);
							p.getInventory()
									.setBoots(arenas.get(path + ".armor.boots") != "air"
											? arenas.getItemStack(path + ".armor.boots")
											: null);
							p.updateInventory();
							continue;
						} else {
							for (String kit : Main.instance.getConfig().getConfigurationSection("kits").getKeys(false)) {
								if (Main.instance.getConfig().getBoolean("kits." + kit + ".default")) {
									p.closeInventory();
									p.sendMessage(Api.getPrefix() + "§7(You did not choose a kit and were given the default one!)");
									YamlConfiguration arenas = ArenaCMD.arenas;
									String path = "kits." + kit + ".";
									@SuppressWarnings("unchecked")
									ArrayList<ItemStack> is = (ArrayList<ItemStack>) arenas.get(path + ".items");
									ItemStack[] items = is.toArray(new ItemStack[is.size()]);
									for (ItemStack item : items) {
										if (item == null || item.getType() == Material.AIR) {
											continue;
										}
										p.getInventory().addItem(item);
									}
									p.getInventory()
											.setHelmet(arenas.get(path + ".armor.helmet") != "air"
													? arenas.getItemStack(path + ".armor.helmet")
													: null);
									p.getInventory()
											.setChestplate(arenas.get(path + ".armor.chestplate") != "air"
													? arenas.getItemStack(path + ".armor.chestplate")
													: null);
									p.getInventory()
											.setLeggings(arenas.get(path + ".armor.leggings") != "air"
													? arenas.getItemStack(path + ".armor.leggings")
													: null);
									p.getInventory()
											.setBoots(arenas.get(path + ".armor.boots") != "air"
													? arenas.getItemStack(path + ".armor.boots")
													: null);
									p.updateInventory();
									break;
								}
							}
						}
					}
				}
				ArenaStarted.pvpOn.put(arena, true);
				arenaTimer.put(arena, endseconds);
			}
		}, starttime);
	 
	}
	public static void playerDied(String arena, UUID id) {
		List<UUID> remove = new ArrayList<>();
		remove.add(id);
		Api.removePlayers(remove, arena);
		int size = 0;
		for (UUID fill : PrizeJoinCMD.joinedPlayers.keySet()) {
			if (PrizeJoinCMD.joinedPlayers.get(fill).equals(arena)) {
				size++;
				continue;
			}
		}
		for (UUID idd : PrizeJoinCMD.joinedPlayers.keySet()) {
			Player pl = Bukkit.getPlayer(idd);
			if (PrizeJoinCMD.joinedPlayers.get(idd).equals(arena)) {
				if (size == 1) {
					ArenaComplete.finishArena(arena);
					break;
				}
				pl.sendMessage(Api.getPrefix() + "§eThere are §6" + size + "§e players left!");
			}
		}
	}

}
