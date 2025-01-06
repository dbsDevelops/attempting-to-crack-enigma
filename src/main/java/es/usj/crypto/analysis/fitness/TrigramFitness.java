package es.usj.crypto.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class TrigramFitness extends FitnessFunction {
    private float[] trigrams;

    private static int triIndex(int a, int b, int c) {
        return (a << 10) | (b << 5) | c;
    }

    public TrigramFitness() {
        // Trigrams
        this.trigrams = new float[26426];
        Arrays.fill(this.trigrams, (float)Math.log10(epsilon));
        try (final InputStream is = TrigramFitness.class.getResourceAsStream("/es/usj/crypto/data/text/trigrams");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        String key = s[0];
                        int i = triIndex(key.charAt(0) - 65, key.charAt(1) - 65, key.charAt(2) - 65);
                        this.trigrams[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.trigrams = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        int current = -1, next1 = -1, next2 = -1; // Initialize with invalid indices

        for (char c : text) {
            if (Character.isAlphabetic(c)) {
                int index = Character.toUpperCase(c) - 'A'; // Convert to uppercase and get index

                // Shift the indices for trigram calculation
                current = next1;
                next1 = next2;
                next2 = index;

                // If we have enough valid characters, compute the trigram fitness
                if (current != -1 && next1 != -1 && next2 != -1) {
                    fitness += this.trigrams[triIndex(current, next1, next2)];
                }
            }
        }

        return fitness;
    }
}