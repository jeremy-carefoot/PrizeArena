package com.bluecreeper111.prizearena;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.bluecreeper111.prizearena.action.ArenaActionHandler;
import com.bluecreeper111.prizearena.action.ArenaStarted;
import com.bluecreeper111.prizearena.action.RewardHandler;
import com.bluecreeper111.prizearena.api.CooldownManager;
import com.bluecreeper111.prizearena.api.PrizeCommand;
import com.bluecreeper111.prizearena.commands.ArenaCMD;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;
import com.bluecreeper111.prizearena.gui.ArenaGUI;
import com.bluecreeper111.prizearena.gui.KitGUI;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public static String prefix;
	public static Main instance;
	private static Economy econ;

	public void onEnable() {
		RewardHandler.plugin = this;
		registerStrings();
		registerCommands();
		registerConfig();
		registerEvents();
		if (setupEconomy()) {
			getLogger().info("Vault implementation successfull");
		} else {
			getLogger().severe("Error: Could not enable economy with Vault.");
			getLogger().severe("Make sure you have a vault-supported economy plugin installed!");
		}
		getLogger().info("Successfully enabled version V." + getDescription().getVersion());
	}

	public void onDisable() {
		giveItems();
	}

	@SuppressWarnings("unused")
	private void registerCommands() {
		BukkitTask cooldown = new CooldownManager().runTaskTimer(this, 20, 20);
		PrizeCommand.registerCommands(this);
	}

	private void registerStrings() {
		prefix = getConfig().getString("prefix");
		instance = this;

	}

	private void registerConfig() {
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			saveResource("config.yml", false);

		}
		if (!ArenaCMD.arena.exists()) {
			try {
				ArenaCMD.arena.createNewFile();
				ArenaCMD.arenas.addDefault("importantnote",
						"If you are reading this, please remember NOT to edit this file. All configuration can be done in the config.yml.");
				ArenaCMD.arenas.options().copyDefaults();
				ArenaCMD.arenas.save(ArenaCMD.arena);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ArenaCMD.arenas.getConfigurationSection("arenas") != null
				&& ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() > 0) {
			for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
				int i = getConfig().getInt("arenas." + arena + ".time-between");
				CooldownManager.defaultvalues.put(arena, i);
			}
		}
	}

	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ArenaGUI(), this);
		pm.registerEvents(new KitGUI(), this);
		pm.registerEvents(new ArenaStarted(), this);
		pm.registerEvents(new ArenaActionHandler(), this);
	}

	private void giveItems() {
		if (ArenaCMD.arenas.getConfigurationSection("arenas") != null
				&& ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() > 0) {
			for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
				Player p = Bukkit.getPlayer(id);
				String arena = PrizeJoinCMD.joinedPlayers.get(id);
				if (ArenaStarted.arenasInAction.contains(arena)) {
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
					p.getInventory().setContents(ArenaStarted.playerItems.get(p.getUniqueId()));
					p.getInventory().setArmorContents(ArenaStarted.playerItemsArmor.get(p.getUniqueId()));
					ArenaStarted.playerItems.remove(p.getUniqueId());
					ArenaStarted.playerItemsArmor.remove(p.getUniqueId());
					Location loc = ArenaStarted.playerLoc.get(p.getUniqueId());
					p.teleport(loc);
					ArenaStarted.playerLoc.remove(p.getUniqueId());
				}
			}
		}
	}
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return true;
		
	}
	public static Economy getEconomy() {
		return econ;
	}

}
