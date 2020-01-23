package com.bluecreeper111.prizearena.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bluecreeper111.prizearena.action.ArenaComplete;
import com.bluecreeper111.prizearena.action.ArenaInAction;
import com.bluecreeper111.prizearena.action.ArenaStarted;
import com.bluecreeper111.prizearena.commands.ArenaCMD;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;

public class CooldownManager extends BukkitRunnable {

	public static HashMap<String, Integer> cooldowns = new HashMap<>();
	public static HashMap<String, Integer> defaultvalues = new HashMap<>();
	public static HashSet<String> resetArena = new HashSet<>();
	private ArenaStarted start = new ArenaStarted();

	public void run() {
		if (ArenaCMD.arenas.getConfigurationSection("arenas") != null
				&& ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() > 0) {
			for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
				if (resetArena.contains(arena)) {
					ArenaInAction.arenaTimer.remove(arena);
					ArenaStarted.arenasInAction.remove(arena);
					cooldowns.remove(arena);
					ArenaStarted.pvpOn.remove(arena);
					resetArena.remove(arena);
				}
				int cooldown = cooldowns.getOrDefault(arena, defaultvalues.getOrDefault(arena, 900));
				if (ArenaInAction.arenaTimer.containsKey(arena)) {
					int currenttime = ArenaInAction.arenaTimer.getOrDefault(arena, 240);
					if (currenttime == 0) {
						ArenaComplete.cancelArena(arena);
					}
					switch(currenttime) {
					case 60:
						for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
							if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
								Player pl = Bukkit.getPlayer(id);
								pl.sendMessage(Api.getPrefix() + "§6" + currenttime + "§e seconds left!");
							}
						}
						break;
					case 30:
						for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
							if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
								Player pl = Bukkit.getPlayer(id);
								pl.sendMessage(Api.getPrefix() + "§6" + currenttime + "§e seconds left!");
							}
						}
						break;
					case 10:
						for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
							if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
								Player pl = Bukkit.getPlayer(id);
								pl.sendMessage(Api.getPrefix() + "§6" + currenttime + "§e seconds left!");
							}
						}
						break;
					}
					if (currenttime < 6 && currenttime > 0) {
						for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
							if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
								Player pl = Bukkit.getPlayer(id);
								pl.sendMessage(Api.getPrefix() + "§6" + currenttime + "§e seconds left!");
							}
						}
					}
					ArenaInAction.arenaTimer.put(arena, currenttime - 1);
				}
				if (!ArenaStarted.arenasInAction.contains(arena)) {
					switch (cooldown) {
					case 120:
						Bukkit.broadcastMessage(
								Api.getPrefix() + "§6PrizeArena §e" + arena + "§6 is starting in §e2 §6minutes!");
						break;
					case 60:
						Bukkit.broadcastMessage(
								Api.getPrefix() + "§6PrizeArena §e" + arena + "§6 is starting in §e60 §6seconds!");
						break;
					case 30:
						Bukkit.broadcastMessage(
								Api.getPrefix() + "§6PrizeArena §e" + arena + "§6 is starting in §e30 §6seconds!");
						break;
					case 10:
						Bukkit.broadcastMessage(
								Api.getPrefix() + "§6PrizeArena §e" + arena + "§6 is starting in §e10 §6seconds!");
						break;
					}
					if (cooldown < 6 && cooldown > 0) {
						Bukkit.broadcastMessage(Api.getPrefix() + "§6PrizeArena §e" + arena + "§6 is starting in §e"
								+ cooldown + " §6seconds!");
					}
					if (cooldown == 0) {
						Bukkit.broadcastMessage(Api.getPrefix() + "§aPrizeArena §2" + arena + "§a has started!");
						start.startArena(arena);
					}
					cooldowns.put(arena, cooldown - 1);
				}
			}
		}
	}

}
