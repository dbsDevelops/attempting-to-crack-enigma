package es.usj.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.usj.crypto.analysis.EnigmaAnalysis;
import es.usj.crypto.analysis.ScoredEnigmaKey;
import es.usj.crypto.analysis.fitness.IoCFitness;

@SpringBootApplication
public class EnigmaCrackerApplication {
    public static void main(String[] args) {
        try {
            // Load the file as a resource
            InputStream inputStream = EnigmaCrackerApplication.class
                    .getResourceAsStream("/es/usj/crypto/cipher.txt");

            if (inputStream == null) {
                throw new IOException("File not found in resources: /es/usj/crypto/cipher.txt");
            }

            String ciphertextContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Trying to crack the following ciphertext:\n");
            System.out.println(ciphertextContent + "\n");

            // Get 10 best rotor configurations
            IoCFitness fitness = new IoCFitness();
            ScoredEnigmaKey[] bestRotorConfigurations = EnigmaAnalysis.findRotorConfiguration(ciphertextContent, 10, fitness);

            // Print the best rotor configurations
            System.out.println("Best rotor configurations:");
            for (ScoredEnigmaKey key : bestRotorConfigurations) {
                System.out.println("Rotor Configurations: " + Arrays.toString(key.rotorConfigurations)
                 + " Initial positions: " + Arrays.toString(key.rotorPositions) + " Score: " + key.getScore());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}