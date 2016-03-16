import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;


public class Main
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(
                () -> {

                    //This windowloader can be called wherever, I just made a main with it so you can see it in action
                    WindowLoader ElvenMasterRulesOverPunyHumans = new WindowLoader();

                }
        );
    }
}