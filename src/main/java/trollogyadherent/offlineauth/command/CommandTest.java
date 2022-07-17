package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.ResetCachesPacket;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CommandTest implements ICommand {
    private final List aliases;

    public CommandTest()
    {
        aliases = new ArrayList();
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "test";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/test";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
            return;
        }

        sender.addChatMessage(new ChatComponentText(OfflineAuth.varInstanceServer.playerRegistry.toString()));


        /* try {
            String enc = KeyUtil.signStringWithPrivateKey("sneed", KeyUtil.loadPrivateKey("/home/jack/Projects/Modding/OfflineAuth/run/Keys/private.key"));

            System.out.println(KeyUtil.stringSignatureValid("sneed", enc, KeyUtil.loadPublicKey("/home/jack/Projects/Modding/OfflineAuth/run/Keys/public.key")));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException |
                 IOException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        } */

        /* Full working example of signing and verifying the signature of a string. Also, the private key passes through a file, as a proof of concept. */
        /*try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey privKey = pair.getPrivate();
            PublicKey pubKey = pair.getPublic();


            byte[] key = pubKey.getEncoded();
            FileOutputStream keyfos = new FileOutputStream("pub.key");
            keyfos.write(key);
            keyfos.close();

            FileInputStream keyfis = new FileInputStream("pub.key");
            byte[] encKey = new byte[keyfis.available()];
            keyfis.read(encKey);
            keyfis.close();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
            PublicKey pubKey2 = keyFactory.generatePublic(pubKeySpec);

            Signature sign = Signature.getInstance("SHA256withDSA");
            sign.initSign(privKey);
            byte[] bytes = "Hello how are you".getBytes();
            sign.update(bytes);
            byte[] signature = sign.sign();

            sign.initVerify(pubKey2);
            sign.update(bytes);
            boolean bool = sign.verify(signature);
            System.out.println(bool);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException |
                 NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }*/


        /*List<String> algorithms = Arrays.stream(Security.getProviders())
                .flatMap(provider -> provider.getServices().stream())
                .filter(service -> "Cipher".equals(service.getType()))
                .map(Provider.Service::getAlgorithm)
                .collect(Collectors.toList());

        for (String s : algorithms) {
            sender.addChatMessage(new ChatComponentText(s));
        }

        if (argString.length == 0) {
            return;
        }

        try {
            String encryptedMessage = KeyUtil.encryptWithPrivateKey(argString[0], ServerUtil.loadServerPrivateKey());
            sender.addChatMessage(new ChatComponentText("Encrypted with private: " + encryptedMessage));

            String decryptedMessage = KeyUtil.decryptWithPublicKey(encryptedMessage, ServerUtil.loadServerPublicKey());
            sender.addChatMessage(new ChatComponentText("Decrypted with public: " + decryptedMessage));

            String encryptedMessage2 = KeyUtil.encryptWithPublicKey(argString[0], ServerUtil.loadServerPublicKey());
            sender.addChatMessage(new ChatComponentText("Encrypted with public: " + encryptedMessage2));

            String decryptedMessage2 = KeyUtil.decryptWithPrivateKey(encryptedMessage2, ServerUtil.loadServerPublicKey());
            sender.addChatMessage(new ChatComponentText("Decrypted with private: " + decryptedMessage2));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException(e);
        }*/


            /*
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();

            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            FileOutputStream fos = new FileOutputStream("public_test.key");
            fos.write(publicKey.getEncoded());
            FileOutputStream fos_ = new FileOutputStream("private_test.key");
            fos_.write(privateKey.getEncoded());

            File publicKeyFile = new File("public_test.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            File privateKeyFile = new File("private_test.key");
            byte[] privateKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            keyFactory.generatePublic(publicKeySpec);
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
            keyFactory.generatePrivate(publicKeySpec);
             */

            /*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();

            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            File f = new File("public_test.key");
            //f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(publicKey.getEncoded());
            fos.flush();
            fos.close();

            f = new File("private_test.key");
            //f.getParentFile().mkdirs();
            fos = new FileOutputStream(f);
            fos.write(privateKey.getEncoded());
            fos.flush();
            fos.close();


            File publicKeyFile = new File("public_test.key");
            publicKey = null;
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Files.readAllBytes(publicKeyFile.toPath()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);

            File privateKeyFile = new File("private_test.key");
            privateKey = null;
            PKCS8EncodedKeySpec keySpec_ = new PKCS8EncodedKeySpec(Files.readAllBytes(privateKeyFile.toPath()));
            keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec_);*/

            //String encryptedMsg = KeyUtil.encrypt("sneed", KeyUtil.loadPrivateKey(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "private.key"), KeyUtil.KeyType.PRIVATE);

            if (argString.length == 0) {

            }   else {
                System.out.println("sending delete caches packets");
                for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ResetCachesPacket.SimpleMessage();
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }
            }


        try {
            ServerSkinUtil.transferDefaultSkins();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OfflineAuth.info(sender.getCommandSenderName() + " issued test command");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }
}
