package com.morkaz.moxchattitles.data;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxlibrary.api.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatTitle {

	private String title;
	private String permission;
	private String titleIndex;
	private ItemStack guiItemStack;

	public ChatTitle(String title, String titleIndex, String permission, ItemStack guiItemStack) {
		if (title != null){
			this.title = ChatColor.translateAlternateColorCodes('&', title);
		}
		this.permission = permission;
		this.titleIndex = titleIndex;
		this.guiItemStack = guiItemStack;
		prepareItemStack();
	}

	public ItemStack getGuiItemStack() {
		return guiItemStack;
	}

	public String getTitle() {
		return title;
	}

	public String getPermission() {
		return permission;
	}

	public String getTitleIndex() {
		return titleIndex;
	}

	private void prepareItemStack(){
		if (guiItemStack == null){
			return;
		}
		ItemMeta itemMeta = this.guiItemStack.getItemMeta();
		//Prepare name
		String name = replacePlaceholders(itemMeta.getDisplayName());
		String lore = replacePlaceholders(String.join("||", itemMeta.getLore()));
		this.guiItemStack = ItemUtils.setItemName(guiItemStack, name);
		this.guiItemStack = ItemUtils.setItemLore(guiItemStack, lore);
	}

	private String replacePlaceholders(String text){
		MoxChatTitles main = MoxChatTitles.getInstance();
		return text
				.replace("%default-name%", main.getMessagesConfig().getString("gui.default-name"))
				.replace("%default-lore%", main.getMessagesConfig().getString("gui.default-lore"))
				.replace("%title%", title)
				.replace("%permission%", permission)
		;
	}

}
