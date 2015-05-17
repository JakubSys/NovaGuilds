package co.marcin.NovaGuilds.basic;

import java.util.ArrayList;
import java.util.List;

import co.marcin.NovaGuilds.NovaGuilds;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NovaGuild {
	private int id;
	private String name;
	private String tag;
	private NovaRegion region;
	private String leadername;
	private Location spawnpoint;
	private double money = 0;
	private int points;
	private NovaRaid raid;
	private long timeRest;
	private long lostLiveTime;
	private int lives;
	
	private List<NovaPlayer> players = new ArrayList<>();

	private List<String> allies = new ArrayList<>();
	private List<String> allies_invited = new ArrayList<>();

	private List<String> war = new ArrayList<>();
	private List<String> nowar_inv = new ArrayList<>();

	//getters
	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}
	
	public List<String> getAllies() {
		return allies;
	}
	
	public List<String> getAllyInvitations() {
		return allies_invited;
	}

	public List<String> getWars() {
		return war;
	}

	public List<String> getNoWarInvitations() {
		return nowar_inv;
	}
	
	public String getTag() {
		return tag;
	}
	
	public NovaRegion getRegion() {
		return region;
	}
	
	public List<NovaPlayer> getPlayers() {
		return players;
	}

	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getPlayers()) {
			if(nPlayer.isOnline()) {
				if(nPlayer.getPlayer() != null) {
					list.add(nPlayer.getPlayer());
				}
			}
		}

		return list;
	}
	
	public String getLeaderName() {
		return leadername;
	}
	
	public Location getSpawnPoint() {
		return spawnpoint;
	}
	
	public int getId() {
		return id;
	}
	
	public double getMoney() {
		return money;
	}

	public NovaRaid getRaid() {
		return raid;
	}

	public long getTimeRest() {
		return timeRest;
	}

	public int getLives() {
		return lives;
	}

	public long getLostLiveTime() {
		return lostLiveTime;
	}

	//setters
	public void setName(String n) {
		name = n;
	}
	
	public void setTag(String t) {
		tag = t;
	}
	
	public void setRegion(NovaRegion r) {
		region = r;
	}
	
	public void setLeaderName(String name) {
		leadername = name;
	}
	
	public void setSpawnPoint(Location loc) {
		spawnpoint = loc;
	}
	
	public void setId(int i) {
		id = i;
	}
	
	public void setMoney(double m) {
		money = m;
	}
	
	public void setAllies(List<String> a) {
		allies.clear();

		for(String ally : a) {
			allies.add(ally.toLowerCase());
		}
	}
	
	public void setAllyInvitations(List<String> ai) {
		allies_invited.clear();

		for(String allyinv : ai) {
			allies_invited.add(allyinv.toLowerCase());
		}
	}

	public void setWars(List<String> w) {
		war.clear();

		for(String warr : w) {
			war.add(warr.toLowerCase());
		}
	}

	public void setNoWarInvitations(List<String> nwi) {
		nowar_inv.clear();

		for(String nowar : nwi) {
			nowar_inv.add(nowar.toLowerCase());
		}
	}
	
	public void setPoints(int p) {
		points = p;
	}

	public void updateTimeRest() {
		timeRest = NovaGuilds.systemSeconds();
	}

	public void updateLostLive() {
		lostLiveTime = NovaGuilds.systemSeconds();
	}

	public void setLostLiveTime(long t) {
		lostLiveTime = t;
	}

	public void resetLostLiveTime() {
		lostLiveTime = 0;
	}

	public void setLives(int l) {
		lives = l;
	}

	public void setTimeRest(long timeRest) {
		this.timeRest = timeRest;
	}

	public void isNotRaid() {
		raid = null;
	}
	
	//check
	public boolean isInvitedToAlly(NovaGuild guild) {
		return allies_invited.contains(guild.getName().toLowerCase());
	}

	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild.getName().toLowerCase());
	}

	public boolean isNoWarInvited(NovaGuild guild) {
		return nowar_inv.contains(guild.getName().toLowerCase());
	}

	public boolean isLeader(String playername) {
		return leadername.equals(playername);
	}

	public boolean isLeader(NovaPlayer nPlayer) {
		return leadername.equalsIgnoreCase(nPlayer.getName());
	}
	
	public boolean isLeader(CommandSender sender) {
		return leadername.equals(sender.getName());
	}
	
	public boolean hasRegion() {
		return region != null;
	}
	
	public boolean isAlly(NovaGuild guild) {
		return guild != null && allies.contains(guild.getName().toLowerCase());
	}

	public boolean isRaid() {
		return !(raid == null);
	}

	//add/remove
	public void addAlly(NovaGuild guild) {
		allies.add(guild.getName().toLowerCase());
	}
	
	public void addAllyInvitation(NovaGuild guild) {
		allies_invited.add(guild.getName().toLowerCase());
	}

	public void addWar(NovaGuild guild) {
		war.add(guild.getName().toLowerCase());
	}

	public void addNoWarInvitation(NovaGuild guild) {
		nowar_inv.add(guild.getName().toLowerCase());
	}

	public void addPlayer(NovaPlayer nPlayer) {
		if(!players.contains(nPlayer)) {
			players.add(nPlayer);
		}
	}
	
	public void addMoney(double m) {
		money += m;
	}
	
	public void addPoints(int p) {
		points += p;
	}
	
	public void removePlayer(NovaPlayer nPlayer) {
		if(players.contains(nPlayer)) {
			players.remove(nPlayer);
		}
	}
	
	public void removeAlly(NovaGuild guild) {
		if(allies.contains(guild.getName().toLowerCase()))
			allies.remove(guild.getName().toLowerCase());
	}

	public void removeWar(NovaGuild guild) {
		if(war.contains(guild.getName().toLowerCase()))
			war.remove(guild.getName().toLowerCase());
	}

	public void removeNoWarInvitation(NovaGuild guild) {
		if(nowar_inv.contains(guild.getName().toLowerCase()))
			nowar_inv.remove(guild.getName().toLowerCase());
	}
	
	public void takeMoney(double m) {
		money -= m;
	}

	public void takeLive() {
		lives--;
	}

	public void addLive() {
		lives++;
	}
	
	public void takePoints(int p) {
		points -= p;
	}

	public void removeAllyInvitation(NovaGuild guild) {
		if(allies_invited.contains(guild.getName().toLowerCase()))
			allies_invited.remove(guild.getName().toLowerCase());
	}

	public void createRaid(NovaGuild attacker) {
		raid = new NovaRaid(attacker,this);
	}

	public boolean isMember(NovaPlayer nPlayer) {
		return players.contains(nPlayer);
	}
}