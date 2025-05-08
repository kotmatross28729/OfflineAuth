package trollogyadherent.offlineauth.registry.cooldown;

import com.google.gson.JsonObject;
import trollogyadherent.offlineauth.registry.cooldown.deobf.UserListEntryDeobf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CooldownEntry extends UserListEntryDeobf {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date lastReg;
	protected final Date lastRegCooldownEnd;
	
	public CooldownEntry(Object ip, Date regTime, Date regCooldown) {
		super(ip);
		this.lastReg = regTime == null ? new Date() : regTime;
		this.lastRegCooldownEnd = regCooldown;
	}
	
	public CooldownEntry(JsonObject jsonObject) {
		super(getIpFromJson(jsonObject), jsonObject);
		
		Date lastReg;
		try {
			lastReg = jsonObject.has("lastReg") ? dateFormat.parse(jsonObject.get("lastReg").getAsString()) : new Date();
		} catch (ParseException parseexception) {
			lastReg = new Date();
		}
		this.lastReg = lastReg;
		
		Date cdEnd;
		try {
			cdEnd = jsonObject.has("lastRegCooldownEnd") ? dateFormat.parse(jsonObject.get("lastRegCooldownEnd").getAsString()) : null;
		}
		catch (ParseException parseexception) {
			cdEnd = null;
		}
		this.lastRegCooldownEnd = cdEnd;
	}
	
	@Override
	public boolean hasExpired() {
		return this.lastRegCooldownEnd != null && this.lastRegCooldownEnd.before(new Date());
	}
	
	private static String getIpFromJson(JsonObject jsonObject) {
		return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
	}
	
	public Date getLastSuccessfulRegistration() {
		return this.lastReg;
	}
	
	public Date getCooldownEndDate() {
		return this.lastRegCooldownEnd;
	}
	
	
	public String remainedTime() {
		if (this.lastRegCooldownEnd == null) {
			return "offlineauth.db.cooldown.never";
		}
		long remainingMillis = this.lastRegCooldownEnd.getTime() - new Date().getTime();
		if (remainingMillis > 0) {
			long days = TimeUnit.MILLISECONDS.toDays(remainingMillis);
			long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24;
			long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60;
			long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60;
			
			return String.format("%dD: %02dH: %02dM: %02dS ", days, hours, minutes, seconds);
		}
		return null;
	}
	
	
	@Override
	public void onSerialization(JsonObject data) {
		if (this.getValue() != null) {
			data.addProperty("ip", (String)this.getValue());
			data.addProperty("lastReg", dateFormat.format(this.lastReg));
			data.addProperty("lastRegCooldownEnd", this.lastRegCooldownEnd == null ? "never" : dateFormat.format(this.lastRegCooldownEnd));
		}
	}
	
}
