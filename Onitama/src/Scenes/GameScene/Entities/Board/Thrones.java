package Onitama.src.Scenes.GameScene.Entities.Board;

import java.awt.Color;
import java.awt.Dimension;

import Engine.Entities.TileMap.Tile;
import Engine.Entities.TileMap.TileMap;
import Engine.Global.Util;
import Engine.Structures.Sprite;
import Engine.Structures.Vector2D;

public class Thrones extends TileMap {

    Sprite redThroneSprite;
    Sprite blueThroneSprite;

    public Thrones(int lines, int columns, Dimension area, Vector2D offset) {
        super(5, 5, area, offset);

        redThroneSprite = new Sprite(Util.getImage("Onitama/res/Sprites/redThrone.png"));
        
        blueThroneSprite = new Sprite(Util.getImage("Onitama/res/Sprites/blueThrone.png"));
        
        addTile(0, 2, new Tile(this, 0, 2, blueThroneSprite));
        addTile(4, 2, new Tile(this, 4, 2, redThroneSprite));
    }
    
}
