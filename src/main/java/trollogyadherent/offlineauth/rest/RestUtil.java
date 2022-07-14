package trollogyadherent.offlineauth.rest;

import trollogyadherent.offlineauth.request.objects.*;
import trollogyadherent.offlineauth.util.AesKeyUtil;
import trollogyadherent.offlineauth.util.JsonUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RestUtil {
    public static Object getRequestBodyObject(byte[] bytes, AesKeyUtil.AesKeyPlusIv aesKeyPlusIv, Class class_) {
        String rbo_json_encrypted = new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8);
        String rbo_json;
        try {
            rbo_json = new String(AesKeyUtil.decryptFromString(rbo_json_encrypted, aesKeyPlusIv.key, aesKeyPlusIv.iv));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            return null;
        }
        return JsonUtil.jsonToObject(rbo_json, class_);
    }
}
