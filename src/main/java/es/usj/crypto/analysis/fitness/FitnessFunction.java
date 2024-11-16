package es.usj.crypto.analysis.fitness;

public abstract class FitnessFunction {
    protected final float epsilon = 3e-10f;

    public float score(char[] text) {
        return 0f;
    }
}