package Onitama.src.Scenes.MainMenuScene;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import Engine.Core.Renderer.Scene;
import Engine.Entities.GameObject;
import Engine.Entities.UI.ColorArea;
import Engine.Entities.UI.FlatButton;
import Engine.Entities.UI.MenuFrame;
import Engine.Global.Util;
import Engine.Structures.Sprite;
import Engine.Structures.Vector2D;
import Onitama.src.Main;
import Onitama.src.Scenes.GameScene.GameScene;
import Onitama.src.Scenes.GameScene.Scripts.History.History;
import Onitama.src.Scenes.GameScene.Scripts.States.State;
import Onitama.src.Scenes.HowToPlayScene.HowToPlayScene;
import Onitama.src.Scenes.NewGameMenu.NewGameMenuScene;

public class MainMenuScene extends Scene {
    
    MenuFrame buttonsFrame;
    MenuFrame titleFrame;

    public MainMenuScene() {
        //createTitle();
        //createTitleShadow();
        createButtons();

        Sprite titleSprite = new Sprite(Util.getImage("Onitama/res/Sprites/Title_byMayaShieda.png"));
        GameObject title = new GameObject();
        title.setSprite(titleSprite);
        title.setScale(new Vector2D(0.4, 0.4));
        title.setPos(
            (int)(Main.engine.getResolution().width/2 - title.getSize().width/2),
            (int)(Main.engine.getResolution().height * 0.1)
        );
        addComponent(title);

        // Add foreground
        ColorArea foreground = new ColorArea(
            Main.Palette.background, 
            new Dimension(
                (int)(Main.engine.getResolution().width * 0.85), 
                (int)(Main.engine.getResolution().height * 0.85)
            ),
            new Vector2D(
                Main.engine.getResolution().width * 0.075,
                Main.engine.getResolution().height * 0.075
            )
        );
        foreground.setCurvature(120, 120);
        addComponent(foreground);

        // Add background
        ColorArea background = new ColorArea(Main.Palette.background.brighter(), new Dimension(Main.engine.getResolution().width, Main.engine.getResolution().height));
        addComponent(background);
    }

    private void createTitle() {
        titleFrame = new MenuFrame(
            new Dimension(
                (int)(Main.engine.getResolution().width * 0.8),
                (int)(Main.engine.getResolution().height * 0.4)
            ),
            new Vector2D(
                Main.engine.getResolution().width * 0.1,
                Main.engine.getResolution().height * 0.05
            )
        );

        titleFrame.setMainColor(new Color(0,0,0,0));
        titleFrame.setAccentColor(new Color(0,0,0,0));

        titleFrame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("O N I T A M A", SwingConstants.CENTER);
        titleLabel.setFont(Main.FontManager.getDefaultCustomFont(Font.ITALIC, 86));
        titleLabel.setForeground(Main.Palette.foreground);

        titleFrame.add(titleLabel, BorderLayout.CENTER);

        addComponent(titleFrame);
    }

    private void createTitleShadow() {
        MenuFrame titleFrameShadow = new MenuFrame(
            new Dimension(
                (int)(Main.engine.getResolution().width * 0.8),
                (int)(Main.engine.getResolution().height * 0.4)
            ),
            new Vector2D(
                Main.engine.getResolution().width * 0.1 + 5,
                Main.engine.getResolution().height * 0.05 + 5
            )
        );

        titleFrameShadow.setMainColor(new Color(0,0,0,0));
        titleFrameShadow.setAccentColor(new Color(0,0,0,0));

        titleFrameShadow.setLayout(new BorderLayout());


        JLabel titleShadow = new JLabel("O N I T A M A", SwingConstants.CENTER);
        titleShadow.setFont(Main.FontManager.getDefaultCustomFont(Font.ITALIC, 86));
        titleShadow.setForeground(Main.Palette.selection);

        titleFrameShadow.add(titleShadow, BorderLayout.CENTER);

        addComponent(titleFrameShadow);
    }

    private void createButtons() {
        buttonsFrame = new MenuFrame(
            new Dimension(
                (int)(Main.engine.getResolution().width/2),
                (int)(Main.engine.getResolution().height/1.5)
            ),
            new Vector2D(
                (int)((Main.engine.getResolution().width/2) - Main.engine.getResolution().width/4),
                (int)((Main.engine.getResolution().height/2.8))
            )
        );

        buttonsFrame.setMainColor(new Color(0,0,0,0));
        buttonsFrame.setAccentColor(new Color(0,0,0,0));

        buttonsFrame.setLayout(new BoxLayout(buttonsFrame, BoxLayout.Y_AXIS));


        FlatButton newGameButton = createBaseButton("New Game");

        FlatButton loadButton = createBaseButton("Load Game");
        
        FlatButton howToPlayButton = createBaseButton("How To Play");
        
        FlatButton quitButton = createBaseButton("Quit");
        quitButton.setMainColor(new Color(100,0,0,10));
        quitButton.setAccentColor(new Color(255,100,100,100));

        // Calculate the maximum width needed
        int maxWidth = howToPlayButton.getPreferredSize().width;

        // Set the maximum width for all buttons
        Dimension buttonSize = new Dimension(maxWidth, newGameButton.getPreferredSize().height);
        newGameButton.setPreferredSize(buttonSize);
        newGameButton.setMaximumSize(buttonSize);
        newGameButton.setMinimumSize(buttonSize);

        howToPlayButton.setPreferredSize(buttonSize);
        howToPlayButton.setMaximumSize(buttonSize);
        howToPlayButton.setMinimumSize(buttonSize);

        loadButton.setPreferredSize(buttonSize);
        loadButton.setMaximumSize(buttonSize);
        loadButton.setMinimumSize(buttonSize);

        quitButton.setPreferredSize(buttonSize);
        quitButton.setMaximumSize(buttonSize);
        quitButton.setMinimumSize(buttonSize);


        Box buttonBox1 = Box.createHorizontalBox();
        buttonBox1.add(Box.createHorizontalGlue());
        buttonBox1.add(newGameButton);
        buttonBox1.add(Box.createHorizontalGlue());

        Box buttonBox2 = Box.createHorizontalBox();
        buttonBox2.add(Box.createHorizontalGlue());
        buttonBox2.add(loadButton);
        buttonBox2.add(Box.createHorizontalGlue());

        Box buttonBox3 = Box.createHorizontalBox();
        buttonBox3.add(Box.createHorizontalGlue());
        buttonBox3.add(howToPlayButton);
        buttonBox3.add(Box.createHorizontalGlue());

        Box buttonBox4 = Box.createHorizontalBox();
        buttonBox4.add(Box.createHorizontalGlue());
        buttonBox4.add(quitButton);
        buttonBox4.add(Box.createHorizontalGlue());

        buttonsFrame.add(Box.createVerticalGlue());
        buttonsFrame.add(buttonBox1);
        buttonsFrame.add(Box.createVerticalStrut(6));
        buttonsFrame.add(buttonBox2);
        buttonsFrame.add(Box.createVerticalStrut(6));
        buttonsFrame.add(buttonBox3);
        buttonsFrame.add(Box.createVerticalStrut(6));
        buttonsFrame.add(buttonBox4);
        buttonsFrame.add(Box.createVerticalGlue());
        //buttonsFrame.add(Box.createVerticalStrut(6));

        buttonsFrame.add(Box.createVerticalGlue());
        
        addComponent(buttonsFrame);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.engine.setCurrentScene(new NewGameMenuScene());
            }
            
        });

        howToPlayButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Main.engine.setCurrentScene(new HowToPlayScene(Main.engine.getCurrentScene()));
            }
        });

        File file = new File("Onitama/savefiles/game.save");
        if (!file.exists()) {
            loadButton.setEnabled(false);
        }

        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("Onitama/savefiles/game.save");
                if (file.exists())
                {
                    try (FileInputStream fileIn = new FileInputStream(file);
                    GZIPInputStream gzipIn = new GZIPInputStream(new BufferedInputStream(fileIn));
                    ObjectInputStream in = new ObjectInputStream(gzipIn)) {
                    State state = (State) in.readObject();
                    History hisotry = (History) in.readObject();
                    Main.gameScene = new GameScene(state, hisotry);
                    Main.engine.setCurrentScene(Main.gameScene);
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();        
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    System.err.println("File not foud:" +file.getAbsolutePath());
                }
                
            }
            
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.engine.stop();
            }
            
        });
    }

    private FlatButton createBaseButton(String content) {
        FlatButton button = new FlatButton(content);
        button.setFont(Main.FontManager.getDefaultCustomFont(Font.BOLD, 18));
        button.setForeground(Main.Palette.foreground);
        button.setMainColor(Main.Palette.selection);
        button.setAccentColor(new Color(255,255,255,25));
        button.setCurvature(20, 20);
        button.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        button.setSize(20, 20);
        button.setFocusable(false);
        return button;
    }
}
