package trollogyadherent.offlineauth.registry.cooldown.deobf;

import com.google.gson.JsonObject;

/** Straight up copy of {@link net.minecraft.server.management.UserListEntry}, but the names are readable (no magic number field/func, also without stupid access modifiers) */
public class UserListEntryDeobf {
	private final Object value;
	public UserListEntryDeobf(Object value) {
		this.value = value;
	}
	public UserListEntryDeobf(Object value, JsonObject jsonObject) {
		this.value = value;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public boolean hasExpired() {
		return false;
	}
	
	public void onSerialization(JsonObject data) {}
}
