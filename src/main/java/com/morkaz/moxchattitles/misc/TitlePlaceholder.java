package com.morkaz.moxchattitles.misc;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class TitlePlaceholder extends PlaceholderExpansion {


	public String getIdentifier() {
		return "moxchattitles";
	}

	public String getPlugin() {
		return MoxChatTitles.getInstance().getName();
	}

	public String getAuthor() {
		return "Morkazoid";
	}


	public String getVersion() {
		return MoxChatTitles.getInstance().getDescription().getVersion();
	}


	public String onPlaceholderRequest(Player player, String identifier) {
		if (identifier.equalsIgnoreCase("title")){
			if (player != null){
				PlayerData playerData = MoxChatTitles.getInstance().getDataManager().getPlayerData(MoxChatTitles.getInstance().getDataManager().getPlayerID(player));
				if (playerData != null){
					if (playerData.getLastTitle() != null){
						return playerData.getLastTitle().getTitle();
					}
				}
			}
			return "";
		}
		return null;
	}
}
