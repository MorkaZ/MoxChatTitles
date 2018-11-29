package com.morkaz.moxchattitles.listeners;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	private MoxChatTitles main;

	public ChatListener(MoxChatTitles main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority= EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		String playerID = main.getDataManager().getPlayerID(e.getPlayer());
		PlayerData playerData = main.getDataManager().getPlayerData(playerID);
		ChatTitle chatTitle = playerData.getLastTitle();
		String chatFormat = e.getFormat();
		if (chatTitle == null && main.getConfig().getBoolean("default-title.enabled")){
			chatTitle = main.getDataManager().getDefaultTitle();
		}
		if (chatTitle != null) {
			if (chatFormat.contains("{mox_chattitle}")) {
				e.setFormat(chatFormat.replace("{mox_chattitle}", chatTitle.getTitle()));
			} else {
				e.setFormat(chatTitle.getTitle() + e.getFormat());
			}
		}
	}
}
