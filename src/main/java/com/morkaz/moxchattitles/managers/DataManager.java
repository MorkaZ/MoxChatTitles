package com.morkaz.moxchattitles.managers;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;

import java.util.*;

import com.morkaz.moxlibrary.api.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataManager{

	private MoxChatTitles main;
	//			Index	Data
	public Map<String, ChatTitle> titlesMap = new HashMap<>();
	//			ID		Data
	public Map<String, PlayerData> playerDataMap = new HashMap<>();
	public ChatTitle defaultTitle;

	public DataManager(MoxChatTitles main) {
		this.main = main;
		this.reload();
	}

	public void reload(){
		this.loadTitles();
		this.purgeOldPlayers();
		this.loadAllPlayersData();
	}

	public PlayerData loadPlayerData(Player player){
		String playerID = Bukkit.getOnlineMode() ? player.getUniqueId().toString() : player.getName().toLowerCase();
		if (playerDataMap.containsKey(playerID)){
			return playerDataMap.get(playerID);
		}
		PlayerData playerData = new PlayerData(playerID, System.currentTimeMillis(), null, false);
		playerDataMap.put(playerID, playerData);
		return playerData;
	}

	public void unloadPlayerData(String playerID){
		PlayerData playerData = playerDataMap.remove(playerID);
	}

	public void purgeOldPlayers(){
		// Config
		if (!main.getConfig().getBoolean("purger.enabled")){
			return;
		}
		Integer configDays = main.getConfig().getInt("purger.max-not-online-days");
		Long minDate = System.currentTimeMillis() - (86400000L * configDays.longValue()); // (current date) - (max time)
		// Query
		String query = "DELETE FROM `"+main.TABLE+"` WHERE `"+main.LAST_LOGIN_COLUMN+"` < "+minDate;
		main.getDatabase().updateSync(query);
	}

	public void loadTitles(){
		this.titlesMap.clear();
		Bukkit.getLogger().info("["+main.getDescription().getName()+"] Loading chat titles from config..");
		ConfigurationSection indexes =  main.getTitlesConfig().getConfigurationSection("titles");
		for (String index : indexes.getKeys(false)){
			String title = main.getTitlesConfig().getString("titles."+index+".title");
			String permission =  main.getTitlesConfig().getString("titles."+index+".permission");
			ItemStack guiItemStack = ConfigUtils.loadItemStack(main.getTitlesConfig(), "titles."+index+".gui-item", main);
			ChatTitle chatTitle = new ChatTitle(title, index, permission, guiItemStack);
			if (title != null){
				titlesMap.put((index+"").toLowerCase(), chatTitle);
			} else {
				Bukkit.getLogger().warning("["+main.getDescription().getName()+"] Failed to load chat title \"" + chatTitle + "\". Content is null, permission is not set or title field is not text in configuration.");
				continue;
			}
		}
		this.defaultTitle = this.getTitle(main.getConfig().getString("default-title.title-name"));
		Bukkit.getLogger().info("["+main.getDescription().getName()+"] Chat titles loaded!");
	}

	public ChatTitle getTitle(String titleIndex){
		return this.titlesMap.get(titleIndex);
	}

	public void loadAllPlayersData(){
		playerDataMap.clear();
		Bukkit.getLogger().info("["+main.getDescription().getName()+"] Loading all players data..");
		if (titlesMap.isEmpty()){
			Bukkit.getLogger().info("["+main.getDescription().getName()+"] There is no titles loaded from config in memory.. Aborting player data loading..");
			return;
		}
		String query = "SELECT * FROM `"+main.TABLE+"`";
		ResultSet resultSet = main.getDatabase().getResult(query);
		try{
			while(resultSet.next()){
				String playerID =  resultSet.getString(main.ID_COLUMN);
				Long lastLogin = resultSet.getLong(main.LAST_LOGIN_COLUMN);
				String lastTitleString = resultSet.getString(main.LAST_TITLE_COLUMN);
				ChatTitle lastTitle = getTitle(lastTitleString);
				if (lastTitle == null){
					Bukkit.getLogger().info("["+main.getDescription().getName()+"] Last title: \""+lastTitleString+"\" of player: \""+playerID+"\" have not been found in titles configuration.. Player has been removed from last title memory.");
					resultSet.deleteRow();
					continue;
				}
				if (lastLogin == null){
					resultSet.updateString(main.LAST_LOGIN_COLUMN, lastLogin.toString());
					Bukkit.getLogger().info("["+main.getDescription().getName()+"] Player: \""+playerID+"\" has not set last login date. His last login date has been set to now.");
				}
				PlayerData playerData = new PlayerData(playerID, lastLogin, lastTitle, true);
				playerDataMap.put(playerID, playerData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Bukkit.getLogger().info("["+main.getDescription().getName()+"] All player data loaded!");
	}

	public PlayerData getPlayerData(String playerID){
		PlayerData playerData = this.playerDataMap.get(playerID);
		if (playerData == null){
			playerData = new PlayerData(playerID, null, null, false);
		}
		return playerData;
	}

	public Boolean isPlayersDataLoaded(String playerID){
		return playerDataMap.containsKey(playerID);
	}

	public void setPlayersTitle(String playerName, ChatTitle title){
		PlayerData playerData = getPlayerData(playerName);
		playerData.setLastTitle(title);
	}

	public ChatTitle getPlayersTitle(String playerName){
		PlayerData playerData = getPlayerData(playerName);
		return playerData.getLastTitle();
	}

	public String getPlayerID(Player player){
		return Bukkit.getOnlineMode() ? player.getUniqueId().toString() : player.getName().toLowerCase();
	}

	public String getPlayerIDFromName(String playerName){
		if (Bukkit.getOnlineMode()){
			Player player = Bukkit.getPlayerExact(playerName);
			if (player != null){
				return Bukkit.getPlayerExact(playerName).getUniqueId().toString();
			} else {
				return Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
			}
		} else {
			return playerName.toLowerCase();
		}
	}

	public ChatTitle getDefaultTitle() {
		return defaultTitle;
	}

	public List<ChatTitle> getOwnedTitles(Player player){
		List<ChatTitle> chatTitles = new ArrayList<>();
		for (ChatTitle chatTitle : titlesMap.values()){
			if (player.hasPermission(chatTitle.getPermission())){
				chatTitles.add(chatTitle);
			}
		}
		return chatTitles;
	}

}
