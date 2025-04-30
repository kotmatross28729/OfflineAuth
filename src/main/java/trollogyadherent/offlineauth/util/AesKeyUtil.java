package trollogyadherent.offlineauth.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/* https://www.baeldung.com/java-aes-encryption-decryption */
public class AesKeyUtil {
    public static SecretKey genSecretKey() throws NoSuchAlgorithmException {
        return genSecretKey(128);
    }

    public static SecretKey genSecretKey(int size) throws NoSuchAlgorithmException {
        //KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(size);
        return keyGen.generateKey();
    }

    public static SecretKey keyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static IvParameterSpec ivFromBytes(byte[] ivbytes) {
        return new IvParameterSpec(ivbytes);
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encryptToStr(String input, SecretKey key, IvParameterSpec iv) throws NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        return Base64.getEncoder().encodeToString(encryptToBytes(input, key, iv));
    }

    public static byte[] encryptToBytes(String input, SecretKey key, IvParameterSpec iv) throws NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input.getBytes());
    }

    public static byte[] encryptToBytes(byte[] input, SecretKey key, IvParameterSpec iv) throws NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input);
    }

    public static byte[] decryptFromString(String cipherText, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(Base64.getDecoder().decode(cipherText));
    }

    public static byte[] decryptFromBytes(byte[] cipherText, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(cipherText);
    }

    public static class AesKeyPlusIv {
        public SecretKey key;
        public IvParameterSpec iv;

        public AesKeyPlusIv(SecretKey key, IvParameterSpec iv) {
            this.key = key;
            this.iv = iv;
        }
    }
}
