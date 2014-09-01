package pubsub.distribution;

public class ExponentialGenerator {

    private final double lambda;
    private final UniformGenerator generator;

    public ExponentialGenerator(double lambda, long seed) {
        this.lambda = lambda;
        this.generator = new UniformGenerator(seed);
    }

    public ExponentialGenerator(double lambda) {
        this(lambda, System.currentTimeMillis());
    }

    public double next() {
        return -Math.log(1 - generator.uniform()) / lambda;
    }
}
