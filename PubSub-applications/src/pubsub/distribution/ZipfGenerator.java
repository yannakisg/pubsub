package pubsub.distribution;

public class ZipfGenerator {

    private final UniformGenerator generator;
    private final int size;
    private final double skew;
    private double bottom = 0;

    public ZipfGenerator(int size, double skew, long seed) {
        this.size = size;
        this.skew = skew;
        this.generator = new UniformGenerator(seed);

        for (int i = 1; i < size; i++) {
            this.bottom += (1 / Math.pow(i, this.skew));
        }
    }

    public ZipfGenerator(int size, double skew) {
        this(size, skew, System.currentTimeMillis());
    }

    public int next() {
        int rank;
        double friquency = 0;
        double dice;

        rank = generator.uniform(size);
        friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
        dice = generator.uniform();

        while (!(dice < friquency)) {
            rank = generator.uniform(size);
            friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
            dice = generator.uniform();
        }

        return rank;
    }
}
