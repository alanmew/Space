
import javafx.scene.layout.BackgroundImage;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class MainMenuPanel extends BasePanel {
    private SpriteLoader imageLoader;
    private Sprite backgroundImage = new Sprite(500, 500, 0, "elvenFae.png");

    public MainMenuPanel(double scalar, int monitorHZ, WindowLoader parent) {
        super(scalar, monitorHZ, parent);
        imageLoader =  new SpriteLoader();



        runLoop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);

        Toolkit.getDefaultToolkit().sync();
    }

    protected void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        loadSpriteWithGraphics2D(g2d, backgroundImage, this);


    }

}