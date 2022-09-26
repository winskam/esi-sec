package cipher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class VigenereCipher {

    public static void cipher(String input, String output, String key)
            throws FileNotFoundException, IOException {
        Scanner in = new Scanner(new FileReader(input));
        PrintStream out = new PrintStream(new FileOutputStream(output));
        int[] offset = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            offset[i] = key.charAt(i) - 97;
        }
        String line = in.nextLine();
        for (int i = 0; i < line.length(); i++) {
            int posInOffset = i % key.length();
            int newPos = (Character.toLowerCase(line.charAt(i)) + offset[posInOffset] - 97) % 26;
            char newChar = (newPos < 0) ? (char) (newPos + 123) : (char) (newPos + 97);
            out.print(newChar);
        }
    }

    public static void decipher(String input, String output, String key)
            throws FileNotFoundException, IOException {
        Scanner in = new Scanner(new FileReader(input));
        PrintStream out = new PrintStream(new FileOutputStream(output));
        int[] offset = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            offset[i] = key.charAt(i) - 97;
        }
        String line = in.nextLine();
        for (int i = 0; i < line.length(); i++) {
            int posInOffset = i % key.length();
            int newPos = (Character.toLowerCase(line.charAt(i)) - offset[posInOffset] - 97) % 26;
            char newChar = (newPos < 0) ? (char) (newPos + 123) : (char) (newPos + 97);
            out.print(newChar);
        }
    }

}
