package me.lim_bo56.lnpp.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lim_bo56.lnpp.menus.PlayerPreferences;
import me.lim_bo56.lnpp.utils.AllStrings;

/**
 * 
 * @author lim_bo56
 *
 */

public class PreferencesPlayer implements CommandExecutor {

	 @Override
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
		 
		String world = p.getWorld().getName();	
		 
		 for(String m : AllStrings.getInstance().World) {
		if(world.equalsIgnoreCase(m)) { 
		 if(label.equalsIgnoreCase("preferencesPlayer")) {
			 PlayerPreferences.openPlayerPreferences(p);
		 }
		} 
		 }
		 
		 return false;	 
	 }
}
