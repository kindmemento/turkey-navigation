import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Image mapImage = new ImageIcon("src/map/map.png").getImage();
        int width = mapImage.getWidth(null);
        int height = mapImage.getHeight(null);

        StdDraw.setCanvasSize(width / 2, height / 2);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.picture(width / 2, height / 2, "src/map/map.png", width, height);

        StdDraw.show();
    }
}