package com.morkaz.moxchattitles.listeners;

import com.morkaz.moxchattitles.MoxChatTitles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

	private MoxChatTitles main;

	public JoinListener(MoxChatTitles main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority= EventPriority.HIGHEST)
	public void joinListener(PlayerJoinEvent e){
		String playerID = Bukkit.getOnlineMode() ? e.getPlayer().getUniqueId().toString() : e.getPlayer().getName().toLowerCase();
		if (!main.getDataManager().isPlayersDataLoaded(playerID)){
			main.getDataManager().loadPlayerData(e.getPlayer());
		}
	}


}
