package es.usj.crypto.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class BigramFitness extends FitnessFunction {
    private float[] bigrams;

    private static int biIndex(int a, int b) {
        return (a << 5) | b;
    }


    public BigramFitness() {
        // Bigrams
        this.bigrams = new float[826];
        Arrays.fill(this.bigrams, (float)Math.log10(epsilon));
        try (final InputStream is = BigramFitness.class.getResourceAsStream("/es/usj/crypto/data/text/bigrams");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r);
             final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        String key = s[0];
                        int i = biIndex(key.charAt(0) - 65, key.charAt(1) - 65);
                        this.bigrams[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.bigrams = null;
        }
    }

    @Override
    public float score(char[] text) {
        float fitness = 0;
        int current = -1; // Initialize with an invalid index

        for (char c : text) {
            if (Character.isAlphabetic(c)) {
                int next = Character.toUpperCase(c) - 'A';

                // If we already have a valid previous character, calculate the bigram fitness
                if (current != -1) {
                    fitness += this.bigrams[biIndex(current, next)];
                }

                // Update the current character
                current = next;
            }
        }

        return fitness;
    }
}