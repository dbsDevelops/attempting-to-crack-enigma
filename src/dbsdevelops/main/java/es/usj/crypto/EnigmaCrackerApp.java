public class EnigmaCrackerApp {
    /**
     * Command line runner that initializes the Enigma machine and processes input/output files.
     *
     * @param args Command line arguments
     * @throws Exception If an error occurs while processing the files
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            parseArguments(args);
            Machine machine = createMachine();
            processFile(machine);
        } catch (Exception e) {
            LOG.error("An error occurred: {}", e.getMessage(), e);
            System.exit(-1);
        }
    }
}
