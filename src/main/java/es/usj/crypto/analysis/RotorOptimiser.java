package es.usj.crypto.analysis;

import es.usj.crypto.analysis.fitness.FitnessFunction;
import es.usj.crypto.enigma.Machine;
import es.usj.crypto.enigma.Plugboard;
import es.usj.crypto.enigma.Reflector;
import es.usj.crypto.enigma.Rotor;
import es.usj.crypto.enigma.constant.ReflectorConfiguration;
import es.usj.crypto.enigma.constant.RotorConfiguration;

import java.util.*;

public class RotorOptimiser {
    public enum AvailableRotors {
        FIVE
    }

    private static List<RotorConfiguration[]> getRotorConfigurations() {
        List<RotorConfiguration[]> rotorConfigurations = new ArrayList<>();

        for (RotorConfiguration firstRotorConfiguration : RotorConfiguration.values()) {
            for (RotorConfiguration secondRotorConfiguration : RotorConfiguration.values()) {
                if (firstRotorConfiguration.equals(secondRotorConfiguration)) continue;
                for (RotorConfiguration thirdRotorConfiguration : RotorConfiguration.values()) {
                    if (firstRotorConfiguration.equals(thirdRotorConfiguration) || secondRotorConfiguration.equals(thirdRotorConfiguration)) continue;
                    rotorConfigurations.add(new RotorConfiguration[] {firstRotorConfiguration, secondRotorConfiguration, thirdRotorConfiguration});
                }
            }
        }
        return rotorConfigurations;
    }

    public static ScoredEnigmaKey[] findRotorConfiguration(String ciphertext, int requiredKeys, FitnessFunction f) {
        int decimalValueOfFirstAscii = 65;

        List<RotorConfiguration[]> rotorConfigurations = getRotorConfigurations();
        final List<ScoredEnigmaKey> keySet = Collections.synchronizedList(new ArrayList<>());

        rotorConfigurations.parallelStream()
            .forEach(rotorConfiguration -> {
                System.out.println("Rotors used: " + Arrays.toString(rotorConfiguration));

                float maxFitness = -1e30f;
                EnigmaKey bestKey = null;
                for (int firstLetter = 0; firstLetter < 26; firstLetter++) {
                    for (int secondLetter = 0; secondLetter < 26; secondLetter++) {
                        for (int thirdLetter = 0; thirdLetter < 26; thirdLetter++) {

                            Reflector reflector = new Reflector(ReflectorConfiguration.REFLECTOR_DEFAULT);

                            Rotor leftRotor = new Rotor(rotorConfiguration[0], (char) (firstLetter + decimalValueOfFirstAscii));
                            Rotor middleRotor = new Rotor(rotorConfiguration[1], (char) (secondLetter + decimalValueOfFirstAscii));
                            Rotor rightRotor = new Rotor(rotorConfiguration[2], (char) (thirdLetter + decimalValueOfFirstAscii));

                            Plugboard plugboard = new Plugboard("");

                            Machine enigmaMachine = new Machine(plugboard, leftRotor, middleRotor, rightRotor, reflector);

                            String decryption = enigmaMachine.getCipheredText(ciphertext);
                            float fitness = f.score(decryption.toCharArray());
                            if (fitness > maxFitness) {
                                maxFitness = fitness;
                                bestKey = new EnigmaKey(rotorConfiguration, new int[] {firstLetter, secondLetter, thirdLetter}, "");
                            }
                        }
                    }
                }

                keySet.add(new ScoredEnigmaKey(bestKey, maxFitness));
            });

        // Sort keys by best performing (highest fitness score)
        keySet.sort(Collections.reverseOrder());
        return keySet.stream()
                .sorted(Collections.reverseOrder())
                .limit(requiredKeys)
                .toArray(ScoredEnigmaKey[]::new);
    }
}