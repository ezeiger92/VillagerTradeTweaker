package com.chromaclypse.villagermod;

import com.chromaclypse.villagermod.VillagerTracker.Data;

public class TradeProperties {
	private final Data data;
	
	public TradeProperties(Data data) {
		this.data = data;
	}
	
	public int getInt(String key, int bound) {
		return (data.getTrackedUUID() + key).hashCode() % bound;
	}
	
	public int getInt(String key, int bound, int minimum) {
		return minimum + getInt(key, bound - minimum);
	}
	
	public int getRandomInt(String key, int tickRefresh, int bound) {
		return getInt(key + (data.getTradeTimer() / tickRefresh), bound);
	}
	
	public int getRandomInt(String key, int tickRefresh, int bound, int minimum) {
		return minimum + getRandomInt(key, tickRefresh, bound - minimum);
	}
	
	public boolean getBool(String key) {
		return getInt(key, 2) == 1;
	}
	
	public boolean getBool(String key, double probability) {
		return getInt(key, 10000) / (double)10000 < probability;
	}
}
