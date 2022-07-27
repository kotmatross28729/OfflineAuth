package trollogyadherent.offlineauth.request;

import org.apache.http.entity.ByteArrayEntity;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.request.objects.*;
import trollogyadherent.offlineauth.util.AesKeyUtil;
import trollogyadherent.offlineauth.util.JsonUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RequestUtil {
    public static ByteArrayEntity getChallengeRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String clientPubKeyStr, ServerKeyTokenRegistry.TokenType type) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        ChallengeRequestBodyObject rbo = new ChallengeRequestBodyObject(identifier, clientPubKeyStr, type);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getRegisterRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String displayname, String password, String uuid, String token, String clientPubKeyStr) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        RegisterRequestBodyObject rbo = new RegisterRequestBodyObject(identifier, displayname, password, uuid, token, clientPubKeyStr);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getVibeCheckRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String displayname, String password, String clientKeyToken) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        VibeCheckRequestBodyObject rbo = new VibeCheckRequestBodyObject(identifier, displayname, password, clientKeyToken);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getDeleteAccountRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String password, String clientKeyToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        DeleteAccountRequestBodyObject rbo = new DeleteAccountRequestBodyObject(identifier, password, clientKeyToken);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getChangePasswordRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String password, String newPassword) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        ChangePasswordRequestBodyObject rbo = new ChangePasswordRequestBodyObject(identifier, password, newPassword);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getChangeDisplaynameRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String password, String newDisplayname, String clientKeyToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        ChangeDisplaynameRequestBodyObject rbo = new ChangeDisplaynameRequestBodyObject(identifier, password, newDisplayname, clientKeyToken);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }

    public static ByteArrayEntity getUploadSkinOrCapeRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String password, byte[] imageBytes, String clientKeyToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        UploadSkinOrCapeRequestBodyObject rbo = new UploadSkinOrCapeRequestBodyObject(identifier, password, imageBytes, clientKeyToken);
        String rboJson = JsonUtil.objectToJson(rbo);
        byte[] encryptedData = AesKeyUtil.encryptToBytes(rboJson.getBytes(StandardCharsets.UTF_8), aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(encryptedData);
    }

    public static ByteArrayEntity getRemoveSkinOrCapeRequestBody(AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, String identifier, String password, String clientKeyToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        RemoveSkinOrCapeRequestBodyObject rbo = new RemoveSkinOrCapeRequestBodyObject(identifier, password, clientKeyToken);
        String rboJson = JsonUtil.objectToJson(rbo);
        String encryptedData = AesKeyUtil.encryptToStr(rboJson, aesKeyPlusIv.key, aesKeyPlusIv.iv);
        return new ByteArrayEntity(Base64.getEncoder().encode(encryptedData.getBytes(StandardCharsets.UTF_8)));
    }
}
