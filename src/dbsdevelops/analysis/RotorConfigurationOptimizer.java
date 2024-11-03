package es.usj.crypto.enigma.analysis;

import java.util.ArrayList;
import java.util.List;

import es.usj.crypto.enigma.Machine;
import es.usj.crypto.enigma.constant.ReflectorConfiguration;
import es.usj.crypto.enigma.constant.RotorConfiguration;
import es.usj.crypto.enigma.rotor.Rotor;
import main.java.es.usj.crypto.enigma.analysis.IndexOfCoincidence;


public class RotorConfigurationOptimizer {
    private final Machine enigmaMachine;
    private final IndexOfCoincidence icCalculator;

    public RotorConfigurationOptimizer(Machine enigmaMachine) {
        this.enigmaMachine = enigmaMachine;
        this.icCalculator = new IndexOfCoincidence();
    }

    public RotorConfiguration[] findBestConfiguration(String ciphertext) {
        double bestIoC = 0.0;
        RotorConfiguration[] bestConfig = null;
        ReflectorConfiguration reflector = ReflectorConfiguration.REFLECTOR_DEFAULT;

        // Generate and test all possible rotor configurations
        for (RotorConfiguration rightRotorConfiguration : RotorConfiguration.values()) {
            for (RotorConfiguration middleRotorConfiguration : RotorConfiguration.values()) {
                for (RotorConfiguration leftRotorConfiguration : RotorConfiguration.values()) {
                    if (rightRotorConfiguration.equals(middleRotorConfiguration) || middleRotorConfiguration.equals(leftRotorConfiguration) || rightRotor.equals(leftRotorConfiguration)) {
                        continue; // Ensure unique rotors for each position
                    }
                    for (char letter: Machine.ALPHABET.toCharArray()) {
                        Rotor rightRotor = new Rotor(rightRotorConfiguration, letter);
                    }
                    Rotor middleRotor = new Rotor(middleRotorConfiguration, 'A');
                    Rotor leftRotor = new Rotor(leftRotorConfiguration, 'A');

                    // Set the rotors in the Enigma machine
                    this.enigmaMachine = new Machine(null, rightRotor, middleRotor, leftRotor, reflector);

                    // Decrypt using this configuration
                    String decryptedText = enigmaMachine.getCipheredText(ciphertext);

                    // Calculate IC for the decrypted text
                    double currentIC = icCalculator.calculate(decryptedText);

                    // Update the best configuration if the IC is higher
                    if (currentIC > bestIoC) {
                        bestIoC = currentIC;
                        bestConfig = [rightRotor, middleRotor, leftRotor];
                    }
                }
            }
        }
        
        return bestConfig;
    }
}