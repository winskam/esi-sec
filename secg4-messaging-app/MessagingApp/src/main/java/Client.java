
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class representing a client in the app.
 *
 * @author 55047 55315
 */
public class Client {

    private String host;
    private int port;
    private KeyPair key;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String login;

    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        new Client("127.0.0.1", 12345).run();
    }

    /**
     * Getter for the login.
     *
     * @return the login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Constructor of the class.
     *
     * @param host the host of the client.
     * @param port the port of the client.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Makes it possible for an existing user to connect to the chat.
     *
     * @return the private key of this user.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PrivateKey login() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keystring = Files.readString(Paths.get(login + ".key"));

        byte[] byte_pubkey = Base64.getDecoder().decode(keystring);

        KeyFactory factory = KeyFactory.getInstance("DSA");
        PrivateKey privateKey = (PrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(byte_pubkey));
        this.privateKey = privateKey;

        return privateKey;
    }

    /**
     * Registers a new user to the chat application.
     *
     * @return the public key of the new user in a string.
     * @throws IOException
     */
    public String register() throws IOException {
        key = generateKeys(512);
        privateKey = key.getPrivate();
        publicKey = key.getPublic();
        String name = login + ".key";

        String str_key = Base64.getEncoder().encodeToString(key.getPublic().getEncoded());
        String priv = Base64.getEncoder().encodeToString(key.getPrivate().getEncoded());

        DataOutputStream dos = new DataOutputStream(new FileOutputStream(name));
        dos.write(priv.getBytes());
        dos.flush();

        return str_key;
    }

    /**
     * Generates a KeyPair.
     *
     * @param size the size of the KeyPair.
     * @return the generated keyPair.
     */
    public KeyPair generateKeys(int size) {
        KeyPairGenerator pairgen = null;
        try {
            pairgen = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
        }
        SecureRandom random = new SecureRandom();
        pairgen.initialize(size, random);

        KeyPair keyPair = pairgen.generateKeyPair();
        return keyPair;
    }

    /**
     * Makes the connection with the server and then reads every line and sends
     * it to the server.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public void run() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Socket client = new Socket(host, port);
        System.out.println("Client successfully connected to server!");

        PrintStream output = new PrintStream(client.getOutputStream());

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a nickname: ");
        login = sc.nextLine();

        File tempFile = new File(login + ".key");
        if (tempFile.exists()) {
            login();
            String message = "we are signing in";
            Signature signingEngine = Signature.getInstance("DSA");
            signingEngine.initSign(privateKey);
            signingEngine.update(message.getBytes());
            byte[] si = signingEngine.sign();

            String signa = Base64.getEncoder().encodeToString(si);

            output.println("login:" + login + ":" + signa);

        } else {
            String publicKey = register();
            output.println("new:" + login + ":" + publicKey);
        }

        new Thread(new ReceivedMessagesHandler(client.getInputStream(), login)).start();

        System.out.println("Messages: \n");
        File historic = new File(login + "historic.txt");
        if (historic.exists()) {
            String content = new String(Files.readAllBytes(Paths.get(login + "historic.txt")));
            System.out.println(content);
        }

        while (sc.hasNextLine()) {
            String message = sc.nextLine();
            if (message.charAt(0) == '@') {
                int firstSpace = message.indexOf(" ");
                String messageToSend = message.substring(firstSpace + 1);
                Signature signingEngine = Signature.getInstance("DSA");
                signingEngine.initSign(privateKey);
                signingEngine.update(messageToSend.getBytes());

                byte[] si = signingEngine.sign();
                String signa = Base64.getEncoder().encodeToString(si);

                FileWriter fw = new FileWriter(login + "historic.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(message);
                bw.newLine();
                bw.close();

                output.println(message + "#" + signa);

            } else {
                output.println(message);
            }
        }
        output.close();
        sc.close();
        client.close();
    }
}

/**
 * Class that handles the message on the inputStream of a client.
 *
 * @author 55047 55315
 */
class ReceivedMessagesHandler implements Runnable {

    private InputStream server;
    private String login;

    /**
     * Constructor of the class.
     *
     * @param server the inputStream with messages from the server.
     * @param login the login of the Client.
     */
    public ReceivedMessagesHandler(InputStream server, String login) {
        this.server = server;
        this.login = login;
    }

    /**
     * Reads every message from the server and handles it.
     */
    public void run() {
        // receive server messages and print out to screen
        Scanner s = new Scanner(server);
        String tmp = "";
        while (s.hasNextLine()) {
            tmp = s.nextLine();
            if (tmp.charAt(0) == 'k') {
                DataOutputStream dos = null;
                try {
                    String keyToAdd = tmp.substring(4);
                    int iend2 = keyToAdd.indexOf(":");
                    String nickname = "";
                    String key = "";
                    if (iend2 != -1) {
                        nickname = keyToAdd.substring(0, iend2);
                        key = keyToAdd.substring(iend2 + 1);
                    }
                    dos = new DataOutputStream(new FileOutputStream(nickname + "Public.key"));
                    dos.write(key.getBytes());
                    dos.flush();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        dos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (tmp.charAt(0) == '#') {
                try {
                    int firstSpace = tmp.indexOf(" ");
                    String sender = tmp.substring(1, firstSpace);
                    String message = tmp.substring(firstSpace + 1);
                    int iend = message.indexOf("#"); //this finds the first occurrence of "."

                    String msg = "";
                    String signature = "";
                    if (iend != -1) {
                        msg = message.substring(0, iend);
                        signature = message.substring(iend + 1);
                    }

                    String keystring = Files.readString(Paths.get(sender + "Public.key"));

                    byte[] byte_pubkey = Base64.getDecoder().decode(keystring);

                    KeyFactory factory = KeyFactory.getInstance("DSA");
                    PublicKey public_key = (PublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));

                    Signature sig = Signature.getInstance("DSA");
                    sig.initVerify(public_key);
                    sig.update(msg.getBytes());

                    byte[] signature_byte = Base64.getDecoder().decode(signature);

                    boolean ok = sig.verify(signature_byte);
                    if (ok) {
                        String to_print = "#" + sender + ":" + msg;
                        System.out.println(to_print);
                        FileWriter fw = new FileWriter(login + "historic.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(to_print);
                        bw.newLine();
                        bw.close();

                    } else {
                        System.out.println("Please contact " + sender + " someone is trying to steal his identity");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SignatureException ex) {
                    Logger.getLogger(ReceivedMessagesHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println(tmp);
            }
        }
        s.close();
    }

}
