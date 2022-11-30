import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AES {

    public static String encrypt(String data, String key) throws Exception {
        byte[] contentBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedBytes);
    }

    public static String decrypt(String data, String key) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedBytes = decoder.decode(data);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws Exception {
        return cipherOperation(contentBytes, keyBytes, 1);
    }

    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws Exception {
        return cipherOperation(contentBytes, keyBytes, 2);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        byte[] bytes = "A-16-Byte-String".getBytes(StandardCharsets.UTF_8);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ivParameterSpec);
        return cipher.doFinal(contentBytes);
    }
}
