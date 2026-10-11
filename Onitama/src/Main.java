package Onitama.src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Engine.Core.Engines.GameEngine;
import Engine.Global.Settings;
import Engine.Global.Util;
import Onitama.src.Scenes.GameScene.GameScene;
import Onitama.src.Scenes.GameScene.Scripts.AI.Testing;
import Onitama.src.Scenes.GameScene.Scripts.States.Config;
import Onitama.src.Scenes.MainMenuScene.MainMenuScene;

public class Main {
    private static boolean testing = false;

    public static GameEngine engine;

    public static Dimension initialResolution;

    // this field is set to true if
    // undo or redo was clicked
    public static boolean iaShouldWait = false;

    public static GameScene gameScene;

    public static MainMenuScene mainMenuScene;

    public static void main(String[] args) {

        // Custom Settings
        Settings.stretch = true;
        Settings.resizable = true;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double aspectRatio = (double) screenSize.width / screenSize.height;
        //Settings.resolution = screenSize;
        Settings.resolution = new Dimension((int)(aspectRatio*600),600);
        Settings.applicationName = "Onitama";

        initialResolution = Settings.resolution;

        // Settings commands
        Settings.fullscreen_key = KeyEvent.VK_F10;
        Settings.undo_key = KeyEvent.VK_W;
        Settings.redo_key = KeyEvent.VK_X;
        Settings.action1_key = KeyEvent.VK_A;
        Settings.action2_key = KeyEvent.VK_E;
        Settings.up_key = KeyEvent.VK_Z;
        Settings.left_key = KeyEvent.VK_Q;


        // create engine
        engine = new GameEngine();
        engine.setResolution(Settings.resolution);
        engine.setIcon(Util.getImage("Onitama/res/Sprites/redKing.png"));

        
        if (testing) {
            gameScene = new GameScene(new Config(null, null, 0));
            Testing.test(); 
        } else {
            // start engine
            mainMenuScene = new MainMenuScene();
            engine.setCurrentScene(mainMenuScene);
            engine.start();
        }
    }

    
public static class FontManager {
    private static Font defaultFont;
    private static Font unicodeFont;

    static {
        try {
            // Load the default font using resource loader
            InputStream defaultFontStream = new FileInputStream("Onitama/res/Fonts/monofonto.otf");
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, defaultFontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(defaultFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            defaultFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    static {
        try {
            // Load the Unicode font using resource loader
            InputStream unicodeFontStream = new FileInputStream("Onitama/res/Fonts/Symbola.ttf");
            unicodeFont = Font.createFont(Font.TRUETYPE_FONT, unicodeFontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(unicodeFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            unicodeFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    public static Font getDefaultCustomFont(int style, float size) {
        return defaultFont.deriveFont(style, size);
    }

    public static Font getUnicodeCustomFont(int style, float size) {
        return unicodeFont.deriveFont(style, size);
    }
}
    
    public static class Palette {
        public static Color background = new Color(40,42,54,255);
        public static Color selection = new Color(68,71,90,255);
        public static Color foreground = new Color(248,248,242,255);
        public static Color highlight = new Color(98,144,164,255);
        public static Color cyan = new Color(139,233,253,255);
        public static Color green = new Color(80,250,123,255);
        public static Color orange = new Color(255,184,108, 255);
        public static Color pink = new Color(255,121,198,255);
        public static Color purple = new Color(189,147,249,255);
        public static Color red = new Color(255, 85, 85,255);
        public static Color yellow = new Color(241,250,140,255); 
    }

}
