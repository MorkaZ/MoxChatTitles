package com.morkaz.moxchattitles.listeners;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

	private MoxChatTitles main;

	public QuitListener(MoxChatTitles main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority= EventPriority.HIGHEST)
	public void quitListener(PlayerQuitEvent e){
		String playerID = Bukkit.getOnlineMode() ? e.getPlayer().getUniqueId().toString() : e.getPlayer().getName().toLowerCase();
		PlayerData playerData = main.getDataManager().getPlayerData(playerID);
		if (playerData == null){
			return;
		}
		if (!playerData.isInDatabase()){
			main.getDataManager().unloadPlayerData(playerID);
		}
	}
}
