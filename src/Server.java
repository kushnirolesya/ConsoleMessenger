import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Vector;

public class Server {

    static RSAKeyPairGenerator keyPairGeneratorRSA;

    static {
        try {
            keyPairGeneratorRSA = new RSAKeyPairGenerator();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static String publicKeyRSA = Base64.getEncoder().encodeToString(keyPairGeneratorRSA.getPublicKey().getEncoded());
    static String privateKeyRSA = Base64.getEncoder().encodeToString(keyPairGeneratorRSA.getPrivateKey().getEncoded());

    static int i = 0;
    static Vector<ClientHandler> ar = new Vector<>();

    public static void main(String[] var0) throws IOException {
        ServerSocket var1 = new ServerSocket(3000);
        System.out.println("Сервер розпочав роботу");

        while (true) {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                socket = var1.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("\nстворений новий потік: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket, dataInputStream, dataOutputStream, privateKeyRSA);
                Thread thread = new Thread(clientHandler);
                ar.add(clientHandler);
                thread.start();
                ++i;
            } catch (Exception e) {
                socket.close();
                dataInputStream.close();
                dataOutputStream.close();
                e.printStackTrace();
            }
        }
    }
}
