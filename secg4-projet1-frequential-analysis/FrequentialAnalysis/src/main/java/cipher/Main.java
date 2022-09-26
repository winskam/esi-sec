package cipher;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args[0].equals("Process")) {
            Preprocess.preprocessFile(args[1], args[2], true);
        } else if (args[0].equals("Caesar")) {
            CesarCipher.cipher(args[1], args[2], Integer.parseInt(args[3]));
        } else if (args[0].equals("DecipherCaesar")) {
            CesarCipher.decipher(args[1], args[2], Integer.parseInt(args[3]));
        } else if (args[0].equals("Vigenere")) {
            VigenereCipher.cipher(args[1], args[2], args[3]);
        } else if (args[0].equals("DecipherVigenere")) {
            VigenereCipher.decipher(args[1], args[2], args[3]);
        } else if (args[0].equals("CalculateKeyCaesar")) {
            FrequencyCesar fc = new FrequencyCesar();
            fc.makeFreqBoard(args[1]);
            System.out.println(fc.chiSquare());
            CesarCipher.decipher(args[1], args[2], fc.chiSquare());
        } else if (args[0].equals("CalculateKeyVigenere")) {
            FrequencyVigenere f = new FrequencyVigenere();
            System.out.println(f.getKey(args[1]));
            VigenereCipher.decipher(args[1], args[2], f.getKey(args[1]));
        } else {
            System.out.println("Not a command");
        }
    }

}
