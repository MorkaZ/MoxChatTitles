package com.morkaz.moxchattitles.listeners;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;
import org.bukkit.Bukkit;
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

	@EventHandler(priority= EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		String playerID = Bukkit.getOnlineMode() ? e.getPlayer().getUniqueId().toString() : e.getPlayer().getName().toLowerCase();
		PlayerData playerData = main.getDataManager().getPlayerData(playerID);
		ChatTitle chatTitle = playerData.getLastTitle();
		String chatFormat = e.getFormat();
		if (chatTitle != null) {
			if (chatFormat.contains("%moxtitle%")) {
				e.setFormat(chatFormat.replace("%moxtitle%", chatTitle.getTitle()));
			} else {
				e.setFormat(chatTitle.getTitle() + e.getFormat());
			}
		}
	}
}
