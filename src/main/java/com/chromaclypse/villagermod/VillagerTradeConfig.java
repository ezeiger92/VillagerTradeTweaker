package com.chromaclypse.villagermod;

import java.util.Map;

import com.chromaclypse.api.Defaults;
import com.chromaclypse.api.config.ConfigObject;

public class VillagerTradeConfig extends ConfigObject {

	public int baseCost = 64;
	public boolean enabled = true;
	public Map<String, Integer> customCosts = Defaults.keys(
			"at max level, this enchant costs ->"
		).values(
			12
		);
}
