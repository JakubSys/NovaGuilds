/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private FileConfiguration config;

	private DataStorageType primaryDataStorageType;
	private DataStorageType secondaryDataStorageType;
	private DataStorageType dataStorageType;

	private final List<PotionEffectType> guildEffects = new ArrayList<>();

	private final Map<Config, Object> cache = new HashMap<>();
	private static final ServerVersion serverVersion = ServerVersion.detect();

	/**
	 * Gets current server version
	 *
	 * @return server version
	 */
	public static ServerVersion getServerVersion() {
		return serverVersion;
	}

	public enum ServerVersion {
		MINECRAFT_1_7_R4,
		MINECRAFT_1_8_R2,
		MINECRAFT_1_9_R1,
		MINECRAFT_1_9_R2,
		MINECRAFT_1_10_R1;

		/**
		 * Detects server version
		 *
		 * @return server version enum
		 */
		public static ServerVersion detect() {
			String craftBukkitVersion = Reflections.getVersion();
			craftBukkitVersion = craftBukkitVersion.substring(1, craftBukkitVersion.length() - 1);

			for(ServerVersion version : values()) {
				String string = version.name();
				string = org.apache.commons.lang.StringUtils.replace(string, "MINECRAFT_", "");

				if(craftBukkitVersion.startsWith(string)) {
					LoggerUtils.info("This server is using version: " + craftBukkitVersion);
					return version;
				}
			}

			ServerVersion closestVersion = getClosestVersion(craftBukkitVersion);
			LoggerUtils.error("Version " + craftBukkitVersion + " is not supported by NovaGuilds.");
			LoggerUtils.error("Expect bugs and report them to the development team. (/ng)");
			LoggerUtils.error("NovaGuilds is now using implementation for version: " + closestVersion.name());

			//Work on closest supported version
			return closestVersion;
		}

		/**
		 * Checks if a version is older than some other
		 *
		 * @param version version to check
		 * @return boolean
		 */
		public boolean isOlderThan(ServerVersion version) {
			return getIndex() < version.getIndex();
		}

		/**
		 * Checks if a version is newer than some other
		 *
		 * @param version version to check
		 * @return boolean
		 */
		public boolean isNewerThan(ServerVersion version) {
			return getIndex() > version.getIndex();
		}

		/**
		 * Gets server version closest to desired
		 * or exact if is supported
		 *
		 * @param versionString version string present in craftbukkit package name
		 * @return version
		 */
		public static ServerVersion getClosestVersion(String versionString) {
			versionString = org.apache.commons.lang.StringUtils.replace(versionString, "_", "");
			versionString = org.apache.commons.lang.StringUtils.replace(versionString, "R", "");
			int versionInt = Integer.parseInt(versionString);
			final List<Integer> intVersions = new ArrayList<>();
			intVersions.add(versionInt);
			Map<Integer, ServerVersion> integerServerVersionMap = new HashMap<>();

			for(ServerVersion serverVersion : ServerVersion.values()) {
				String versionString1 = org.apache.commons.lang.StringUtils.replace(serverVersion.name().substring(10), "_", "");
				versionString1 = org.apache.commons.lang.StringUtils.replace(versionString1, "R", "");
				int versionNumber = Integer.parseInt(versionString1);
				intVersions.add(versionNumber);
				integerServerVersionMap.put(versionNumber, serverVersion);
			}

			Collections.sort(intVersions, new Comparator<Integer>() {
				public int compare(Integer o1, Integer o2) {
					return o2 - o1;
				}
			});

			int index;
			for(index = 0; index < intVersions.size(); index++) {
				if(versionInt == intVersions.get(index)) {
					break;
				}
			}

			int targetIndex = index + 1;
			if(intVersions.size() <= targetIndex) {
				targetIndex = intVersions.size() - 2;
			}

			return integerServerVersionMap.get(intVersions.get(targetIndex));
		}

		/**
		 * Gets version index
		 *
		 * @return version index
		 */
		private int getIndex() {
			int index = 1;

			for(ServerVersion version : values()) {
				if(version == this) {
					return index;
				}

				index++;
			}

			return index;
		}
	}

	public static final Map<String, String> essentialsLocale = new HashMap<String, String>() {{
		put("en", "en-en");
		put("pl", "pl-pl");
		put("de", "de-de");
		put("zh", "zh-cn");
	}};

	/**
	 * Reloads the config
	 */
	public void reload() {
		cache.clear();

		if(!new File(plugin.getDataFolder(), "config.yml").exists()) {
			LoggerUtils.info("Creating default config...");
			plugin.saveDefaultConfig();
		}

		File schematicDirectory = new File(NovaGuilds.getInstance().getDataFolder(), "/schematic/");
		if(!schematicDirectory.exists() && schematicDirectory.mkdirs()) {
			LoggerUtils.info("Created schematic/ directory");
		}

		plugin.reloadConfig();
		config = plugin.getConfig();

		LoggerUtils.info("This server is using Bukkit: " + Bukkit.getBukkitVersion());

		if(Config.USETITLES.getBoolean() && getServerVersion().isOlderThan(ServerVersion.MINECRAFT_1_8_R2)) {
			Config.USETITLES.set(false);
			LoggerUtils.error("You can't use Titles with Bukkit other than 1.8");
		}

		if(Config.TABLIST_ENABLED.getBoolean() && getServerVersion() != ServerVersion.MINECRAFT_1_8_R2) {
			Config.TABLIST_ENABLED.set(false);
			LoggerUtils.error("TabList is not currently implemented for server version other than 1.8");
		}

		String primaryDataStorageTypeString = Config.DATASTORAGE_PRIMARY.getString().toUpperCase();
		String secondaryDataStorageTypeString = Config.DATASTORAGE_SECONDARY.getString().toUpperCase();

		boolean primaryValid = false;
		boolean secondaryValid = false;

		if(primaryDataStorageTypeString.equals(secondaryDataStorageTypeString)) {
			LoggerUtils.error("Primary and secondary data storage types cannot be the same!");
			LoggerUtils.error("Resetting to defaults. (MySQL/Flat)");
			primaryDataStorageTypeString = DataStorageType.MYSQL.name();
			secondaryDataStorageTypeString = DataStorageType.FLAT.name();
		}

		for(DataStorageType dst : DataStorageType.values()) {
			if(dst.name().equals(primaryDataStorageTypeString)) {
				primaryValid = true;
			}

			if(dst.name().equals(secondaryDataStorageTypeString)) {
				secondaryValid = true;
			}
		}

		if(!primaryValid || !secondaryValid) {
			LoggerUtils.error("Not valid Data Storage Types.");
			LoggerUtils.error("Resetting to defaults. (MySQL/Flat)");
			primaryDataStorageTypeString = DataStorageType.MYSQL.name();
			secondaryDataStorageTypeString = DataStorageType.FLAT.name();
		}

		if(primaryDataStorageTypeString.equalsIgnoreCase("sqlite") && !Config.DEBUG.getBoolean()) {
			primaryDataStorageTypeString = DataStorageType.MYSQL.name();
			LoggerUtils.error("Please enable debug mode to use SQLite storage.");
		}

		primaryDataStorageType = DataStorageType.valueOf(primaryDataStorageTypeString);
		secondaryDataStorageType = DataStorageType.valueOf(secondaryDataStorageTypeString);
		setToPrimaryDataStorageType();
		LoggerUtils.info("Data storage: Primary: " + primaryDataStorageType.name() + ", Secondary: " + secondaryDataStorageType.name());

		//Effects
		guildEffects.clear();
		List<String> guildEffectsString = Config.GUILD_EFFECT_LIST.getStringList();
		for(String effect : guildEffectsString) {
			PotionEffectType effectType = PotionEffectType.getByName(effect);
			if(effectType != null) {
				guildEffects.add(effectType);
			}
		}

		//Check time values
		if(Config.LIVEREGENERATION_TASKINTERVAL.getSeconds() < 60) {
			LoggerUtils.error("Live regeneration task interval can't be shorter than 60 seconds.");
			Config.LIVEREGENERATION_TASKINTERVAL.set("60s");
		}

		if(Config.CLEANUP_INTERVAL.getSeconds() < 60) {
			LoggerUtils.error("Cleanup interval can't be shorter than 60 seconds.");
			Config.CLEANUP_INTERVAL.set("60s");
		}

		if(Config.SAVEINTERVAL.getSeconds() < 60) {
			LoggerUtils.error("Save interval can't be shorter than 60 seconds.");
			Config.SAVEINTERVAL.set("60s");
		}

		//Bar style enum
		if(getServerVersion().isNewerThan(ServerVersion.MINECRAFT_1_8_R2)) {
			if(Config.BOSSBAR_RAIDBAR_STYLE.toEnum(BarStyle.class) == null) {
				LoggerUtils.error("Invalid BarStyle enum. Resetting to default.");
				Config.BOSSBAR_RAIDBAR_STYLE.set(BarStyle.SOLID.name());
			}

			//Bar color enum
			if(Config.BOSSBAR_RAIDBAR_COLOR.toEnum(BarColor.class) == null) {
				LoggerUtils.error("Invalid BarColor enum. Resetting to default.");
				Config.BOSSBAR_RAIDBAR_COLOR.set(BarColor.PURPLE.name());
			}
		}

		Config.BOSSBAR_ENABLED.set(Config.BOSSBAR_ENABLED.getBoolean() && (getServerVersion().isNewerThan(ServerVersion.MINECRAFT_1_8_R2) || plugin.getDependencyManager().isEnabled(Dependency.BARAPI) || plugin.getDependencyManager().isEnabled(Dependency.BOSSBARAPI)));
		Config.BOSSBAR_RAIDBAR_ENABLED.set(Config.BOSSBAR_RAIDBAR_ENABLED.getBoolean() && Config.BOSSBAR_ENABLED.getBoolean());
		Config.TABLIST_ENABLED.set(Config.TABLIST_ENABLED.getBoolean() && (plugin.getDependencyManager().isEnabled(Dependency.NORTHTAB) && getServerVersion() == ServerVersion.MINECRAFT_1_8_R2));
		Config.HOLOGRAPHICDISPLAYS_ENABLED.set(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean() && plugin.getDependencyManager().isEnabled(Dependency.HOLOGRAPHICDISPLAYS));

		//Run tasks
		plugin.getTaskManager().runTasks();
	}

	/**
	 * Gets data storage type
	 *
	 * @return data storage type enum
	 */
	public DataStorageType getDataStorageType() {
		return dataStorageType;
	}

	/**
	 * Gets available effects for a guild
	 *
	 * @return list of potion effect types
	 */
	public List<PotionEffectType> getGuildEffects() {
		return guildEffects;
	}

	/**
	 * Gets the config as FileConfiguration
	 *
	 * @return the config
	 */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Sets the config to secondary data type
	 */
	public void setToSecondaryDataStorageType() {
		dataStorageType = secondaryDataStorageType;
	}

	/**
	 * Sets the config to primary data type
	 */
	public void setToPrimaryDataStorageType() {
		dataStorageType = primaryDataStorageType;
	}

	/**
	 * Gets a value from cache
	 *
	 * @param c config enum
	 * @return cached object
	 */
	public Object getEnumConfig(Config c) {
		return cache.get(c);
	}

	/**
	 * Checks if a value is present in the cache
	 *
	 * @param c config enum
	 * @return boolean
	 */
	public boolean isInCache(Config c) {
		return cache.containsKey(c);
	}

	/**
	 * Puts an object in cache
	 *
	 * @param c config enum
	 * @param o the object
	 */
	public void putInCache(Config c, Object o) {
		if(!cache.containsKey(c)) {
			cache.put(c, o);
		}
	}

	/**
	 * Removes an object from the cache
	 *
	 * @param c config enum
	 */
	public void removeFromCache(Config c) {
		if(cache.containsKey(c)) {
			cache.remove(c);
		}
	}

	/**
	 * Gets a string
	 *
	 * @param path config path
	 * @return the value
	 */
	public String getString(String path) {
		return config.getString(path) == null ? "" : config.getString(path);
	}

	/**
	 * Gets a string list
	 *
	 * @param path config path
	 * @return the value
	 */
	public List<String> getStringList(String path) {
		return config.getStringList(path) == null ? new ArrayList<String>() : config.getStringList(path);
	}

	/**
	 * Gets a long
	 *
	 * @param path config path
	 * @return the value
	 */
	public long getLong(String path) {
		return config.getLong(path);
	}

	/**
	 * Gets an int
	 *
	 * @param path config path
	 * @return the value
	 */
	public int getInt(String path) {
		return config.getInt(path);
	}

	/**
	 * Gets a double
	 *
	 * @param path config path
	 * @return the value
	 */
	public double getDouble(String path) {
		return config.getDouble(path);
	}

	/**
	 * Gets a boolean
	 *
	 * @param path config path
	 * @return the value
	 */
	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	/**
	 * Gets seconds
	 * Converted from a string
	 * 1y 5d 10h 3s etc.
	 *
	 * @param path config path
	 * @return the value
	 */
	public int getSeconds(String path) {
		return StringUtils.stringToSeconds(getString(path));
	}

	/**
	 * Gets an itemstack
	 * from Essentials compatible string
	 * STONE:1 1 etc.
	 *
	 * @param path config path
	 * @return the value
	 */
	public ItemStack getItemStack(String path) {
		return ItemStackUtils.stringToItemStack(getString(path));
	}

	/**
	 * Gets a material from its name
	 *
	 * @param path config path
	 * @return the value
	 */
	public Material getMaterial(String path) {
		return Material.getMaterial((getString(path).contains(":") ? org.apache.commons.lang.StringUtils.split(getString(path), ':')[0] : getString(path)).toUpperCase());
	}

	/**
	 * Gets material data
	 * from a string after a colon
	 * STONE:1
	 *       ^
	 *
	 * @param path config path
	 * @return the value
	 */
	public byte getMaterialData(String path) {
		return Byte.valueOf(getString(path).contains(":") ? org.apache.commons.lang.StringUtils.split(getString(path), ':')[1] : "0");
	}

	/**
	 * Gets itemstack list
	 *
	 * @param path config path
	 * @return the value
	 */
	public List<ItemStack> getItemStackList(String path) {
		final List<String> stringList = getStringList(path);
		final List<ItemStack> itemStackList = new ArrayList<>();

		for(String string : stringList) {
			ItemStack is = ItemStackUtils.stringToItemStack(string);

			if(is != null) {
				itemStackList.add(is);
			}
		}

		return itemStackList;
	}

	/**
	 * Gets material list
	 *
	 * @param path config path
	 * @return the value
	 */
	public List<Material> getMaterialList(String path) {
		final List<String> stringList = getStringList(path);
		final List<Material> materialList = new ArrayList<>();

		for(String string : stringList) {
			Material material = Material.getMaterial(string);
			if(material != null) {
				materialList.add(material);
			}
		}

		return materialList;
	}

	/**
	 * Gets config file
	 *
	 * @return the file
	 */
	public File getConfigFile() {
		return new File(plugin.getDataFolder(), "config.yml");
	}

	/**
	 * Backups the config
	 *
	 * @throws IOException when something goes wrong
	 */
	public void backupFile() throws IOException {
		File backupFile = new File(getConfigFile().getParentFile(), "config.yml.backup");
		Files.copy(getConfigFile().toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Sets a value in the config
	 *
	 * @param c   config enum
	 * @param obj value
	 */
	public void set(Config c, Object obj) {
		config.set(c.getPath(), obj);
		removeFromCache(c);
	}

	/**
	 * Saves the config to file
	 *
	 * @throws IOException when something goes wrong
	 */
	public void save() throws IOException {
		config.save(getConfigFile());
	}
}
