import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class ClientHandler implements Runnable {
    DataOutputStream dataOutputStream;
    Socket s;
    DataInputStream dataInputStream;
    String name = "анонім";
    String privateKey;
    boolean isLogged;

    private static final AESKeyGenerator aesGenerator;

    static {
        try {
            aesGenerator = new AESKeyGenerator();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    String secretKey = Base64.getEncoder().encodeToString(aesGenerator.getSecretKey().getEncoded());

    public ClientHandler(Socket s, DataInputStream dataInputStream, DataOutputStream dataOutputStream, String privateKey) {
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        this.s = s;
        this.privateKey = privateKey;
        this.isLogged = true;
    }

    public void run() {
        String encMsg = "";
        String connectName = "";
        ClientHandler clientHandler = null;

        String decrMsg;
        try {
            encMsg = this.dataInputStream.readUTF();
            System.out.println("сервер отримав зашифрований секретний ключ клієнта: " + encMsg + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        while (true) {
            try {
                decrMsg = AES.decrypt(encMsg, secretKey);
                if (decrMsg.equals("вихід")) {
                    this.isLogged = false;
                    break;
                }
                System.out.println("####################################");
                System.out.println("зашифроване повідомлення: " + encMsg);
                System.out.println("розшифроване повідомлення: " + decrMsg);
                System.out.println("####################################");
                if (decrMsg.equals("*")) {
                    if (clientHandler != null)
                        clientHandler.dataOutputStream.writeUTF(AES.encrypt("-> " + this.name + " від'єднується від чату", this.secretKey));
                }
                else if (decrMsg.charAt(0) == '$') {
                    String msg = "ваш нікнейм успішно змінено";

                    for (ClientHandler client : Server.ar) {
                        if (client.name.equals(decrMsg.substring(1)) && client.isLogged) {
                            msg = "дане ім'я користувача вже існує";
                            break;
                        }
                    }

                    if (!msg.equals("дане ім'я користувача вже існує")) {
                        this.name = decrMsg.substring(1);
                    }

                    this.dataOutputStream.writeUTF(AES.encrypt(msg, this.secretKey));
                }
                else if (decrMsg.charAt(0) == '@') {
                    if (!decrMsg.equals("@1") && !decrMsg.equals("@0")) {
                        connectName = decrMsg.substring(1);

                        for (ClientHandler client : Server.ar) {
                            if (client.name.equals(connectName) && client.isLogged) {
                                clientHandler = client;
                            }
                        }
                    }
                    if (clientHandler != null) {
                        this.secretKey = clientHandler.secretKey;
                        if (!decrMsg.equals("@1") && !decrMsg.equals("@0")) {
                            clientHandler.dataOutputStream.writeUTF(AES.encrypt("-> " + this.name + " приєднюється до переписки з тобою", this.secretKey));
                        }

                        System.out.println("Успішне з'єднання");

                        try {
                            encMsg = Base64.getEncoder().encodeToString(RSA.encrypt(this.secretKey, Server.publicKeyRSA));
                            System.out.println("зашифрований секретний ключ: " + encMsg);
                            clientHandler.dataOutputStream.writeUTF(encMsg);
                        } catch (Exception var11) {
                            System.err.println(var11.getMessage());
                        }

                        this.dataOutputStream.writeUTF(AES.encrypt("вас під'єднано до користувача " + connectName, this.secretKey));
                    } else {
                        this.dataOutputStream.writeUTF(AES.encrypt("неможливо під'єднатися до користувача " + connectName, this.secretKey));
                    }
                }
                else if (decrMsg.equals("%")) {
                    ClientHandler clr = null;
                    StringBuilder s = new StringBuilder("++++++++++++++\nonline users:\n");
                    for (ClientHandler mc : Server.ar) {
                        if ((mc.name).equals(this.name))
                            clr = mc;
                        s.append(mc.name).append("\n");
                    }
                    s.append("++++++++++++++");

                    if (clr != null)
                        clr.dataOutputStream.writeUTF(AES.encrypt(s.toString(), secretKey));
                }
                else if (clientHandler != null) {
                    clientHandler.dataOutputStream.writeUTF(AES.encrypt(this.name + ": " + decrMsg, this.secretKey));
                }
                else {
                    this.dataOutputStream.writeUTF(AES.encrypt("ви не під'єднані до жодного користувача ", this.secretKey));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
            this.s.close();
        } catch (
                IOException var9) {
            var9.printStackTrace();
        }

    }
}
