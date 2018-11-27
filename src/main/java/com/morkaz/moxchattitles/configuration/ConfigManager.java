package com.morkaz.moxchattitles.configuration;

import com.morkaz.moxlibrary.misc.configuration.LocaleConfiguration;
import org.bukkit.plugin.Plugin;



public class ConfigManager extends LocaleConfiguration {

	public ConfigManager(Plugin plugin) {
		super(plugin, "pl", "locale");
	}

}
