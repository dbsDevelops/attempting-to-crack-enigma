package es.usj.crypto;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.usj.crypto.analysis.RotorOptimiser;
import es.usj.crypto.analysis.ScoredEnigmaKey;
import es.usj.crypto.analysis.fitness.BigramFitness;
import es.usj.crypto.analysis.fitness.IoCFitness;
import es.usj.crypto.analysis.fitness.SingleCharacterFitness;
import es.usj.crypto.analysis.fitness.TrigramFitness;
import es.usj.crypto.enigma.Machine;
import es.usj.crypto.enigma.Plugboard;
import es.usj.crypto.enigma.Reflector;
import es.usj.crypto.enigma.Rotor;
import es.usj.crypto.enigma.constant.ReflectorConfiguration;
import es.usj.crypto.analysis.PlugboardHillClimber;

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
            IoCFitness iocFitness = new IoCFitness();
            ScoredEnigmaKey[] optimisedRotorConfigurations = RotorOptimiser.findRotorConfiguration(ciphertextContent, 10, iocFitness);

            // Print the best rotor configurations
            System.out.println("Best rotor configurations:");
            for (ScoredEnigmaKey key : optimisedRotorConfigurations) {
                System.out.println("Rotor Configurations: " + Arrays.toString(key.rotorConfigurations)
                 + " Initial positions: " + Arrays.toString(key.rotorPositions) + " Score: " + key.getScore());
            }

            // Get the best plugboard configuration
            SingleCharacterFitness singleCharacterFitness = new SingleCharacterFitness();
            BigramFitness bigramFitness = new BigramFitness();
            TrigramFitness trigramFitness = new TrigramFitness();
            
            // Optimise the plugboards for each rotor configuration
            Map<ScoredEnigmaKey, String> optimisedPlugboardConfigurations = PlugboardHillClimber.optimisePlugboard(
                ciphertextContent, optimisedRotorConfigurations, singleCharacterFitness, bigramFitness, trigramFitness, 10000, 
                "plugboard_hill_climber.log", "plugboards.txt", "plugboards.txt");

            // Prepare file for results
            try (FileWriter writer = new FileWriter("decrypted_texts.txt", StandardCharsets.UTF_8)) {
                // Decrypt the ciphertext with each configuration and save the results
                for (Map.Entry<ScoredEnigmaKey, String> entry : optimisedPlugboardConfigurations.entrySet()) {
                    ScoredEnigmaKey key = entry.getKey();
                    String plugboardConfig = entry.getValue();

                    // Create rotors
                    Rotor[] rotors = new Rotor[3];
                    for (int i = 0; i < 3; i++) {
                        rotors[i] = new Rotor(key.rotorConfigurations[i], (char) ('A' + key.rotorPositions[i]));
                    }

                    // Create plugboard and reflector
                    Plugboard plugboard = new Plugboard(plugboardConfig);
                    Reflector reflector = new Reflector(ReflectorConfiguration.REFLECTOR_DEFAULT);

                    // Create the Enigma machine
                    Machine machine = new Machine(plugboard, rotors[0], rotors[1], rotors[2], reflector);

                    // Decrypt the ciphertext
                    String decryptedText = machine.getCipheredText(ciphertextContent);

                    // Write results to file
                    writer.write("Rotor Configuration: " + Arrays.toString(key.rotorConfigurations)
                            + " Initial positions: " + Arrays.toString(key.rotorPositions) + "\n");
                    writer.write("Plugboard Configuration: " + plugboardConfig + "\n");
                    writer.write("Decrypted Text:\n" + decryptedText + "\n");
                    writer.write("=====================================\n");
                }

                System.out.println("Decrypted texts saved to 'decrypted_texts.txt'.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}