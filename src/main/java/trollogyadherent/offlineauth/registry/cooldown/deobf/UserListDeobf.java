package trollogyadherent.offlineauth.registry.cooldown.deobf;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Copy of {@link net.minecraft.server.management.UserList}, but the names are readable (no magic number field/func, also without stupid access modifiers) */
public class UserListDeobf {
	protected static final Logger logger = LogManager.getLogger();
	protected final Gson gson;
	private final File saveFile;
	private final Map<String, UserListEntryDeobf> values = Maps.newHashMap();
	private static final ParameterizedType saveFileFormat = new ParameterizedType() {
		public Type[] getActualTypeArguments()
		{
			return new Type[] {UserListEntryDeobf.class};
		}
		public Type getRawType()
		{
			return List.class;
		}
		public Type getOwnerType()
		{
			return null;
		}
	};
	
	public UserListDeobf(File saveFile) {
		this.saveFile = saveFile;
		GsonBuilder gsonbuilder = (new GsonBuilder()).setPrettyPrinting();
		gsonbuilder.registerTypeHierarchyAdapter(UserListEntryDeobf.class, new UserListDeobf.Serializer(null));
		this.gson = gsonbuilder.create();
	}
	public void addEntry(UserListEntryDeobf entry) {
		this.values.put(this.getObjectKey(entry.getValue()), entry);
		
		try {
			this.writeChanges();
		}
		catch (IOException ioexception) {
			logger.warn("Could not save the list after adding a user.", ioexception);
		}
	}
	
	public UserListEntryDeobf getEntry(Object obj) {
		this.removeExpired();
		return this.values.get(this.getObjectKey(obj));
	}
	
	public void removeEntry(Object p_152684_1_) {
		this.values.remove(this.getObjectKey(p_152684_1_));
		
		try {
			this.writeChanges();
		}
		catch (IOException ioexception) {
			logger.warn("Could not save the list after removing a user.", ioexception);
		}
	}
	
	@SideOnly(Side.SERVER)
	public File getSaveFile() {
		return this.saveFile;
	}
	
	public String[] getKeys() {
		return (String[])this.values.keySet().toArray(new String[this.values.size()]);
	}
	
	protected String getObjectKey(Object obj) {
		return obj.toString();
	}
	
	public boolean hasEntry(Object entry) {
		return this.values.containsKey(this.getObjectKey(entry));
	}
	
	private void removeExpired() {
		ArrayList<Object> arraylist = Lists.newArrayList();
		Iterator iterator = this.values.values().iterator();
		
		while (iterator.hasNext()) {
			UserListEntryDeobf userlistentry = (UserListEntryDeobf)iterator.next();
			
			if (userlistentry.hasExpired()) {
				arraylist.add(userlistentry.getValue());
			}
		}
		
		iterator = arraylist.iterator();
		
		while (iterator.hasNext()) {
			Object object = iterator.next();
			this.values.remove(object);
		}
	}
	
	protected UserListEntryDeobf createEntry(JsonObject entryData) {
		return new UserListEntryDeobf(null, entryData);
	}
	
	protected Map<String, UserListEntryDeobf> getValues() {
		return this.values;
	}
	
	public void writeChanges() throws IOException {
		Collection<UserListEntryDeobf> collection = this.values.values();
		String s = this.gson.toJson(collection);
		BufferedWriter bufferedwriter = null;
		
		try {
			bufferedwriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
			bufferedwriter.write(s);
		}
		finally {
			IOUtils.closeQuietly(bufferedwriter);
		}
	}
	
	@SideOnly(Side.SERVER)
	public boolean hasEntries() {
		return this.values.size() < 1;
	}
	
	@SideOnly(Side.SERVER)
	public void readSavedFile() throws IOException {
		Collection collection = null;
		BufferedReader bufferedreader = null;
		
		try {
			bufferedreader = Files.newReader(this.saveFile, Charsets.UTF_8);
			collection = (Collection)this.gson.fromJson(bufferedreader, saveFileFormat);
		}
		finally {
			IOUtils.closeQuietly(bufferedreader);
		}
		
		if (collection != null) {
			this.values.clear();
			
			for (Object o : collection) {
				UserListEntryDeobf userlistentry = (UserListEntryDeobf) o;
				
				if (userlistentry.getValue() != null) {
					this.values.put(this.getObjectKey(userlistentry.getValue()), userlistentry);
				}
			}
		}
	}
	
	class Serializer implements JsonDeserializer, JsonSerializer {
		private Serializer() {}
		
		public JsonElement serializeEntry(UserListEntryDeobf p_152751_1_, Type p_152751_2_, JsonSerializationContext p_152751_3_) {
			JsonObject jsonobject = new JsonObject();
			p_152751_1_.onSerialization(jsonobject);
			return jsonobject;
		}
		
		public UserListEntryDeobf deserializeEntry(JsonElement p_152750_1_, Type p_152750_2_, JsonDeserializationContext p_152750_3_) {
			if (p_152750_1_.isJsonObject()) {
				JsonObject jsonobject = p_152750_1_.getAsJsonObject();
				return UserListDeobf.this.createEntry(jsonobject);
			}
			else {
				return null;
			}
		}
		
		public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
			return this.serializeEntry((UserListEntryDeobf)p_serialize_1_, p_serialize_2_, p_serialize_3_);
		}
		
		public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) {
			return this.deserializeEntry(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
		}
		
		Serializer(Object p_i1141_2_) {
			this();
		}
	}
}
