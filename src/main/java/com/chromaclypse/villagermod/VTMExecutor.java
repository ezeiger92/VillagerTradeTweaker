package com.chromaclypse.villagermod;

import com.chromaclypse.api.command.Context;

public class VTMExecutor {
	
	VillagerTradeMod handle;
	
	public VTMExecutor(VillagerTradeMod pHandle) {
		handle = pHandle;
	}
	
	public boolean reload(Context context) {
		context.Sender().sendMessage("[VillagerTradeMod] Reloading config");
		handle.applyConfig();
		
		return true;
	}
}
