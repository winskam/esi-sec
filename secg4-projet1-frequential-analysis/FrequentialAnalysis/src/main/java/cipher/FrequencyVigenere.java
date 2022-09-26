package cipher;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FrequencyVigenere {

    private ArrayList<Integer> distance = new ArrayList<>();
    private int gcd = 1;
    private ArrayList<double[]> freqs = new ArrayList<>();
    double[] freqEnglish = {8.2, 1.5, 2.8, 4.3, 13, 2.2, 2, 6.1, 7, 0.15, 0.77, 4, 2.4, 6.7,
        7.5, 1.9, 0.095, 6, 6.3, 9.1, 2.8, 0.98, 2.4, 0.15, 2, 0.074};

    public void findRepetition(String input)
            throws FileNotFoundException, IOException {
        Scanner in = new Scanner(new FileReader(input));
        String line = in.nextLine();
        for (int i = 0; i < line.length() - 6 && distance.size() < 20; i++) {
            for (int j = i + 3; j < line.length() && distance.size() < 3; j++) {
                if (line.charAt(i) == line.charAt(j)) {
                    int length_subString = 1;
                    for (int x = 1; x < line.length() - j; x++) {
                        if (line.charAt(i + x) == line.charAt(j + x)) {
                            length_subString++;
                        } else {
                            break;
                        }
                    }
                    if (length_subString == 3) {
                        distance.add(j - i);
                        gcdArray();
                        if (distance.size() > 1 && gcd == 1) {
                            distance.remove(distance.size() - 1);
                        }
                    }
                }
            }
        }
    }

    int gcd(int a, int b) {
        if (a == 0) {
            return b;
        }
        return gcd(b % a, a);
    }

    void gcdArray() {
        int result = 0;
        for (int element : distance) {
            result = gcd(result, element);

        }
        gcd = result;
    }

    public void makeFreqBoard(String input)
            throws FileNotFoundException, IOException {
        Scanner in = new Scanner(new FileReader(input));
        String line = in.nextLine();
        for (int x = 0; x < gcd; x++) {
            double[] freq = new double[26];
            for (int i = x; i < line.length(); i += gcd) {
                freq[(int) (line.charAt(i) - 97)]++;
            }
            for (int i = 0; i < freq.length; i++) {
                freq[i] = (freq[i] / (line.length() / gcd)) * 100;
            }
            freqs.add(freq);
        }
    }

    public String chiSquare() {
        String keys = "";
        for (int i = 0; i < gcd; i++) {
            double[] freq = freqs.get(i);
            double minChi = 10000;
            int key = 0;
            for (int possibleKey = 0; possibleKey < 26; possibleKey++) {
                double chi = 0;
                for (int letter = 0; letter < 26; letter++) {
                    chi += Math.pow(freq[(letter + possibleKey) % 26], 2) / freqEnglish[letter];
                }
                if (chi < minChi) {
                    minChi = chi;
                    key = possibleKey;
                }
            }
            keys += Character.toString(key + 97);
        }
        return keys;
    }

    public String getKey(String input) throws IOException {
        findRepetition(input);
        makeFreqBoard(input);
        return chiSquare();
    }

}
