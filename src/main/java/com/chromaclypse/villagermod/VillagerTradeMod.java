package com.chromaclypse.villagermod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chromaclypse.api.Defaults;

public class VillagerTradeMod extends JavaPlugin implements Listener {
	Map<UUID, Map<Integer, Integer>> trade_backups = Defaults.<UUID>Keys().Values();
	
	VillagerTradeConfig config = new VillagerTradeConfig();
	VTMExecutor command;

	@Override
	public void onEnable() {
		command = new VTMExecutor(this);
		if(!getDataFolder().exists())
			saveDefaultConfig();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(this, this);
		
		PluginCommand plug = getCommand("villagertrademod");
		plug.setAliases(Defaults.List("vtm"));
		plug.setExecutor(command);
		
		applyConfig();
	}
	
	private void closeModifiableTrades() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			InventoryHolder holder = p.getOpenInventory().getTopInventory().getHolder();
			if(!(holder instanceof Villager))
				continue;
			
			Villager merch = (Villager)holder;
			for(int i = 0; i < merch.getRecipeCount(); ++i) {
				MerchantRecipe mr = merch.getRecipe(i);
				if(mr.getResult().getType() == Material.ENCHANTED_BOOK) {
					p.closeInventory();
					resetTrades(merch);
					break;
				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		closeModifiableTrades();
	}
	
	public void applyConfig() {
		closeModifiableTrades();
		config.init(this);
	}
	
	private void resetTrades(Villager merch) {
		UUID merch_id = merch.getUniqueId();
		Map<Integer, Integer> backup = trade_backups.get(merch_id);
		if(backup == null)
			return;
		
		for(Map.Entry<Integer, Integer> pair : backup.entrySet()) {
			MerchantRecipe mr = merch.getRecipe(pair.getKey());
			if(mr.getResult().getType() != Material.ENCHANTED_BOOK)
				continue;
			
			List<ItemStack> ing = mr.getIngredients();
			ing.get(1).setAmount(pair.getValue());
			mr.setIngredients(ing);
			merch.setRecipe(pair.getKey(), mr);
		}
		trade_backups.remove(merch_id);
	}
	
	@EventHandler
	public void onCloseTrade(InventoryCloseEvent event) {
		InventoryHolder ih = event.getInventory().getHolder();
		if(!(ih instanceof Villager) || !config.enabled)
			return;

		Villager merch = (Villager)ih;
		resetTrades(merch);
	}
	
	@EventHandler
	public void onOpenTrade(InventoryOpenEvent event) {
		InventoryHolder ih = event.getInventory().getHolder();
		if(!(ih instanceof Villager) || !config.enabled)
			return;

		Villager merch = (Villager)ih;
		UUID merch_id = merch.getUniqueId();
		Map<Integer, Integer> backup = Defaults.<Integer>Keys().Values();
		boolean modified = false;
		
		for(int i = 0; i < merch.getRecipeCount(); ++i)
		{
			MerchantRecipe mr = merch.getRecipe(i);
			if(mr.getResult().getType() == Material.ENCHANTED_BOOK)
			{
				EnchantmentStorageMeta book = (EnchantmentStorageMeta) mr.getResult().getItemMeta();
				int val = 0;
				for(Map.Entry<Enchantment, Integer> e : book.getStoredEnchants().entrySet())
				{
					int scaledCost;
					Integer customCost = config.customCosts.get(e.getKey().getKey().getKey());
					if(customCost != null)
						scaledCost = customCost.intValue() >> (e.getKey().getMaxLevel() - e.getValue());
					else
						scaledCost = config.baseCost >> (e.getKey().getMaxLevel() - e.getValue());

					val += Math.max(scaledCost, 1);
				}
				val = Math.min(val, 64);
				
				List<ItemStack> ing = mr.getIngredients();
				backup.put(i, ing.get(1).getAmount());
				ing.get(1).setAmount(val);
				mr.setIngredients(ing);
				merch.setRecipe(i, mr);
				modified = true;
			}
		}
		
		if(modified)
			trade_backups.put(merch_id, backup);
	}
}
