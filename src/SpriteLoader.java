import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class SpriteLoader {

    private static HashMap<String, BufferedImage> images;
    private static ArrayList<String> backgroundKeys;
    private static boolean loaded;

    public SpriteLoader ()
    {
        if(!loaded)
        {
            images = new HashMap<>();
            backgroundKeys = new ArrayList<>();
            String dir  = System.getProperty("user.dir") + "/src/Base/Resources/";
            loadDir(dir);
            loaded = true;
        }
    }


    public BufferedImage returnImageFromSet (int index) {
        if (index >= backgroundKeys.size()){
            System.out.println("Key out of bounds, modulating");
            index %= backgroundKeys.size();
        }
        return images.get(backgroundKeys.get(index));
    }

    public BufferedImage returnImageFromSet (String index) {
        return images.get(index);
    }

    public void loadDir(String directory) {
        File dir = new File(directory);
        if(dir.isDirectory())
        {
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String name = child.getName().replace(".png", "").replace(".jpg", "");

                    if(name.contains("overworld")) backgroundKeys.add(name);
                    images.put(name, loadImage("../Base/Resources/" + child.getName()));
                }
            }
        }
        else
        {
            System.out.println("Attempted to load images from a non-existent directory");
        }
    }

    public BufferedImage loadImage(String image_file) {
        BufferedImage img;
        try
        {
            img = ImageIO.read(getClass().getResource(image_file));
        }
        catch (IOException e)
        {
            System.out.println("Error loading image.");
            return null;
        }

        return img;
    }
}
