package com.morkaz.moxchattitles.commands;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.stuff.Pages;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChatTitleCmd implements CommandExecutor {

	private MoxChatTitles main;

	public ChatTitleCmd(MoxChatTitles main){
		this.main = main;
	}


	private void sendHelpMessage(CommandSender sender) {
		ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
		ServerUtils.sendMessage(sender, " ");

		ServerUtils.sendMessage(sender, " ");
		ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
	}


	public List<String> onTabComplete(CommandSender sender, Command command, String commandAlias, String[] argsArray) {
		List<String> completeList = new ArrayList<>();
		List<String> args = Arrays.asList(argsArray);
		if (args.size() == 1){
			completeList.addAll(Arrays.asList("set", "list", "reload", "help"));
		} else {
			if (args.size() == 2) {
				if (args.get(0).equalsIgnoreCase("set")) {
					completeList.add("<player>");
				}
			} else if (args.size() == 3){
				if (args.get(0).equalsIgnoreCase("set")){
					completeList.add("<title.name>");
				}
			}
		}
		return completeList;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] argsArray) {
		List<String> args = Arrays.asList(argsArray);
		if (args.size() == 0){
			this.sendHelpMessage(sender);
			return true;
		}
		if (args.get(0).equalsIgnoreCase("set")){
			if (args.size() == 1){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.bad-command-usage")
					.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&d[?]&3> &3<title.name&d[?]&3>")
				);
				return true;
			}
			if (args.get(1).length() > 16){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.nick-too-long"));
				return true;
			}
			if (args.size() == 2){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.bad-command-usage")
						.replace("%command%", "&9/"+alias+" "+args.get(0)+" "+args.get(1)+" &3<title.name&d[?]&3>")
				);
				return true;
			}
			ChatTitle chatTitle = main.getDataManager().getTitle(args.get(2).toLowerCase());
			if (chatTitle == null){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.title-not-exist"));
				return true;
			}
			String playerID = main.getDataManager().getPlayerIDFromName(args.get(1));
			PlayerData playerData = main.getDataManager().getPlayerData(playerID);
			playerData.setLastTitle(chatTitle);
			ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.title-set")
					.replace("%player%", args.get(1))
					.replace("%title%", args.get(2))
			);
			return true;
		} else if (args.get(0).equalsIgnoreCase("delete") || args.get(0).equalsIgnoreCase("del") || args.get(0).equalsIgnoreCase("remove")){
			if (args.size() == 1){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.bad-command-usage")
						.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&d[?]&3>")
				);
				return true;
			}
			if (args.get(1).length() > 16){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.nick-too-long"));
				return true;
			}
			String playerID = main.getDataManager().getPlayerIDFromName(args.get(1));
			PlayerData playerData = main.getDataManager().getPlayerData(playerID);
			playerData.setLastTitle(null);
			ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.title-removed")
					.replace("%player%", args.get(1))
			);
			return true;
		} else if (args.get(0).equalsIgnoreCase("list")){
			Integer pageNumber = 1;
			if (args.size() == 2){
				if (NumberUtils.isNumber(args.get(1))){
					pageNumber = Integer.valueOf(args.get(1));
				}
			}
			Pages<ChatTitle> pages = new Pages(main.getDataManager().titlesMap.values(), 15);
			Collection<ChatTitle> pageRecords = pages.getObjects(pageNumber);
			if (pageNumber == 1 && pageRecords == null){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.no-titles-defined"));
				return true;
			} else if (pageRecords == null){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.no-titles-on-page")
						.replace("%page%", pageNumber.toString())
						.replace("%max-page%", pages.getMaxPageNumber().toString())
				);
				return true;
			}
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
			ServerUtils.sendMessage(sender, " ");
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-outputs.title-list")+":");
			for (ChatTitle chatTitle : pageRecords){
				ServerUtils.sendMessage(sender, " &7- &9&o"+chatTitle.getTitleIndex()+"&9: &r"+chatTitle.getTitle());
			}
			ServerUtils.sendMessage(sender, " ");
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-outputs.actual-page")
					.replace("%page%", pageNumber.toString())
			);
			ServerUtils.sendMessage(sender, " ");
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
			return true;
		} else if (args.get(0).equalsIgnoreCase("info")){
			if (args.size() == 1){
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("errors.bad-command-usage")
						.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&d[?]&3>")
				);
				return true;
			}
			String playerID = main.getDataManager().getPlayerIDFromName(args.get(1));
			PlayerData playerData = main.getDataManager().getPlayerData(playerID);
			ChatTitle chatTitle = playerData.getLastTitle();
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
			ServerUtils.sendMessage(sender, " ");
			ServerUtils.sendMessage(sender, " "+main.getMessagesConfig().getString("usage-outputs.player-info")+":");
			if (chatTitle != null){
				ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-outputs.last-title")+": "+playerData.getLastTitle());
			} else {
				ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("usage-outputs.last-title")+": "+main.getMessagesConfig().getString("usage-outputs.none"));
			}
			ServerUtils.sendMessage(sender, " ");
			ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
			return true;
		} else if (args.get(0).equalsIgnoreCase("reload")){
			main.reload();
			ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.plugin-reloaded"));
			return true;
		} else {
			this.sendHelpMessage(sender);
		}
		return true;
	}


}
