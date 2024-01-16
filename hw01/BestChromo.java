public class BestChromo extends Chromo {

    private double run, generation;

    public BestChromo(Parameters params) {
        super(params);
    }

    public void update(double r, double g) {
        this.generation = g;
        this.run = r;
    }
}
