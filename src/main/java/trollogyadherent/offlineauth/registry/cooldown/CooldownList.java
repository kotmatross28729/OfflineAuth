package trollogyadherent.offlineauth.registry.cooldown;

import com.google.gson.JsonObject;
import trollogyadherent.offlineauth.registry.cooldown.deobf.UserListDeobf;
import trollogyadherent.offlineauth.registry.cooldown.deobf.UserListEntryDeobf;
import trollogyadherent.offlineauth.util.Util;

import java.io.File;
import java.net.SocketAddress;

public class CooldownList extends UserListDeobf {
	public CooldownList(File cooldownsFile) {
		super(cooldownsFile);
	}
	protected UserListEntryDeobf createEntry(JsonObject entryData) {
		return new CooldownEntry(entryData);
	}
	
	public boolean hasEntryInCooldownList(String ip) {
		return this.hasEntry(ip);
	}
	
	public CooldownEntry getCooldownEntry(String ip) {
		return (CooldownEntry)this.getEntry(ip);
	}
	
	public boolean hasEntryInCooldownList(SocketAddress address) {
		String s = this.addressToString(address);
		return this.hasEntry(s);
	}
	public CooldownEntry getCooldownEntry(SocketAddress address) {
		String s = this.addressToString(address);
		return (CooldownEntry)this.getEntry(s);
	}
	
	private String addressToString(SocketAddress address) {
		String ipport = address.toString();
	
		return Util.getIPUniversal(ipport);
	}
}
