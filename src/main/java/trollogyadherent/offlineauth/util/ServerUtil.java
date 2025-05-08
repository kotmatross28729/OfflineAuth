package trollogyadherent.offlineauth.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import trollogyadherent.offlineauth.OfflineAuth;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class ServerUtil {
    public static void kickPlayerByName(String name, String reason) {
        if (MinecraftServer.getServer() == null) {
            return;
        }
        for (EntityPlayerMP playerMP : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerMP.getDisplayName().equals(name)) {
                playerMP.playerNetServerHandler.kickPlayerFromServer(reason);
                return;
            }
        }
    }

    public static void generateServerKeys() throws NoSuchAlgorithmException, IOException {
        File keyPairFolder = new File(OfflineAuth.varInstanceServer.keyPairPath);
        if (!keyPairFolder.exists()) {
            keyPairFolder.mkdirs();
        }

        File privKeyFile = new File(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "private.key");
        File pubKeyFile = new File(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "public.key");
        if (!privKeyFile.exists() || !pubKeyFile.exists()) {
            RsaKeyUtil.SaveKeyPair(OfflineAuth.varInstanceServer.keyPairPath, RsaKeyUtil.genKeyPair());
        }
    }

    public static PrivateKey loadServerPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return RsaKeyUtil.loadPrivateKey(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "private.key");
    }

    public static PublicKey loadServerPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return RsaKeyUtil.loadPublicKey(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "public.key");
    }
}
