import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;

public class BasePanel extends JPanel
{

    protected WindowLoader parent;
    protected double universalScalar;

    protected double now;
    private int computerHZ;
    protected boolean is4K;
    private int lastSecondTime;
    protected double lastUpdateTime;

    public static double speedMultiplier;
    double TIME_BETWEEN_UPDATES;

    //If you're worried about visual hitches more than perfect timing, set this to 1.
    final int MAX_UPDATES_BEFORE_RENDER = 100;

    //This caps the game framerate, which means that the game doesn't use delta-T
    //For calculating movement. I think this is fine if we set the cap at something like 144hz (~7)
    //Smooth enough for most monitors even if eyes can still see past it.

    //3- Draw all, 2- No useless sprites, 1- No moving background, 0- TBD when we need more GPU capabilities.
    protected int graphicsQuality = 3;

    private int frameCatchup = 0;

    public BasePanel(double scalar, int monitorHZ, WindowLoader parent) {
        this.parent = parent;
        universalScalar = scalar;
        computerHZ = monitorHZ;
        TIME_BETWEEN_UPDATES = 1000000000 / computerHZ;

        speedMultiplier = (double) (60) / (double) computerHZ; //designed for 60, compensates for everything else.
    }

    //Starts a new thread and runs the game loop in it.
    public void runLoop() {
        Thread loop = new Thread()
        {
            public void run()
            {
                loop();
            }
        };
        loop.start();
    }

    private void render(double TARGET_TIME_BETWEEN_RENDERS) {
        now = System.nanoTime();
        int updateCount = 0;

        //Do as many game updates as we need to, potentially playing catchup.
        while(now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER)
        {
            update();
            lastUpdateTime += TIME_BETWEEN_UPDATES;
            updateCount++;
            if (updateCount > 15 && graphicsQuality > 2) //TODO: Get rid of magic numbers in this class? Replace them with static constants maybe?
            {
                graphicsQuality--;
                frameCatchup = (int) (300 / speedMultiplier);
            }
        }

        //If for some reason an update takes forever, we don't want to do an insane number of catchups.
        //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
        if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
        {
            lastUpdateTime = now - TIME_BETWEEN_UPDATES;
        }

        //Render. To do so, we need to calculate interpolation for a smooth render.
        double lastRenderTime = now;

        //Update the frames we got.
        int thisSecond = (int) (lastUpdateTime / 1000000000);
        if (thisSecond > lastSecondTime)
        {
            //TODO System.out.println("Base.Main: NEW SECOND " + thisSecond + " " + frame_count);
            lastSecondTime = thisSecond;
        }

        //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
        while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS)
        {
            if (graphicsQuality < 3){
                frameCatchup--;
            }
            if (frameCatchup < 0){
                graphicsQuality = 3; // Good job team, you did it.
            }

            Thread.yield();

            try {
                Thread.sleep(1);

            } catch(Exception e) {
                e.printStackTrace();
            }
            now = System.nanoTime();
        }

    }

    protected void loadSpriteWithGraphics2D(Graphics2D g2d, Sprite image, ImageObserver observer){
        g2d.drawImage(image.getImage(), image.getX(), image.getY(), observer);
    }


    protected void addImageWithAlphaComposite(BufferedImage buff1, BufferedImage buff2, float opaque, int x, int y) {
        Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }

    protected BufferedImage addImageUsingSetRGB(BufferedImage src, BufferedImage dst, int dx, int dy) {
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                dst.setRGB( dx + x, dy + y, src.getRGB(x,y) );
            }
        }
        return dst;
    }

    protected BufferedImage copyColoredPixelsIntoBufferedImage(BufferedImage dst, int dx, int dy, int sizex, int sizey, Color color) {
        Graphics2D g = dst.createGraphics();
        g.setColor(color);
        g.fillRect(dx, dy, sizex, sizey);
        g.dispose();
        return dst;
    }

    protected BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        //return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(), bi.getHeight());
    }


    public void loop() {
        //We will need the last update time.
        lastUpdateTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = computerHZ;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS; //TODO: Magic number removal service?

        //Simple way of finding FPS.
        lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (true) //TODO: Implement a break condition?
        {
            render(TARGET_TIME_BETWEEN_RENDERS);
        }
    }


    protected void drawGame(float interpolation) {
        repaint();
    }


    public void update() {
        if (graphicsQuality > 2){
            updateParticles();
        }

        float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
        drawGame(interpolation);
    }

    protected void updateParticles() {
        //TODO ADD ACTUAL PARTICLES
    }

}