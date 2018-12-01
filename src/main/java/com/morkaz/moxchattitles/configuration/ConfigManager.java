package com.morkaz.moxchattitles.configuration;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxlibrary.api.ConfigUtils;
import com.morkaz.moxlibrary.other.configuration.LocaleConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;



public class ConfigManager extends LocaleConfiguration {

	private FileConfiguration titlesConfig;
	private MoxChatTitles main;

	public ConfigManager(MoxChatTitles main) {
		super(main, "messages", "pl", "locale");
		this.main = main;
		reloadTitlesConfig();
	}

	public FileConfiguration getTitlesConfig() {
		return titlesConfig;
	}

	public void reloadTitlesConfig(){
		titlesConfig = ConfigUtils.loadFileConfiguration(main, "titles.yml", false);
	}



}
