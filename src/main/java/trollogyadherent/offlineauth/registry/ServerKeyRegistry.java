package trollogyadherent.offlineauth.registry;

import trollogyadherent.offlineauth.util.AesKeyUtil;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerKeyRegistry {
    private HashMap<String, AesKeyUtil.AesKeyPlusIv> map;
    private HashMap<String, Integer> ipHostsUsagecount;

    public ServerKeyRegistry() {
        map = new HashMap<>();
        ipHostsUsagecount = new HashMap<>();
        Runnable clearRegistryRunnable = this::clear;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(clearRegistryRunnable, 0, 5, TimeUnit.SECONDS);
    }

    String concatIpHost(String ip, String host) {
        return ip + ":" + host;
    }

    AesKeyUtil.AesKeyPlusIv insertAesKeyPlusIv(String ip, String host) throws NoSuchAlgorithmException {
        AesKeyUtil.AesKeyPlusIv akpiv = new AesKeyUtil.AesKeyPlusIv(AesKeyUtil.genSecretKey(), AesKeyUtil.generateIv());
        map.put(concatIpHost(ip, host), akpiv);
        ipHostsUsagecount.put(concatIpHost(ip, host), 0);
        return akpiv;
    }

    public AesKeyUtil.AesKeyPlusIv getAesKeyPlusIv(String ip, String host) throws NoSuchAlgorithmException {
        if (map.get(concatIpHost(ip, host)) != null) {
            if (ipHostsUsagecount.get(concatIpHost(ip, host)) == 1) {
                return insertAesKeyPlusIv(ip, host);
            } else {
                ipHostsUsagecount.put(concatIpHost(ip, host), 1);
                return map.get(concatIpHost(ip, host));
            }
        } else {
            return insertAesKeyPlusIv(ip, host);
        }
    }

    public boolean ipHasKeyPair(String ip, String host) {
        return map.get(concatIpHost(ip, host)) != null;
    }

    public void remove(String ip, String host) {
        map.remove(concatIpHost(ip, host));
    }

    public void clear() {
        map = new HashMap<>();
    }
}
