package com.chromaclypse.villagermod;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.chromaclypse.api.Defaults;

public class VTMExecutor implements CommandExecutor, TabExecutor {
	
	VillagerTradeMod handle;
	
	public VTMExecutor(VillagerTradeMod pHandle) {
		handle = pHandle;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("[VillagerTradeMod] Version 0.0.1");
			sender.sendMessage("[VillagerTradeMod] /vtm reload to reload");
		}
		else if(args[0].equals("reload")) {
			boolean hasPermission = sender.hasPermission("villagermod.reload");
			
			if(!hasPermission) {
				return false;
			}
			
			sender.sendMessage("[VillagerTradeMod] Reloading config");
			handle.applyConfig();
		}
		
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> result = Defaults.list("reload");
		
		if(args.length == 0) {
			return result;
		}
		if(args.length == 1) {
			if("reload".startsWith(args[0].toLowerCase())) {
				return result;
			}
		}
		
		return null;
	}

}
