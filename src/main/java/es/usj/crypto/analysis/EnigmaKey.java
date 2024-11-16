package es.usj.crypto.analysis;

import java.util.Arrays;

import es.usj.crypto.enigma.constant.RotorConfiguration;

public class EnigmaKey {
    public RotorConfiguration[] rotorConfigurations;
    public int[] rotorPositions;
    public String plugboard;

    public EnigmaKey(RotorConfiguration[] rotorConfigurations, int[] rotorPositions, String plugboardConnections) {
        this.rotorConfigurations = rotorConfigurations == null ? new RotorConfiguration[] {
            RotorConfiguration.ROTOR_I, RotorConfiguration.ROTOR_II, RotorConfiguration.ROTOR_III} : rotorConfigurations;
        this.rotorPositions = rotorPositions == null ? new int[] {0,0,0} : rotorPositions;
        this.plugboard = plugboardConnections == null ? "" : plugboardConnections;
    }

    public EnigmaKey(EnigmaKey key) {
        this.rotorConfigurations = key.rotorConfigurations == null ? new RotorConfiguration[] {
            RotorConfiguration.ROTOR_I, RotorConfiguration.ROTOR_II, RotorConfiguration.ROTOR_III} : Arrays.copyOf(key.rotorConfigurations, 3);
        this.rotorPositions = key.rotorPositions == null ? new int[] {0,0,0} : Arrays.copyOf(key.rotorPositions, 3);
        this.plugboard = key.plugboard == null ? "" : key.plugboard;
    }
}