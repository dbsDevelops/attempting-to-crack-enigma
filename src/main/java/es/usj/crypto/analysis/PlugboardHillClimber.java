package es.usj.crypto.analysis;

import es.usj.crypto.analysis.fitness.FitnessFunction;
import es.usj.crypto.enigma.Machine;
import es.usj.crypto.enigma.Plugboard;
import es.usj.crypto.enigma.Reflector;
import es.usj.crypto.enigma.Rotor;
import es.usj.crypto.enigma.constant.ReflectorConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class PlugboardHillClimber {

    public static Map<ScoredEnigmaKey, String> optimisePlugboard(
            String ciphertext,
            ScoredEnigmaKey[] keys,
            FitnessFunction singleCharacterFitness,
            FitnessFunction bigramFitness,
            FitnessFunction trigramFitness,
            int maxSwaps,
            String logFilePath,
            String inputFilePath,
            String outputFilePath
    ) {
        // Initialise the reflector
        Reflector reflector = new Reflector(ReflectorConfiguration.REFLECTOR_DEFAULT);

        // Map to store the best plugboard for each rotor configuration
        Map<ScoredEnigmaKey, String> results = new LinkedHashMap<>();

        // Read the initial plugboards from the file
        List<String> initialPlugboards = readInitialPlugboards(inputFilePath);

        // Validate the number of initial plugboards
        if (initialPlugboards.size() < keys.length) {
            throw new IllegalArgumentException("Insufficient initial plugboards in the input file.");
        }

        // Set up the logging mechanism
        try (FileWriter logWriter = new FileWriter(logFilePath, StandardCharsets.UTF_8)) {

            for (int i = 0; i < keys.length; i++) {
                ScoredEnigmaKey key = keys[i];
                String initialPlugboard = initialPlugboards.get(i);

                // Get rotors
                Rotor[] rotors = new Rotor[3];
                for (int j = 0; j < 3; j++) {
                    rotors[j] = new Rotor(key.rotorConfigurations[j], (char) ('A' + key.rotorPositions[j]));
                }

                String plugboard = initialPlugboard;
                float bestFitness = evaluateFitness(ciphertext, rotors, reflector, plugboard, 
                    singleCharacterFitness, bigramFitness, trigramFitness);

                // Perform hill climbing with the given number of swaps
                for (int swap = 0; swap < maxSwaps; swap++) {
                    String currentPlugboard = plugboard;
                
                    // Parallelize swap evaluation
                    List<String> candidatePlugboards = getCandidatePlugboards(currentPlugboard);
                    List<Future<String>> futures = new ArrayList<>();
                
                    ExecutorService executorService = Executors.newWorkStealingPool();
                    AtomicReference<Float> bestFitnessRef = new AtomicReference<>(bestFitness);
                
                    for (String candidate : candidatePlugboards) {
                        futures.add(executorService.submit(() -> {
                            float fitness = evaluateFitness(ciphertext, rotors, reflector, candidate, singleCharacterFitness, bigramFitness, trigramFitness);
                            synchronized (bestFitnessRef) {
                                if (fitness > bestFitnessRef.get()) {
                                    bestFitnessRef.set(fitness); // Update the best fitness
                                    return candidate; // Return the candidate as a better plugboard
                                }
                            }
                            return null; // No improvement
                        }));
                    }
                
                    executorService.shutdown();
                
                    // Collect results
                    boolean improvementMade = false;
                    for (Future<String> future : futures) {
                        try {
                            String betterPlugboard = future.get();
                            if (betterPlugboard != null) {
                                plugboard = betterPlugboard; // Update the plugboard
                                improvementMade = true;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                
                    // Update the bestFitness after evaluating all candidates
                    bestFitness = bestFitnessRef.get();
                
                    // Log the best plugboard
                    if (improvementMade) {
                        logWriter.write("Swap " + (swap + 1) + ": Improved Plugboard: " + plugboard + "\n");
                        logWriter.flush();
                    } else {
                        logWriter.write("Swap " + (swap + 1) + ": No Improvement - Current Best Plugboard: " + plugboard + "\n");
                        logWriter.flush();
                    }
                }

                // Store the best plugboard configuration for the current rotor configuration
                key.score = bestFitness;
                results.put(key, plugboard);

                // Log the updated plugboard
                logWriter.write("Rotor Configuration: " + Arrays.toString(key.rotorConfigurations) +
                                " Initial positions: " + Arrays.toString(key.rotorPositions) +
                                " Plugboard: " + plugboard + "\n");
                logWriter.flush();

                // Update the list of plugboards for the output file
                initialPlugboards.set(i, plugboard);
            }

            // Write the updated plugboards to the output file
            writePlugboardsToFile(initialPlugboards, outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private static List<String> readInitialPlugboards(String inputFilePath) {
        try {
            return Files.readAllLines(Paths.get(inputFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading initial plugboards from file: " + inputFilePath, e);
        }
    }

    private static void writePlugboardsToFile(List<String> plugboards, String outputFilePath) {
        try (FileWriter writer = new FileWriter(outputFilePath, StandardCharsets.UTF_8)) {
            for (String plugboard : plugboards) {
                writer.write(plugboard + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing plugboards to file: " + outputFilePath, e);
        }
    }

    private static List<String> getCandidatePlugboards(String plugboard) {
        // Generate all possible plugboard swaps
        List<String> candidates = new ArrayList<>();
        List<Character> unusedLetters = getUnusedLetters(plugboard);
        for (char unused : unusedLetters) {
            for (String connection : plugboard.split(":")) {
                String candidate = replaceConnection(plugboard, connection, unused);
                if (candidate != null) {
                    candidates.add(candidate);
                }
            }
        }
        return candidates;
    }

    private static String replaceConnection(String plugboard, String connection, char newLetter) {
        if (plugboard.contains(String.valueOf(newLetter))) {
            return null;
        }

        String[] connections = plugboard.split(":");
        List<String> updatedConnections = new ArrayList<>(Arrays.asList(connections));
        updatedConnections.remove(connection);
        updatedConnections.add(connection.charAt(0) + "" + newLetter);

        return String.join(":", updatedConnections);
    }

    private static List<Character> getUnusedLetters(String plugboard) {
        Set<Character> usedLetters = new HashSet<>();
        for (String connection : plugboard.split(":")) {
            for (char c : connection.toCharArray()) {
                usedLetters.add(c);
            }
        }

        List<Character> unusedLetters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!usedLetters.contains(c)) {
                unusedLetters.add(c);
            }
        }
        return unusedLetters;
    }

    private static float evaluateFitness(
            String ciphertext,
            Rotor[] rotors,
            Reflector reflector,
            String plugboardConnections,
            FitnessFunction singleCharacterFitness,
            FitnessFunction bigramFitness,
            FitnessFunction trigramFitness
    ) {
        Plugboard plugboard = new Plugboard(plugboardConnections);
        Machine machine = new Machine(plugboard, rotors[0], rotors[1], rotors[2], reflector);
        String decrypted = machine.getCipheredText(ciphertext);

        String[] words = decrypted.split("\\s+");
        float totalFitness = 0;
        for (String word : words) {
            switch (word.length()) {
                case 1:
                    totalFitness += singleCharacterFitness.score(word.toCharArray());
                    break;
                case 2:
                    totalFitness += bigramFitness.score(word.toCharArray());
                    break;
                case 3:
                    totalFitness += trigramFitness.score(word.toCharArray());
                    break;
                default:
                    totalFitness += 0; // Handle longer words if necessary
                    break;
            }
        }
        return totalFitness;
    }
}