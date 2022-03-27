package trollogyadherent.offlineauth.util;

import net.minecraft.server.MinecraftServer;

public class ClientUtil {
    public static boolean isSinglePlayer()
    {
        try
        {
            if(MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning() )
            {
                return MinecraftServer.getServer().isSinglePlayer();
            }
            return false;
        }
        catch( Exception e ) // Server is null, not started
        {
            return false;
        }
    }
}
