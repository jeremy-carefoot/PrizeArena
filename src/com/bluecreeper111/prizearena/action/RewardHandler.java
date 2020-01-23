package com.bluecreeper111.prizearena.action;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;

public class RewardHandler {
	
	public static Main plugin;

	public void giveReward(UUID id) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			ConfigurationSection rewards = Main.instance.getConfig().getConfigurationSection("rewards");
			FileConfiguration config = Main.instance.getConfig();
			Player p = Bukkit.getPlayer(id);
			double totalpercent = 0;
			public void run() {
				for (String reward : rewards.getKeys(false)) {
					totalpercent = totalpercent + config.getDouble("rewards." + reward + ".percentage");
				}
				if (totalpercent != 100) {
					p.sendMessage(Api.getPrefix()
							+ "§cCould not give reward. Please contact an administrator. (Error: Configuration Error) \n Logged to console.");
					Main.instance.getLogger()
							.severe("Could not give player " + p.getName() + " reward from PrizeArena.");
					Main.instance.getLogger().severe(
							"Error: Reward percentages do not add up to 100! Please check reward configuration.");
					return;
				}
				String reward = getReward(rewards);
				while (reward == null) {
					reward = getReward(rewards);
				}
				if (Main.instance.getConfig().getString("rewards." + reward + ".type").equalsIgnoreCase("item")) {
					String type = Main.instance.getConfig().getString("rewards." + reward + ".item").toUpperCase();
					int amount = Main.instance.getConfig().getInt("rewards." + reward + ".amount");
					int data = Main.instance.getConfig().getInt("rewards." + reward + ".item-data");
					if (data == -1) {
						ItemStack give = new ItemStack(Material.getMaterial(type), amount);
						if (p.getInventory().firstEmpty() == -1) {
							p.getWorld().dropItem(p.getLocation(), give);
						} else {
							p.getInventory().addItem(give);
						}
						p.sendMessage(
								Api.getPrefix() + "§eYou have won the reward §2" + reward + "§e from the PrizeArena!");
						return;
					}
					@SuppressWarnings("deprecation")
					ItemStack give = new ItemStack(Material.getMaterial(type), amount, (short) data);
					if (p.getInventory().firstEmpty() == -1) {
						p.getWorld().dropItem(p.getLocation(), give);
					} else {
						p.getInventory().addItem(give);
					}
					p.sendMessage(
							Api.getPrefix() + "§eYou have won the reward §2" + reward + "§e from the PrizeArena!");
					return;
				} else {
					for (String command : Main.instance.getConfig().getStringList("rewards." + reward + ".execute")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", p.getName()));
					}
					p.sendMessage(
							Api.getPrefix() + "§eYou have won the reward §2" + reward + "§e from the PrizeArena!");
					return;
				}
			}
		}, 20L);
	}

	public String getReward(ConfigurationSection rewards) {
		int random = new Random().nextInt(100);
		int counter = 0;
		for (String reward : rewards.getKeys(false)) {
			double chance = Main.instance.getConfig().getDouble("rewards." + reward + ".percentage");
			if (random < (chance + counter) && random >= counter) {
				return reward;
			}
			counter += chance;
		}
		return null;
	}

}
