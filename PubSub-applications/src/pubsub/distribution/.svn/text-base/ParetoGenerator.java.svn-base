package pubsub.distribution;

/**
 *
 * @author John Gasparis
 */
public class ParetoGenerator {

    private final UniformGenerator generator;
    private final double scale;
    private final double pwr;

    public ParetoGenerator(double scale, double alpha, long seed) {
        this.scale = scale;
        this.pwr = -1.0 / alpha;
        this.generator = new UniformGenerator(seed);
    }

    public ParetoGenerator(double scale, double alpha) {
        this(scale, alpha, System.currentTimeMillis());
    }

    public double next() {
        return scale / Math.pow(generator.uniform(), pwr);
    }
}
