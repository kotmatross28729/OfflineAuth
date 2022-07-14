package trollogyadherent.offlineauth.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/* Class that can manipulate private/public key files, sign, and verify signatures */
/* Lots of code taken from here: https://snipplr.com/view/18368/saveload--private-and-public-key-tofrom-a-file */
public class RsaKeyUtil {
    public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
        return genKeyPair(2048);
    }

    public static KeyPair genKeyPair(int size) throws NoSuchAlgorithmException {
        //KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(size);
        return keyPairGen.generateKeyPair();
    }

    public static void SaveKeyPair(String path, KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        File f = new File(path + File.separator + "public.key");
        //f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(publicKey.getEncoded());
        fos.flush();
        fos.close();

        f = new File(path + File.separator + "private.key");
        //f.getParentFile().mkdirs();
        fos = new FileOutputStream(f);
        fos.write(privateKey.getEncoded());
        fos.flush();
        fos.close();
    }

    /* https://stackoverflow.com/questions/52384809/public-key-to-string-and-then-back-to-public-key-java */
    /* One can't do key -> bytes -> string -> bytes -> key, because new String(bytes) does not suport some characters */
    /* Base64 werks */
    public static PublicKey loadPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File publicKeyFile = new File(path);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Files.readAllBytes(publicKeyFile.toPath()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return publicKey;
    }

    public static PrivateKey loadPrivateKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File privateKeyFile = new File(path);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(privateKeyFile.toPath()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    public static PublicKey pubKeyFromString(String pubKeyStr) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] encodedPublicKey = Base64.getDecoder().decode(pubKeyStr);
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        //KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        return publicKey;
    }

    public static PrivateKey privKeyFromString(String privKeyStr) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] encodedPrivateKey = Base64.getDecoder().decode(privKeyStr);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        //KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return privateKey;
    }

    /* Signing requires a DSA keypair btw, you can't sign and encrypt with the same algorithm. */
    /* https://www.tutorialspoint.com/java_cryptography/java_cryptography_verifying_signature.htm */
    /* I'm archiving that shit, it's so useful https://archive.ph/b2pJG */
    public static String signStringWithPrivateKey(String str, String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException, IOException {
        PrivateKey privateKey = privKeyFromString(privateKeyStr);

        Signature sig = Signature.getInstance("SHA256withDSA");
        sig.initSign(privateKey);

        byte[] strBytes = str.getBytes();
        sig.update(strBytes);
        byte[] signedStrBytes = sig.sign();

        String signedStrString = Base64.getEncoder().encodeToString(signedStrBytes);
        return signedStrString;
    }

    /* https://www.tutorialspoint.com/java_cryptography/java_cryptography_verifying_signature.htm */
    public static boolean stringSignatureValid(String originalStr, String signedStr, String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException {
        PublicKey publicKey = pubKeyFromString(publicKeyString);

        Signature sig = Signature.getInstance("SHA256withDSA");
        sig.initVerify(publicKey);

        byte[] encryptedStrBytes = Base64.getDecoder().decode(signedStr);
        byte[] originalStrBytes = originalStr.getBytes();

        sig.update(originalStrBytes);

        return sig.verify(encryptedStrBytes);
    }

    public enum KeyType {
        PUBLIC,
        PRIVATE
    }

    public static String encrypt(String message, Object key, KeyType type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("RSA");
        if (type == KeyType.PRIVATE) {
            cipher.init(Cipher.ENCRYPT_MODE, (PrivateKey)key);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, (PublicKey)key);
        }
        cipher.update(messageBytes);
        byte[] encryptedMessageBytes = cipher.doFinal();
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public static String encryptWithPrivateKey(String message, PrivateKey privKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(message, privKey, KeyType.PRIVATE);
    }

    public static String encryptWithPublicKey(String message, PublicKey pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(message, pubKey, KeyType.PUBLIC);
    }

    public static String decrypt(String message, Object key, KeyType type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance("RSA");
        if (type == KeyType.PRIVATE) {
            cipher.init(Cipher.DECRYPT_MODE, (PrivateKey)key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, (PublicKey)key);
        }
        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);

        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public static String decryptWithPrivateKey(String message, PrivateKey privKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(message, privKey, KeyType.PRIVATE);
    }

    public static String decryptWithPublicKey(String message, PublicKey pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(message, pubKey, KeyType.PUBLIC);
    }

    public static String getKeyFingerprint(PublicKey pubKey) {
        return DigestUtils.sha1Hex(pubKey.getEncoded());
    }


}
