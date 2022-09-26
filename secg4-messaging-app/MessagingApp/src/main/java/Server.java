
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the server of the application.
 *
 * @author 55047 55315
 */
public class Server {

    private int port;
    private List<User> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        new Server(12345).run();
    }

    /**
     * The constructor of the class.
     *
     * @param port the port on which the server will listen.
     */
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    /**
     * Accepts the connections from the clients.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public void run() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        server = new ServerSocket(port);

        System.out.println("Port 12345 is now open.");

        while (true) {
            Socket client = server.accept();

            String in = (new Scanner(client.getInputStream())).nextLine();
            int iend = in.indexOf(":"); //this finds the first occurrence of "."

            if (in.substring(0, 6).equals("login:")) {
                String info = in.substring(iend + 1);

                int iend2 = in.indexOf(":"); //this finds the first occurrence of "."

                String nickname = "";
                String signed = "";
                if (iend2 != -1) {
                    nickname = info.substring(0, iend2);
                    signed = info.substring(iend2 + 1);
                }
                Signature signalg = Signature.getInstance("DSA");
                boolean found = false;
                for (User cl : this.clients) {
                    if (cl.getNickname().equals(nickname)) {

                        signalg.initVerify(cl.getKey());
                        signalg.update("we are signing in".getBytes());
                        byte[] byte_sign = Base64.getDecoder().decode(signed);
                        found = signalg.verify(byte_sign);
                        if (found) {
                            System.out.println("succesfull!!!");
                            System.out.println(cl.getContacts());
                            cl.setSocket(client);
                            new Thread(new UserHandler(this, cl)).start();
                        }
                    }
                }

            } else {
                String info = in.substring(iend + 1);
                int iend2 = info.indexOf(":");

                String nickname = "";
                String key = "";
                if (iend2 != -1) {
                    nickname = info.substring(0, iend2);
                    key = info.substring(iend2 + 1);
                }

                System.out.println("New Client: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

                //System.out.println(key);
                User newUser = new User(client, nickname, key);

                this.clients.add(newUser);

                // create a new thread for newUser incoming messages handling
                new Thread(new UserHandler(this, newUser)).start();
            }

        }
    }

    /**
     * Sends to every contact of a user that he just connected to the chat.
     *
     * @param user the user that just connected.
     */
    public void broadcastLoginContacts(User user) {
        String login = user.getNickname() + " is online";
        for (User client : this.clients) {
            if (user.getContacts().contains(client.getNickname())) {
                client.getOutStream().println(login);
            }
        }
    }

    /**
     * Sends to every contact of a user that he just disconnected from the chat.
     *
     * @param user the user that just disconnected.
     */
    public void broadcastLogoutContacts(User user) {
        String logout = user.getNickname() + " is now offline";
        for (User client : this.clients) {
            if (user.getContacts().contains(client.getNickname())) {
                client.getOutStream().println(logout);
            }
        }
    }

    /**
     * Sends a notification to a user that the userSender wants to become a
     * contact of him.
     *
     * @param userSender the user that wants to become friend.
     * @param user the targeted user.
     */
    public void sendNotification(User userSender, String user) {
        String messageToAdd = userSender.getNickname() + " wants to become your contact, please type add:" + userSender.getNickname() + " to accept (ignore if you added this contact)";

        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                userSender.getOutStream().println("Notification sent");
                client.getOutStream().println(messageToAdd);
            }
        }
    }

    /**
     * Gets the public key of the new contact and sends it to the user who
     * wants. to become contact.
     *
     * @param user the user.
     * @param newContact the new contact.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public void getKey(User user, String newContact) throws NoSuchAlgorithmException, InvalidKeySpecException {
        for (User client : this.clients) {
            if (client.getNickname().equals(newContact) && client != user) {
                String str_key = Base64.getEncoder().encodeToString(client.getKey().getEncoded());
                String messageToAdd = "key:" + client.getNickname() + ":" + str_key;
                user.getOutStream().println(messageToAdd);
            }
        }
    }

    /**
     * Sends a message to a certain user.
     *
     * @param msg the message to send.
     * @param userSender the sender of the message.
     * @param user the destination of the message.
     */
    public void sendMessageToUser(String msg, User userSender, String user) {
        boolean find = false;
        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                find = true;
                if (client.getContacts().contains(userSender.getNickname())) {
                    System.out.println("#" + userSender.getNickname() + " " + msg);
                    client.getOutStream().println("#" + userSender.getNickname() + " " + msg);
                }
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender.toString() + " pas un contact" + msg);
        }
    }
}

/**
 * Class that handles the users on the server.
 *
 * @author 55047 55315
 */
class UserHandler implements Runnable {

    private Server server;
    private User user;

    /**
     * Constructor of the class.
     *
     * @param server the server.
     * @param user the user that must be handled.
     */
    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.broadcastLoginContacts(user);
    }

    /**
     * Getter for the user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Handles the messages that the user is sending.
     */
    public void run() {
        String message;
        boolean stop = false;

        Scanner sc = new Scanner(this.user.getInputStream());
        while (!stop && sc.hasNextLine()) {
            message = sc.nextLine();

            // Gestion des messages private
            if (message.charAt(0) == '@') {
                System.out.println("private msg : " + message);
                int firstSpace = message.indexOf(" ");
                String userPrivate = message.substring(1, firstSpace);
                if (user.getContacts().contains(userPrivate)) {
                    server.sendMessageToUser(message.substring(firstSpace + 1), user, userPrivate);
                } else {
                    System.out.println("Pas un contact");
                }

            } else if (message.length() > 4 && message.substring(0, 4).equals("add:")) {
                String newContact = message.substring(4);
                getUser().addContact(newContact);
                try {
                    server.getKey(user, newContact);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(UserHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    Logger.getLogger(UserHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                server.sendNotification(user, newContact);
            } else if (message.length() > 7 && message.substring(0, 7).equals("delete:")) {
                String to_delete = message.substring(7);
                user.getContacts().remove(to_delete);
                System.out.println(getUser().getContacts());
            } else if (message.equals("logout")) {
                stop = true;
            }
        }

        this.server.broadcastLogoutContacts(user);
        sc.close();
    }
}

/**
 * A user of the chat application.
 *
 * @author 55047 55315
 */
class User {

    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String nickname;
    private Socket client;
    private PublicKey key;
    private List<String> contacts;

    /**
     * Adds a contact to the user.
     *
     * @param nickname the new contact to add.
     */
    public void addContact(String nickname) {
        contacts.add(nickname);
    }

    /**
     * Getter for the public key of this user.
     *
     * @return the public key.
     */
    public PublicKey getKey() {
        return key;
    }

    /**
     * A list with all the contact of this user.
     *
     * @return the list.
     */
    public List<String> getContacts() {
        return contacts;
    }

    /**
     * A setter to set the socket of this user to the right ones.
     *
     * @param client the socket to set.
     * @throws IOException
     */
    public void setSocket(Socket client) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
    }

    /**
     * Constructor of the class.
     *
     * @param client the socket on which this user is connected.
     * @param name the name of this user.
     * @param key the public key of this user as a string.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public User(Socket client, String name, String key) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.nickname = name;
        this.contacts = new ArrayList<>();

        byte[] byte_pubkey = Base64.getDecoder().decode(key);
        KeyFactory factory = KeyFactory.getInstance("DSA");

        this.key = (PublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));

        //System.out.println(this.key);
        this.userId = nbUser;
        nbUser += 1;
    }

    /**
     * Getter for the outStream.
     *
     * @return the outstream.
     */
    public PrintStream getOutStream() {
        return this.streamOut;
    }

    /**
     * Getter for the input stream.
     *
     * @return the inputStream.
     */
    public InputStream getInputStream() {
        return this.streamIn;
    }

    /**
     * Getter for the name of the user.
     *
     * @return the name of this user.
     */
    public String getNickname() {
        return this.nickname;
    }

}
