package com.bluecreeper111.prizearena.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;
import net.milkbowl.vault.economy.EconomyResponse;

public class KitGUI implements Listener {

	public static HashMap<UUID, String> lockedKit = new HashMap<>();

	public void openGui(Player p) {
		Main plugin = Main.instance;
		if (plugin.getConfig().getConfigurationSection("kits") == null
				|| plugin.getConfig().getConfigurationSection("kits").getKeys(false).size() == 0) {
			return;
		}
		int slots = plugin.getConfig().getConfigurationSection("kits").getKeys(false).size();
		if (slots <= 9) {
			slots = 9;
		} else if (slots > 9 && slots <= 18) {
			slots = 18;
		} else if (slots > 18 && slots <= 27) {
			slots = 27;
		} else if (slots > 27 && slots <= 36) {
			slots = 36;
		} else if (slots > 36 && slots <= 45) {
			slots = 45;
		} else {
			slots = 54;
		}
		Inventory kitinv = Bukkit.createInventory(null, slots, "§e§lKits");
		for (String kit : plugin.getConfig().getConfigurationSection("kits").getKeys(false)) {
			ItemStack add;
			String[] displayitem = plugin.getConfig().getString("kits." + kit + ".displayitem").split(",");
			int displaylength = displayitem.length;
			switch (displaylength) {
			case 3:
				add = new ItemStack(Material.getMaterial(displayitem[0].trim()),
						Integer.parseInt(displayitem[1].trim()));
				if (add instanceof Damageable) {
					Damageable d = (Damageable) add.getItemMeta();
					d.setDamage(Integer.parseInt(displayitem[2].trim()));
					add.setItemMeta((ItemMeta) d);
				}
				break;
			case 2:
				add = new ItemStack(Material.getMaterial(displayitem[0].trim()),
						Integer.parseInt(displayitem[1].trim()));
				break;
			case 1:
				add = new ItemStack(Material.getMaterial(displayitem[0].trim()), 1);
				break;
			default:
				add = new ItemStack(Material.BARRIER, 1);
			}
			String displayname = plugin.getConfig().getString("kits." + kit + ".displayname");
			int cost = plugin.getConfig().getInt("kits." + kit + ".cost");
			displayname = ChatColor.translateAlternateColorCodes('&', displayname);
			if (displayname == null) {
				displayname = kit;
			}
			if (Integer.valueOf(cost) == null) {
				cost = 0;
			}
			ItemMeta meta = add.getItemMeta();
			meta.setDisplayName(displayname);
			ArrayList<String> lore = new ArrayList<>();
			if (cost > 0) {
				lore.add("§2§lCost: §r§a$" + cost);
			} else {
				lore.add("§2§lCost: §r§aFree");
			}
			lore.add(" ");
			if (plugin.getConfig().getBoolean("kits." + kit + ".default")) {
				lore.add("§4§oDefault Kit");
			}
			meta.setLore(lore);
			add.setItemMeta(meta);
			kitinv.addItem(add);
		}
		p.openInventory(kitinv);
	}

	@EventHandler
	public void invenClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		String inv = e.getInventory().getName();
		ItemStack clicked = e.getCurrentItem();
		String currency = Main.getEconomy().currencyNameSingular();
		if (clicked == null || clicked.getType() == Material.AIR) {
			return;
		}
		if (Main.instance.getConfig().getConfigurationSection("kits") == null) {
			return;
		}
		if (!inv.equals("§e§lKits")) {
			return;
		}
		for (String kit : Main.instance.getConfig().getConfigurationSection("kits").getKeys(false)) {
			if (Main.instance.getConfig().getString("kits." + kit + ".displayname") == null) {
				if (kit.equals(clicked.getItemMeta().getDisplayName())) {
					e.setCancelled(true);
					double cost = Main.instance.getConfig().getInt("kits." + kit + ".cost");
					EconomyResponse take = Main.getEconomy().withdrawPlayer(p, cost);
					if (Double.valueOf(cost) == null) {
						cost = 0;
					}
					if (take.transactionSuccess()) {
						p.sendMessage(Api.getPrefix() + "§aYou have purchased the kit §2" + kit + "§a for §2" + cost
								+ currency);
						lockedKit.put(p.getUniqueId(), kit);
						p.closeInventory();
					} else {
						p.sendMessage(Api.getPrefix() + "§cYou do not have enough money!");
						return;
					}
				}
			} else {
				String dn = Api.color(Main.instance.getConfig().getString("kits." + kit + ".displayname"));
				if (dn.equals(clicked.getItemMeta().getDisplayName())) {
					e.setCancelled(true);
					double cost = Main.instance.getConfig().getInt("kits." + kit + ".cost");
					EconomyResponse take = Main.getEconomy().withdrawPlayer(p, cost);
					if (Double.valueOf(cost) == null) {
						cost = 0;
					}
					if (take.transactionSuccess()) {
						p.sendMessage(Api.getPrefix() + "§aYou have purchased the kit §2" + kit + "§a for §2" + cost
								+ currency);
						lockedKit.put(p.getUniqueId(), kit);
						p.closeInventory();
					} else {
						p.sendMessage(Api.getPrefix() + "§cYou do not have enough money!");
						return;
					}

				}
			}
		}

	}

}
