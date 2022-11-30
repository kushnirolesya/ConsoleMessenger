import javax.crypto.KeyGenerator;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class MainTest {

    public static void main(String[] args) throws Exception {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(128);
        keyGen.init(random);
        String secretKey = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());

//        publicKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
//        privateKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());

        Scanner myObj = new Scanner(System.in);
        System.out.println("ֲגוה³עü שמסü: ");
        String userName = myObj.nextLine();

        String encryptedString = Base64.getEncoder().encodeToString(AES.encrypt(userName, secretKey).getBytes());
        String decryptedString = AES.decrypt(encryptedString, secretKey);
        System.out.println(encryptedString);
        System.out.println(decryptedString);

    }
}
