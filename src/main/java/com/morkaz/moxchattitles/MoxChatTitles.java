package com.morkaz.moxchattitles;

import com.morkaz.moxchattitles.commands.ChatTitleCmd;
import com.morkaz.moxchattitles.configuration.ConfigManager;
import com.morkaz.moxchattitles.listeners.ChatListener;
import com.morkaz.moxchattitles.listeners.JoinListener;
import com.morkaz.moxchattitles.listeners.QuitListener;
import com.morkaz.moxchattitles.managers.DataManager;
import com.morkaz.moxchattitles.managers.GUIManager;
import com.morkaz.moxchattitles.misc.Metrics;
import com.morkaz.moxchattitles.misc.TitlePlaceholder;
import com.morkaz.moxlibrary.api.QueryUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.database.sql.SQLDatabase;
import com.morkaz.moxlibrary.database.sql.mysql.MySQLDatabase;
import com.morkaz.moxlibrary.database.sql.sqlite.SQLiteDatabase;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoxChatTitles extends JavaPlugin {

	private static MoxChatTitles main;
	private ConfigManager configManager;
	private DataManager dataManager;
	private GUIManager guiManager;
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

		//Add Metrics
		Metrics metrics = new Metrics(this);

		//Initialize configuration
		configManager = new ConfigManager(this);

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

		//Initialize Managers
		dataManager = new DataManager(this);
		guiManager = new GUIManager(this);

		//Define plugin prefix
		this.prefix = configManager.getMessagesConfig().getString("misc.prefix");

		//Register chattitle command
		ChatTitleCmd chatTitleExecutor = new ChatTitleCmd(this);
		PluginCommand chatTitleCommand = ServerUtils.registerCommand(
				this,
				"chattitle",
				Arrays.asList("cht"),
				"Main command of MoxChatTitles plugin.",
				"/cht <args..>",
				chatTitleExecutor
		);
		chatTitleCommand.setTabCompleter(chatTitleExecutor);

		//Register listeners
		new JoinListener(this);
		new QuitListener(this);
		new ChatListener(this);

//		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
//			boolean oldVersion = false;
//			try {
//				Class.forName("be.maximvdw.placeholderapi.PlaceholderReplacer");
//			} catch( ClassNotFoundException e ) {
//				oldVersion = true;
//			}
//			if (oldVersion == false){
//				PlaceholderAPI.registerPlaceholder(this, "mox_title", new PlaceholderReplacer() {
//					@Override
//					public String onPlaceholderReplace(PlaceholderReplaceEvent placeholderReplaceEvent) {
//						Player player = placeholderReplaceEvent.getPlayer();
//						PlayerData playerData = MoxChatTitles.getInstance().getDataManager().getPlayerData(MoxChatTitles.getInstance().getDataManager().getPlayerID(player));
//						if (playerData != null){
//							if (playerData.getLastTitle() != null){
//								return playerData.getLastTitle().getTitle();
//							}
//						}
//						Bukkit.getLogger().warning("["+getDescription().getName()+"] Title placeholder \"mox_title\" registered for MVDwPlaceholderAPI!");
//						return "";
//
//					}
//				});
//			} else {
//				Bukkit.getLogger().warning("["+getDescription().getName()+"] You are using old MVdWPlaceholderAPI version! Update it if you want to use placeholders!");
//			}
//		}
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			TitlePlaceholder titlePlaceholder = new TitlePlaceholder();
			titlePlaceholder.register();
			Bukkit.getLogger().warning("["+getDescription().getName()+"] Title placeholder \"moxchattitles_title\" registered for PlaceholderAPI!");
		}
		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin enabled!");
	}



	public void onDisable(){

		//Close connection to unlock database
		this.database.closeConnection();

		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin disabled!");
	}

	public void reload(){
		this.configManager.reload();
		this.dataManager.reload();
	}

	public GUIManager getGuiManager() {
		return guiManager;
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

	public FileConfiguration getTitlesConfig(){
		return this.configManager.getTitlesConfig();
	}

	public String getPrefix() {
		return prefix;
	}


}
