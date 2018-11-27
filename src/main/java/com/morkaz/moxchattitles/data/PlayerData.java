package com.morkaz.moxchattitles.data;

import com.morkaz.moxchattitles.MoxChatTitles;
import com.morkaz.moxlibrary.api.QueryUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

	private String playerID;
	private Long lastLogin;
	private ChatTitle lastTitle;
	private Boolean inDatabase;

	public PlayerData(String playerID, Long lastLogin, ChatTitle lastTitle, Boolean isInDatabase) {
		this.playerID = playerID;
		this.lastLogin = lastLogin;
		this.lastTitle = lastTitle;
		this.inDatabase = isInDatabase;
	}

	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
		this.updateDatabase();
	}

	public void setLastTitle(ChatTitle lastTitle) {
		this.lastTitle = lastTitle;
		this.updateDatabase();
	}

	public String getPlayerID() {
		return playerID;
	}

	public Long getLastLogin() {
		return lastLogin;
	}

	public ChatTitle getLastTitle() {
		return lastTitle;
	}

	public Boolean isInDatabase(){
		return this.inDatabase;
	}

	private void updateDatabase(){
		MoxChatTitles main = MoxChatTitles.getInstance();
		List<Pair<String, Object>> pairs = new ArrayList<>();
		pairs.add(Pair.of(main.ID_COLUMN, playerID));
		pairs.add(Pair.of(main.LAST_LOGIN_COLUMN, lastLogin));
		pairs.add(Pair.of(main.LAST_TITLE_COLUMN, lastTitle));
		List<String> queries = QueryUtils.constructQueryMultipleValuesSet(
				main.TABLE,
				pairs,
				true,
				main.getDatabase().getDatabaseType()
		);
		queries.forEach(
				(query) -> main.getDatabase().updateAsync(query)
		);
		this.inDatabase = true;
	}

}
