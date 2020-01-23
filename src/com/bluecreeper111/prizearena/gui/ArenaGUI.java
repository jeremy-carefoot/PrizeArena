package com.bluecreeper111.prizearena.gui;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.commands.ArenaCMD;

public class ArenaGUI implements Listener {
	
	public void openGui(Player p) {
		if ((ArenaCMD.arenas.getConfigurationSection("arenas") == null) || ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).isEmpty()) {
			p.sendMessage(Api.getPrefix() + "§cThere are no arenas to manage!");
			return;
		}
		Inventory arenagui = Bukkit.createInventory(null, 27, "§4§lArenas");
		for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
			ItemStack add = new ItemStack(Material.REDSTONE, 1);
			ItemMeta addm = add.getItemMeta();
			ArrayList<String> lore = new ArrayList<>();
			addm.setDisplayName("§e" + arena);
			lore.add("§7Manage this arena!");
			addm.setLore(lore);
			add.setItemMeta(addm);
			arenagui.addItem(add);
		}
		p.openInventory(arenagui);
	}
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		String inv = e.getInventory().getName();
		ItemStack clicked = e.getCurrentItem();
		if (clicked == null || clicked.getType() == Material.AIR) { return; }
		if (inv.equals("§4§lArenas")) {
			for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
				if (clicked.getItemMeta().getDisplayName().equals("§e" + arena)) {
					ItemStack add = new ItemStack(Material.GHAST_SPAWN_EGG, 1);
					ItemMeta addm = add.getItemMeta();
					addm.setDisplayName("§8Set Arena Spawn");
					ArrayList<String> lore = new ArrayList<>();
					lore.add("§7Set your current location as the arena spawn!");
					addm.setLore(lore);
					add.setItemMeta(addm);
					ItemStack add3 = new ItemStack(Material.BARRIER, 1);
					ItemMeta addm3 = add3.getItemMeta();
					addm3.setDisplayName("§4Delete Arena");
					ArrayList<String> lore3 = new ArrayList<>();
					lore3.add("§7Remove this arena!");
					addm3.setLore(lore3);
					add3.setItemMeta(addm3);
					ItemStack add4 = new ItemStack(Material.ARROW, 1);
					ItemMeta addm4 = add4.getItemMeta();
					addm4.setDisplayName("§cGo Back");
					ArrayList<String> lore4 = new ArrayList<>();
					lore4.add("§7Return to last page");
					addm4.setLore(lore4);
					add4.setItemMeta(addm4);
					Inventory newinv = Bukkit.createInventory(null, 9, "§e" + arena);
					newinv.addItem(add);
					newinv.addItem(add3);
					newinv.setItem(8, add4);
					e.setCancelled(true);
					p.closeInventory();
					p.openInventory(newinv);
				} 
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void inGuiClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		String invName = e.getInventory().getName();
		if (clicked == null || (clicked.getType() == Material.AIR)) { return; }
		if (ArenaCMD.arenas.getConfigurationSection("arenas") == null) { return; }
		for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
			if (invName.equals("§e" + arena)) {
				if (clicked.getItemMeta().getDisplayName().equals("§8Set Arena Spawn")) {
					e.setCancelled(true);
					p.closeInventory();
					Bukkit.dispatchCommand(p, "arena setspawn " + arena);
					return;
				}
				if (clicked.getItemMeta().getDisplayName().equals("§4Delete Arena")) {
					e.setCancelled(true);
					p.closeInventory();
					Bukkit.dispatchCommand(p, "arena delete " + arena);
					return;
				}
				if (clicked.getItemMeta().getDisplayName().equals("§cGo Back")) {
					e.setCancelled(true);
					p.closeInventory();
					this.openGui(p);
					return;
				}
				return;
			}
		}
	}
	

}
