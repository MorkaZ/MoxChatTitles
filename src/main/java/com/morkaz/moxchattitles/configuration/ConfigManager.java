package com.morkaz.moxchattitles.configuration;

import com.morkaz.moxlibrary.other.configuration.LocaleConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;



public class ConfigManager extends LocaleConfiguration {

	public ConfigManager(Plugin plugin) {
		super(plugin, "messages", "pl", "locale");
	}

}
