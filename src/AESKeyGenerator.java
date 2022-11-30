import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESKeyGenerator {
    private final SecretKey secretKey;

    public AESKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(128);
        keyGen.init(random);
        this.secretKey = keyGen.generateKey();
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}