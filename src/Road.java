public class Road {
    private City from;
    private City to;

    public Road(City from, City to) {
        this.from = from;
        this.to = to;
    }

    public City from() { return this.from; }
    public City to() { return this.to; }
}