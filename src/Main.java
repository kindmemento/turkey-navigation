import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Image mapImage = new ImageIcon("src/map/map.png").getImage();
        int width = mapImage.getWidth(null);
        int height = mapImage.getHeight(null);

        StdDraw.setCanvasSize(width / 2, height / 2);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.picture(width / 2, height / 2, "src/map/map.png", width, height);

        // Load city coordinates and connections from files
        String coordinatesFilePath = "src/input/city_coordinates.txt";
        ArrayList<City> cities = retrieveCities(coordinatesFilePath);
        int citiesSize = cities.size();

        double[][] graph = new double[citiesSize][citiesSize];

        String connectionsFilePath = "src/input/city_connections.txt";
        ArrayList<Road> roads = retrieveRoads(connectionsFilePath, cities, graph);


        for (City city : cities) {
            StdDraw.filledCircle(city.x, city.y, 5.0);
            StdDraw.text(city.x, city.y - 25.0, city.cityName, 10.0);
//            System.out.println(city.id + " " + city.cityName + " - X: " + city.x + " - Y: " + city.y);
        }

        for (Road road : roads) {
            City to = road.to();
            City from = road.from();
//            System.out.println("Index: " + roads.indexOf(road));
//            System.out.println("To: " + to.cityName);
//            System.out.println("From: " + from.cityName);
//            System.out.println("\n");
            StdDraw.line(from.x, from.y, to.x, to.y);
        }
        StdDraw.show();

        // Prompt for start and end cities
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start city: ");
        String startCityName = scanner.nextLine().trim();
        System.out.print("Enter end city: ");
        String endCityName = scanner.nextLine().trim();

        // Find indices of start and end cities
        int start = -1, end = -1;
        for (City city : cities) {
            if (city.cityName.equalsIgnoreCase(startCityName)) {
                start = city.id;
            }
            if (city.cityName.equalsIgnoreCase(endCityName)) {
                end = city.id;
            }
        }

        if (start != -1 && end != -1) {
            dijkstra(graph, cities, start, end);
        } else {
            System.out.println("City not found");
        }
    }

    public static ArrayList<City> retrieveCities(String path) {
        ArrayList<City> cities = new ArrayList<>();

        try {
            Scanner reader = new Scanner(new FileReader(path));
            String line;
            int counter = 0;
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                String[] lineParts = line.split(", ");
                if (lineParts.length == 3) {
                    String cityName = lineParts[0];
                    int x = Integer.parseInt(lineParts[1]);
                    int y = Integer.parseInt(lineParts[2]);
                    City city = new City(counter, cityName, x, y);
                    cities.add(city);
                    counter++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return cities;
    }

    public static ArrayList<Road> retrieveRoads(String path, ArrayList<City> cities, double[][] graph) {
        ArrayList<Road> roads = new ArrayList<>();

        try {
            Scanner reader = new Scanner(new FileReader(path));
            String line;
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                String[] pair = line.split(",");
                if (pair.length == 2) {
                    String firstCity = pair[0];
                    String secondCity = pair[1];
                    City from = null;
                    City to = null;

                    for (City city : cities) {
                        if (city.cityName.equals(firstCity)) {
                            from = city;
                        }
                        if (city.cityName.equals(secondCity)) {
                            to = city;
                        }
                    }
                    if (from != null && to != null) {
                        // Calculate the distance between from and to (Euclidean distance)
                        double distance = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
                        // Update the graph matrix for both directions (assuming undirected graph)
                        graph[from.id][to.id] = distance;
                        graph[to.id][from.id] = distance;
                        roads.add(new Road(from, to));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return roads;
    }

    public static void printIntArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    public static void dijkstra(double[][] graph, ArrayList<City> cities, int start, int end) {
        int numCities = graph.length;
        boolean[] visited = new boolean[numCities];
        double[] distances = new double[numCities];
        int[] parentIds = new int[numCities];

        // Initialize distances with a large value (MAX_VALUE) and set all cities as unvisited
        for (int i = 0; i < numCities; i++) {
            distances[i] = Double.MAX_VALUE;
            parentIds[i] = -1;
            visited[i] = false;
        }

        // Distance to the start city is 0
        distances[start] = 0;

        // Perform Dijkstra's algorithm
        for (int count = 0; count < numCities - 1; count++) {
            // Find the vertex with the minimum distance value among unvisited cities
            int u = -1;
            double minDistance = Double.MAX_VALUE;
            for (int v = 0; v < numCities; v++) {
                if (!visited[v] && distances[v] < minDistance) {
                    u = v;
                    minDistance = distances[v];
                }
            }

            if (u == -1) {
                break; // No valid vertex found, terminate early
            }

            visited[u] = true;

            // Update distances for adjacent cities of the selected city
            for (int v = 0; v < numCities; v++) {
                if (!visited[v] && graph[u][v] != 0 && distances[u] != Double.MAX_VALUE
                        && distances[u] + graph[u][v] < distances[v]) {
                    distances[v] = distances[u] + graph[u][v];
                    parentIds[v] = u; // Update parent for city v
                }
            }
        }

        // Display the shortest path from start to end city
        displayShortestPath(cities, parentIds, start, end);
    }

    private static void displayShortestPath(ArrayList<City> cities, int[] parentIds, int start, int end) {
        System.out.println("Shortest path from " + cities.get(start).cityName + " to " + cities.get(end).cityName + ":");

        ArrayList<City> path = new ArrayList<>();
        int current = end;

        while (current != -1 && current != start) {
            path.add(cities.get(current));
            current = parentIds[current];
        }

        if (current == start) {
            path.add(cities.get(start));
            Collections.reverse(path);
            for (City city : path) {
                System.out.println(city.cityName);
            }
        } else {
            System.out.println("No path found.");
        }
    }
}