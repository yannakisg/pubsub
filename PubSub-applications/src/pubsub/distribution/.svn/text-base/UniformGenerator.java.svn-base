package pubsub.distribution;

import java.util.Random;

/**
 *
 * @author John Gasparis
 */
public class UniformGenerator {
    private Random rand;
    private double a;
    private double diff;

    public UniformGenerator(double a, double b, long seed) {
        this.a = a;
        this.diff = b - a;
        this.rand = new Random(seed);
    }

    public UniformGenerator(double a, double b) {
        this(a, b, System.currentTimeMillis());
    }

    public UniformGenerator() {
        this (0, 1.0);
    }

    public UniformGenerator(long seed) {
        this (0, 1.0, seed);
    }

    public int uniform(int n) {
        return rand.nextInt(n);
    }

    public double uniform() {
        return a + rand.nextDouble() * (diff);
    }
}
