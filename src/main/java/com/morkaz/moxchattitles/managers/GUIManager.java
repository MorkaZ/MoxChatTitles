package com.morkaz.moxchattitles.managers;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxchattitles.data.ChatTitle;
import com.morkaz.moxchattitles.data.PlayerData;
import com.morkaz.moxlibrary.api.ItemUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.gui.ActionItem;
import com.morkaz.moxlibrary.gui.ChestGUI;
import com.morkaz.moxlibrary.stuff.Pages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

	private MoxChatTitles main;

	public GUIManager(MoxChatTitles main) {
		this.main = main;
	}

	public void openGUI(Player player, Integer pageNumber){
		List<ChatTitle> chatTitles = main.getDataManager().getOwnedTitles(player);
		Pages<ChatTitle> chatTitlePages = new Pages(chatTitles, 28);

		List<ChatTitle> chatTitlesPage = new ArrayList(chatTitlePages.getObjects(pageNumber) != null ? chatTitlePages.getObjects(pageNumber) : new ArrayList());
		String playerID = main.getDataManager().getPlayerID(player);
		PlayerData playerData = main.getDataManager().getPlayerData(playerID);
		ChestGUI chestGUI = new ChestGUI(main, 6, ChatColor.translateAlternateColorCodes('&', main.getMessagesConfig().getString("gui.gui-title")), true);
		//Generate Fillers
		generateFillers(chestGUI);
		//Fill GUI with content
		int counter = 0;
		if (chatTitlesPage.size() > 0){
			for (int y = 2; y <= 5; y++){
				for (int x = 2; x <= 8; x++){
					if (chatTitlesPage.size() <= counter){
						break;
					}
					ChatTitle chatTitle = chatTitlesPage.get(counter);
					counter++;
					chestGUI.addItem(new ActionItem(x, y, prepareGUIItemStack(player, playerData, chatTitle)) {
						@Override
						public void onClick(InventoryClickEvent event) {
							event.setCancelled(true);
							if (playerData.getLastTitle() == null){
								if (main.getDataManager().getDefaultTitle().equals(chatTitle)){
									ServerUtils.sendMessage(player, main.getPrefix(), main.getMessagesConfig().getString("gui.outputs.title-already-selected"));
									player.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 0.5F, 0.1F);
									return;
								} else {
									ServerUtils.sendMessage(player, main.getPrefix(), main.getMessagesConfig().getString("gui.outputs.title-selected"));
									player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0F, 1.5F);
									playerData.setLastTitle(chatTitle);
								}
							} else if (playerData.getLastTitle().equals(chatTitle)){
								ServerUtils.sendMessage(player, main.getPrefix(), main.getMessagesConfig().getString("gui.outputs.title-already-selected"));
								player.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 0.5F, 0.1F);
								return;
							} else {
								ServerUtils.sendMessage(player, main.getPrefix(), main.getMessagesConfig().getString("gui.outputs.title-selected"));
								player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0F, 1.5F);
								playerData.setLastTitle(chatTitle);
							}
							player.closeInventory();
							openGUI(player, pageNumber);
						}
					});
				}
				if (chatTitlesPage.size() <= counter){
					break;
				}
			}
		} else {
			chestGUI.addItem(new ActionItem(2, 2, ItemUtils.createItemStack(Material.NETHER_STAR, 1, main.getMessagesConfig().getString("gui.empty-gui-item-name"), main.getMessagesConfig().getString("gui.empty-gui-item-lore"))) {
				@Override
				public void onClick(InventoryClickEvent event) {
					event.setCancelled(true);
				}
			});
		}
		if (chatTitlePages.getLastPageNumber() > pageNumber){
			chestGUI.addItem(new ActionItem(9, 6, ItemUtils.createItemStack(Material.NETHER_STAR, 1, main.getMessagesConfig().getString("gui.next-page-item-name"), null)) {
				@Override
				public void onClick(InventoryClickEvent event) {
					event.setCancelled(true);
					player.closeInventory();
					openGUI(player, pageNumber+1);
				}
			});
		}
		if (pageNumber > 1){
			chestGUI.addItem(new ActionItem(1, 6, ItemUtils.createItemStack(Material.NETHER_STAR, 1, main.getMessagesConfig().getString("gui.previous-page-item-name"), null)) {
				@Override
				public void onClick(InventoryClickEvent event) {
					event.setCancelled(true);
					player.closeInventory();
					openGUI(player, pageNumber-1);
				}
			});
		}
		chestGUI.open(player);
	}

	private void generateFillers(ChestGUI chestGUI){
		//Blue
		for (int x = 1; x <= 9; x++){
			for (int y = 1; y <= 6; y++){
				//Bukkit.broadcastMessage("x:"+x+", y:"+y);
				if (x != 1 && x != 9 && y != 1 && y != 6){
					//Body
					chestGUI.addItem(new ActionItem(x, y, ItemUtils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, main.getPrefix(), null)) {
						@Override
						public void onClick(InventoryClickEvent event) {
							event.setCancelled(true);
						}
					});
				} else {
					//Frame
					chestGUI.addItem(new ActionItem(x, y, ItemUtils.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1, main.getPrefix(), null)) {
						@Override
						public void onClick(InventoryClickEvent event) {
							event.setCancelled(true);
						}
					});
				}
			}
		}
	}

	private ItemStack prepareGUIItemStack(Player player, PlayerData playerData, ChatTitle chatTitle){
		ItemStack guiItemStack = new ItemStack(chatTitle.getGuiItemStack());
		ItemMeta itemMeta = guiItemStack.getItemMeta();
		//Prepare name
		String name = replacePlaceholders(player, playerData, chatTitle, itemMeta.getDisplayName());
		String lore = replacePlaceholders(player, playerData, chatTitle, String.join("||", itemMeta.getLore()));
		guiItemStack = ItemUtils.setItemName(guiItemStack, name);
		guiItemStack = ItemUtils.setItemLore(guiItemStack, lore);
		return guiItemStack;
	}

	//Replacing dynamic placeholders that was unnable to be replaced in ChatTitle object.
	private String replacePlaceholders(Player player, PlayerData playerData, ChatTitle chatTitle, String text){
		text = text
				.replace("%player%", player.getName())
		;
		if (playerData.getLastTitle() != null){
			text = text
					.replace("%selection%", playerData.getLastTitle().equals(chatTitle) ? main.getMessagesConfig().getString("gui.selected") : main.getMessagesConfig().getString("gui.not-selected"))
			;
		} else {
			if (main.getDataManager().getDefaultTitle().equals(chatTitle)){
				text = text
						.replace("%selection%", main.getMessagesConfig().getString("gui.selected"))
				;
			} else {
				text = text
						.replace("%selection%", main.getMessagesConfig().getString("gui.not-selected"))
				;
			}
		}
		return text;
	}




}
