package com.morkaz.moxchattitles;

import com.morkaz.moxchattitles.commands.ChatTitleCmd;
import com.morkaz.moxchattitles.configuration.ConfigManager;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;
import com.morkaz.moxchattitles.listeners.ChatListener;
import com.morkaz.moxchattitles.listeners.JoinListener;
import com.morkaz.moxchattitles.listeners.QuitListener;
import com.morkaz.moxchattitles.managers.DataManager;
import com.morkaz.moxlibrary.api.QueryUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.database.sql.SQLDatabase;
import com.morkaz.moxlibrary.database.sql.mysql.MySQLDatabase;
import com.morkaz.moxlibrary.database.sql.sqlite.SQLiteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoxChatTitles extends JavaPlugin {

	private static MoxChatTitles main;
	private ConfigManager configManager;
	private DataManager dataManager;
	private SQLDatabase database;
	public final String
			TABLE = "Players",
			ID_COLUMN = "id",
			LAST_TITLE_COLUMN = "lastTitle",
			LAST_LOGIN_COLUMN = "lastLogin"
	;
	public String prefix;


	public void onEnable(){
		//Set Instance
		main = this;

		//Initialize Managers
		configManager = new ConfigManager(this);
		dataManager = new DataManager(this);

		//Define plugin prefix
		this.prefix = configManager.getMessagesConfig().getString("misc.prefix");

		//Database setup
		if (getConfig().getString("database.type").equalsIgnoreCase("mysql")){
			String host = getConfig().getString("database.settings.mysql.host");
			String port = getConfig().getString("database.settings.mysql.port");
			String databaseName = getConfig().getString("database.settings.mysql.database");
			String user = getConfig().getString("database.settings.mysql.user");
			String password = getConfig().getString("database.settings.mysql.password");
			database = new MySQLDatabase(this);
			database.createConnection(host, port, databaseName, user, password);
		} else {
			String fileLocation = getConfig().getString("database.settings.sqlite.file-location").replace("%plugin-folder%", getDataFolder().getPath());
			database = new SQLiteDatabase(this);
			database.createConnection(fileLocation);
		}

		//New table
		List<Pair<String, String>> columnTypeList = new ArrayList<>();
		columnTypeList.add(Pair.of(ID_COLUMN, "varchar(36)"));
		columnTypeList.add(Pair.of(LAST_TITLE_COLUMN, "TEXT"));
		columnTypeList.add(Pair.of(LAST_LOGIN_COLUMN, "BIGINT"));
		String newTableQuery = QueryUtils.constructQueryTableCreate(
				TABLE,
				columnTypeList,
				ID_COLUMN,
				database.getDatabaseType()
		);
		database.updateSync(newTableQuery);

		//Register chattitle command
		ChatTitleCmd chatTitleCmd = new ChatTitleCmd(this);
		ServerUtils.registerCommand(
				this,
				"chattitle",
				Arrays.asList("cht"),
				"Main command of MoxChatTitles plugin.",
				"/cht <args..>",
				chatTitleCmd
		);

		//Register listeners
		new JoinListener(this);
		new QuitListener(this);
		new ChatListener(this);

		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin enabled!");
	}



	public void onDisable(){
		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin disabled!");
	}

	public void reload(){
		this.configManager.reloadConfiguration();
		this.dataManager.reload();
	}



	public ConfigManager getConfigManager() {
		return configManager;
	}

	public SQLDatabase getDatabase() {
		return database;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public FileConfiguration getMessagesConfig(){
		return this.configManager.getMessagesConfig();
	}

	public static MoxChatTitles getInstance() {
		return main;
	}

	public String getPrefix() {
		return prefix;
	}


}
