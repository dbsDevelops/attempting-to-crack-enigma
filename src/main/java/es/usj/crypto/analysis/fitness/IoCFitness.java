package es.usj.crypto.analysis.fitness;

public class IoCFitness extends FitnessFunction {

    public IoCFitness() {
    }

    @Override
    public float score(char[] text) {
        int[] histogram = new int[26]; // Array to count occurrences of letters
        int validCharacterCount = 0;

        for (char c : text) {
            if (Character.isAlphabetic(c)) { // Check if character is alphabetic
                char uppercase = Character.toUpperCase(c); // Convert to uppercase if needed
                int index = uppercase - 'A'; // Calculate index
                if (index >= 0 && index < 26) { // Ensure index is within bounds
                    histogram[index]++;
                    validCharacterCount++;
                }
            }
        }

        if (validCharacterCount < 2) {
            // Handle edge case where there are fewer than two valid characters
            return 0.0f;
        }
    
        float total = 0.0f;
    
        for (int v : histogram) {
            total += (v * (v - 1)); // Calculate pairwise frequency
        }
    
        return total / (validCharacterCount * (validCharacterCount - 1)); // Normalized index of coincidence
    }
}