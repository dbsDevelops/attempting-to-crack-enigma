package dbsdevelops.analysis;

import java.util.HashMap;
import java.util.Map;

public class IndexOfCoincidence {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public double calculate(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (char c : ALPHABET.toCharArray()) {
            frequency.put(c, 0);
        }

        // Count occurrences of each letter
        for (char c : text.toCharArray()) {
            if (frequency.containsKey(c)) {
                frequency.put(c, frequency.get(c) + 1);
            }
        }

        int length = text.length();
        double ic = 0.0;

        // Calculate the Index of Coincidence
        for (char c : ALPHABET.toCharArray()) {
            int freq = frequency.get(c);
            ic += (freq * (freq - 1));
        }
        
        ic /= (length * (length - 1));
        return ic;
    }
}