package Onitama.src;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Engine.Structures.Vector2D;
import Onitama.src.Scenes.GameScene.Scripts.Card.CardInfo;

public class JsonReader {
    List<CardInfo> jsonCards;
    
    public JsonReader() {
        jsonCards = new ArrayList<>();
    }

    public List<CardInfo> readJson(String filePath) {
        JSONParser jsonParser = new JSONParser();

        // Open the InputStream from the file path
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath))) {
            // Parse Json file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            
            // Get Json Array
            JSONArray cards = (JSONArray) jsonObject.get("cards");
            
            for (Object cardObject : cards) {
                CardInfo card = new CardInfo();
                JSONObject jCard = (JSONObject) cardObject;
                String cardName = (String) jCard.get("name");
                JSONArray configArray = (JSONArray) jCard.get("config");
                
                card.setString(cardName);
                for (Object configObj : configArray) {
                    JSONObject config = (JSONObject) configObj;
                    String player = (String) config.get("player");
                    JSONArray moves = (JSONArray) config.get("moves");
                    
                    for (Object moveObj : moves) {
                        JSONObject move = (JSONObject) moveObj;
                        long rowl = (long) move.get("row");
                        long coll = (long) move.get("col");
                        int row = (int) rowl;
                        int col = (int) coll;
                        Vector2D movement = new Vector2D(row, col);
                        if (player.equals("Blue"))
                            card.addBlue(movement);
                        else
                            card.addRed(movement);
                    }
                }
                jsonCards.add(card);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return jsonCards;
    }
}
