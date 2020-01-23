package com.bluecreeper111.prizearena.action;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;

public class ArenaActionHandler implements Listener {
	
	public HashMap<UUID, String> died = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void damage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			String arena = PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()) != null
					? PrizeJoinCMD.joinedPlayers.get(p.getUniqueId())
					: null;
			if (arena == null) {
				return;
			}
			if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId()) && ArenaStarted.arenasInAction.contains(arena)
					&& !ArenaStarted.pvpOn.get(arena)) {
				e.setCancelled(true);
				damager.sendMessage(Api.getPrefix() + "§cPvP is not enabled yet!");
				return;
			}
		}
	}

	@EventHandler
	public void breakb(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId())
				&& ArenaStarted.arenasInAction.contains(PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()))) {
			e.setCancelled(true);
			p.sendMessage(Api.getPrefix() + "§cCannot build while in a PrizeArena!");
			return;
		}
	}
	@EventHandler
	public void placeb(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId())
				&& ArenaStarted.arenasInAction.contains(PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()))) {
			e.setCancelled(true);
			p.sendMessage(Api.getPrefix() + "§cCannot build while in a PrizeArena!");
			return;
		}
	}
	@EventHandler
	public void sendcmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId())
				&& ArenaStarted.arenasInAction.contains(PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()))) {
			e.setCancelled(true);
			p.sendMessage(Api.getPrefix() + "§cCannot use commands while in a PrizeArena!");
			return;
		}
	}
	@EventHandler
	public void dead(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId()) && ArenaStarted.arenasInAction.contains(PrizeJoinCMD.joinedPlayers.get(p.getUniqueId()))) {
			String arena = PrizeJoinCMD.joinedPlayers.get(p.getUniqueId());
			ArenaInAction.playerDied(arena, p.getUniqueId());
			died.put(p.getUniqueId(), arena);
			e.getDrops().clear();
		}
	}
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (died.containsKey(p.getUniqueId())) {
			String arena = died.get(p.getUniqueId());
			String message = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("arenas." + arena + ".dieMessage"));
			p.sendMessage(Api.getPrefix() + message);
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.getInventory().setContents(ArenaStarted.playerItems.get(p.getUniqueId()));
			p.getInventory().setArmorContents(ArenaStarted.playerItemsArmor.get(p.getUniqueId()));
			ArenaStarted.playerItems.remove(p.getUniqueId());
			ArenaStarted.playerItemsArmor.remove(p.getUniqueId());
			Location loc = ArenaStarted.playerLoc.get(p.getUniqueId());
			e.setRespawnLocation(loc);
			ArenaStarted.playerLoc.remove(p.getUniqueId());
			died.remove(p.getUniqueId());
		}
		
	}

}
