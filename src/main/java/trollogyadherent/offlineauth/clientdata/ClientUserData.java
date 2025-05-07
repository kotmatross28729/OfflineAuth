package trollogyadherent.offlineauth.clientdata;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trollogyadherent.offlineauth.OfflineAuth;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;

/** Straight up copy of {@link net.minecraftforge.common.UsernameCache}, but client-side */
public class ClientUserData {
	private static Map<UUID, String> map = Maps.newHashMap();
	private static final Charset charset = Charsets.UTF_8;
	public static final File userdatafile = new File(new File(OfflineAuth.rootPath, "userdata.json").getPath());
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger log = LogManager.getLogger(ClientUserData.class);
	
	private ClientUserData() {}
	
	/**
	 * Set a player's current username
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @param username
	 *            the player's username
	 */
	public static void setUsername(UUID uuid, String username) {
		if(uuid == null || username == null) {
			return;
		}
		
		if (username.equals(map.get(uuid))) return;
		
		map.put(uuid, username);
		save();
	}
	
	/**
	 * Remove a player's username from the cache
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return if the cache contained the user
	 */
	public static boolean removeUsername(UUID uuid) {
		if(uuid == null) {
			return false;
		}
		
		if (map.remove(uuid) != null) {
			save();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the player's last known username
	 * <p>
	 * <b>May be <code>null</code></b>
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return the player's last known username, or <code>null</code> if the
	 *         cache doesn't have a record of the last username
	 */
	@Nullable
	public static String getLastKnownUsername(UUID uuid) {
		if(uuid == null) {
			return null;
		}
		return map.get(uuid);
	}
	
	/**
	 * Check if the cache contains the given player's username
	 *
	 * @param uuid
	 *            the player's {@link java.util.UUID UUID}
	 * @return if the cache contains a username for the given player
	 */
	public static boolean containsUUID(UUID uuid) {
		if(uuid == null) {
			return false;
		}
		return map.containsKey(uuid);
	}
	
	/**
	 * Get an immutable copy of the cache's underlying map
	 *
	 * @return the map
	 */
	public static Map<UUID, String> getMap() {
		return ImmutableMap.copyOf(map);
	}
	
	/**
	 * Save the cache to file
	 */
	private static void save() {
		new ClientUserData.SaveThread(gson.toJson(map)).start();
	}
	
	/**
	 * Load the cache from file
	 */
	public static void load() {
		if (!userdatafile.exists()) return;
		
		try {
			String json = Files.toString(userdatafile, charset);
			Type type = new TypeToken<Map<UUID, String>>() {}.getType();
			
			map = gson.fromJson(json, type);
		}
		catch (JsonSyntaxException e) {
			log.error("Could not parse username cache file as valid json, deleting file", e);
			userdatafile.delete();
		}
		catch (IOException e) {
			log.error("Failed to read username cache file from disk, deleting file", e);
			userdatafile.delete();
		}
		finally {
			// Can sometimes occur when the json file is malformed
			if (map == null) {
				map = Maps.newHashMap();
			}
		}
	}
	
	/**
	 * Used for saving the {@link com.google.gson.Gson#toJson(Object) Gson}
	 * representation of the cache to disk
	 */
	private static class SaveThread extends Thread {
		
		/** The data that will be saved to disk */
		private final String data;
		
		public SaveThread(String data) {
			this.data = data;
		}
		
		@Override
		public void run() {
			try {
				// Make sure we don't save when another thread is still saving
				synchronized (userdatafile) {
					Files.write(data, userdatafile, charset);
				}
			}
			catch (IOException e) {
				log.error("Failed to save username cache to file!", e);
			}
		}
	}
}
