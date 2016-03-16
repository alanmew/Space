import javax.swing.*;
import java.awt.*;
import java.io.*;


public class WindowLoader {

    private JFrame myBlackScreen;
    private JFrame myGameScreen;


    private static int ElvenFramerate = 0;
    private static int ElvenWindowedResolution = 0;


    private double screenWidth;
    private double screenHeight;


    private boolean isRunning = false;

    private boolean spawnBlackBKG = true;

    private boolean didInit = false;

    private int pseudoVSync;
    private double universalScaler;


    public WindowLoader() {

        initUI("main_menu");

        if (ElvenWindowedResolution == 0 && spawnBlackBKG){
            initBlackUI();
            myGameScreen.toFront();
        }
    }

    private void initBlackUI() {

        //Get computer screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();


        myBlackScreen = new JFrame();

        myBlackScreen.setUndecorated(true);
        myBlackScreen.getContentPane().setBackground(Color.BLACK);
        myBlackScreen.setLocation(-0, -0);


        myBlackScreen.setSize((int) screenWidth, (int) screenHeight);
        myBlackScreen.setResizable(false);


        myBlackScreen.setTitle("Trampolines");
        myBlackScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        myBlackScreen.setVisible(true);
    }


    public void initUI(String UIName) {
        //no don't merge thank you and good night
        isRunning = true;
        if (!didInit){

            if (ElvenFramerate == 0){
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] gs = ge.getScreenDevices();

                for (int i = 0; i < gs.length; i++) {
                    DisplayMode dm = gs[i].getDisplayMode();

                    pseudoVSync = dm.getRefreshRate();
                    ElvenFramerate = pseudoVSync;
                    if (pseudoVSync == DisplayMode.REFRESH_RATE_UNKNOWN) {
                        System.out.println("Unknown HZ, using 60 because you are probably in a VM or something"); //I love VMs, might add an override
                        //if the person runs it from the cmdline with the --hz option.
                        pseudoVSync = 60;
                        ElvenFramerate = pseudoVSync;
                    }
                }
            } else {

                pseudoVSync = ElvenFramerate;//use user set fps
            }

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();

            for (int i = 0; i < gs.length; i++) {
                DisplayMode dm = gs[i].getDisplayMode();

                pseudoVSync = dm.getRefreshRate();
                ElvenFramerate = pseudoVSync;
                if (pseudoVSync == DisplayMode.REFRESH_RATE_UNKNOWN) {
                    System.out.println("Unknown HZ, using 60 because you are probably in a VM or something"); //I love VMs, might add an override
                    //if the person runs it from the cmdline with the --hz option.
                    pseudoVSync = 60;
                    ElvenFramerate = pseudoVSync;
                }
            }



            didInit = true;

        } else {
            myGameScreen.dispose();
            myGameScreen.removeAll();
            myGameScreen.revalidate();
            myGameScreen.repaint();
        }

        if (ElvenFramerate != 0){
            pseudoVSync = ElvenFramerate;
        }


        myGameScreen = new JFrame();

        //Get computer screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int screenChangeXBy = 0;
        int screenChangeYBy = 0;
        universalScaler = 1;

        if (ElvenWindowedResolution == 0){
            screenWidth = screenSize.getWidth();
            screenHeight = screenSize.getHeight();

            myGameScreen.setUndecorated(true);
            myGameScreen.setResizable(false); //fullscreen

            if (screenWidth < screenHeight * (16.0 / 9.0)){
                screenChangeYBy = -(int) ((screenWidth * ( 9.0 / 16.0) - screenHeight) / 2);
                screenHeight = screenWidth * ( 9.0 / 16.0);
                universalScaler = screenHeight / 1080.0;
                //Super wide monitors
            } else if (screenWidth > screenHeight * ( 16.0 / 9.0)) {
                //force height to be a nice guy
                screenChangeXBy = -(int) ((screenHeight * ( 16.0 / 9.0) - screenWidth) / 2);
                screenWidth = screenHeight * ( 16.0 / 9.0);
                universalScaler = screenHeight / 1080.0;
            }
        } else {
            screenHeight = ElvenWindowedResolution;
            screenWidth = screenHeight * 16.0 / 9.0;
            universalScaler = screenHeight / 1080.0;

            myGameScreen.setUndecorated(false);
            myGameScreen.setResizable(false); //windowed
        }


        if (screenHeight == screenSize.getHeight()){
            spawnBlackBKG = false;
        }

        switch (UIName) {
            case "main_menu":
                myGameScreen.add(new MainMenuPanel(universalScaler, pseudoVSync, this));
                break;


            //todo add your own cases here for loading your own panels.


        }



        myGameScreen.setLocation(screenChangeXBy, screenChangeYBy);

        //I sure hope your screen size is an int
        myGameScreen.setVisible(true);
        myGameScreen.setSize((int) screenWidth, (int) screenHeight);


        myGameScreen.setBackground(Color.black);

        myGameScreen.setTitle("Trampoline Hero!");
    }



}