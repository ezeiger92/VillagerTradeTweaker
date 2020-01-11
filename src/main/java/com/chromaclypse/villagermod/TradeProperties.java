package com.chromaclypse.villagermod;

import org.bukkit.entity.Villager;

public class TradeProperties {
	private Villager villager;
	
	public TradeProperties(Villager villager) {
		this.villager = villager;
	}
	
	private int getInt(String key, int limit) {
		return (villager.getUniqueId() + key).hashCode() % limit; // Use tracked UUID instead
	}
	
	private int getVaryingInt(String key, int scale, int limit) {
		return getInt(key + (villager.getTicksLived() / scale), limit);
	}
	
	private int getTimedInt(String key, int scale, int limit) {
		return (villager.getTicksLived() / scale) % limit;
	}
	
	private int getInt(String key, int lowerBase, int upperBase, int lowerRange, int upperRange, int timeScale) {
		int base = getInt(key + "-base", upperBase - lowerBase) + lowerBase;
		int range = getInt(key + "-range", upperRange - lowerRange) + lowerRange;
		
		return getVaryingInt(key, timeScale, range) + base;
	}
	
	private boolean getBool(String key) {
		return getBool(key, 2, 1);
	}
	
	private boolean getBool(String key, int maximum, int required) {
		return getInt(key, maximum) >= required;
	}
	//12e7e477-cb7b-455e-a998-650aa6d9b007
	//e64d21fe-e720-411f-9d07-b4f1424e3af5
	//e64d21fe-e720-411f-9d07-b4f1424e3af5
	
}
