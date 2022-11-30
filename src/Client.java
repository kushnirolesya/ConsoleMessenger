import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    private static DataInputStream in = null;
    private static DataOutputStream out = null;
    private static final AESKeyGenerator aesGenerator;

    static {
        try {
            aesGenerator = new AESKeyGenerator();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String secretKey = Base64.getEncoder().encodeToString(aesGenerator.getSecretKey().getEncoded());

    public static void main(String[] var0) throws IOException {
        final Scanner var1 = new Scanner(System.in);
        System.out.println("Привіт, я новий клієнт!");
        Socket socket = new Socket("127.0.0.1", 3000);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        try {
            String encSecretKey = Base64.getEncoder().encodeToString(RSA.encrypt(secretKey, Server.publicKeyRSA));
            System.out.println("Надсилаю на сервер свій зашифрований секретний ключ: " + encSecretKey);
            out.writeUTF(encSecretKey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Thread sendMessage = new Thread(() -> {
            System.out.println("-------------------------------- \n|  Введіть: " +
                    "\n|  $нікнейм - щоб змінити свій нікнейм в системі " +
                    "\n|  @користувач - щоб з'єднатися з користувачем, якому ви хочете надіслати повідомлення " +
                    "\n|  * - щоб від'єднатися від чату " +
                    "\n|  % - щоб побачити всіх користувачів, які зараз знаходяться в мережі " +
                    "\n|  'вихід' щоб вийти з програми \n-----------------------------");

            while (true) {
                String msg = var1.nextLine();

                try {
                    Client.out.writeUTF(AES.encrypt(msg, secretKey));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String msg = Client.in.readUTF();
                    String aesdec = AES.decrypt(msg, Client.secretKey);
                    if (aesdec.contains("приєднюється до переписки з тобою")) {
                        System.out.println("----------\nотримав ключ від іншого користувача: \n----------");
                    }
                    System.out.println(aesdec);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendMessage.start();
        readMessage.start();
    }
}
