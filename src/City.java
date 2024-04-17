public class City {
    public int id;
    public String cityName;
    public int x;
    public int y;
    public boolean isVisited = false;

    public City(int id, String cityName, int x, int y) {
        this.id = id;
        this.cityName = cityName;
        this.x = x;
        this.y = y;
    }
}