package Onitama.src.Scenes.GameScene.Entities.Player;

import java.util.List;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Engine.Core.Renderer.Scene;
import Engine.Entities.GameObject;
import Engine.Global.Settings;
import Engine.Global.Util;
import Engine.Structures.Sprite;
import Engine.Structures.Texture;
import Engine.Structures.Vector2D;
import Onitama.src.Scenes.GameScene.Constants;
import Onitama.src.Scenes.GameScene.GameScene;
import Onitama.src.Scenes.GameScene.Entities.Card.Card;
import Onitama.src.Scenes.GameScene.Scripts.AI.AI;
import Onitama.src.Scenes.GameScene.Scripts.AI.RandomAI;
import Onitama.src.Scenes.GameScene.Scripts.AI.SmartAI;
import Onitama.src.Scenes.GameScene.Scripts.States.Action;
import Onitama.src.Scenes.GameScene.Scripts.States.State;
import Onitama.src.Main;
import Onitama.src.Scenes.GameScene.Entities.Board.Piece;
import Onitama.src.Scenes.GameScene.Entities.Board.Piece.PieceType;
import Onitama.src.Scenes.GameScene.Entities.Board.PieceMap;

public class Player extends GameObject {
    int playerId;

    Card card1, card2, standBy;
    ArrayList<Piece> pieces;
    PieceMap pieceMap;
    AI ai = null;

    private Action aiAction = null;
    private int counter;
    private int iaWaitCounter;

    public Sprite idleCardSprite;
    public Sprite selectedCardSprite;
    public Sprite standBySprite;
    public Sprite selectedStandBySprite;

    // Turn info
    Card selectedCard = null;

    private boolean interactable = true;

    public Player(int playerId) {
        this.playerId = playerId;
    }


    public Player(int playerId, String card1, String card2, String stb) {
        this.playerId = playerId;
        initCards(card1, card2, stb);
        initPieces();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void enableAI(int difficulty) {
        if (standBy != null) {
            standBy.setinteractable(false);
        }

        if (difficulty < 1) {
            ai = new RandomAI();
        } else {
            ai = new SmartAI(difficulty, playerId);
        }
    }

    public void disableAI() {
        ai = null;
    }

    public boolean isAiEnabled() {
        return ai != null;
    }

    @Override
    public void process(double delta) {
        if (GameScene.getCurrentPlayer() != this.playerId) {
            aiAction = null;
            return;
        }

        if (GameScene.gameOver() || ai == null) {
            if (Main.iaShouldWait) {
                GameScene.setAction(null);
                Main.iaShouldWait = false;
            }
            return;
        }
            
        if (Main.iaShouldWait) {
            Main.iaShouldWait = false;
            iaWaitCounter = 2*60;
            GameScene.setAction(null);
            aiAction = null;
            return;
        }

        if (iaWaitCounter > 0) {
            iaWaitCounter--;
            return;
        }

        if (aiAction == null) {
            aiAction = ai.play();
            GameScene.setAction(null);
            counter = 3*60; // 3 second delay
            return;
        }
        
        if (counter > 0) {
            if (counter == 120) {
                setSelectedCard(card1.getName().equals(aiAction.getCard()) ? card1 : card2);
            } else if (counter == 60) {
                Main.gameScene.setAction(aiAction);
            }
            counter--;
            return;
        }

        Main.gameScene.updateMatch();
        aiAction = null;        
    }

    @Override
    public void input(KeyEvent e) {
        if (!interactable)
            return;
        
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (e.getKeyCode() == Settings.action1_key || e.getKeyCode() == KeyEvent.VK_1) {
                setSelectedCard(card1);
            } else if (e.getKeyCode() == Settings.action2_key || e.getKeyCode() == KeyEvent.VK_2) {
                setSelectedCard(card2);
            } 
        }
    }

    public void setCardsInteractable(boolean state) {
        interactable = state;
        card1.setinteractable(state);
        card2.setinteractable(state);
        standBy.setinteractable(state);
    }

    public void setPiecesInteractable(boolean state) {
        for (Piece p : pieces) {
            p.setInteractable(state);
        }
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }
    

    public Piece checkPiecePresence(int l, int c) {
        return (Piece)(pieceMap.getPiece(l, c));
    }

    public String getFirstCard() {
        if (card1 == null) {
            return null;
        }
        return card1.getName();
    }

    public String getSecondCard() {
        if (card2 == null) {
            return null;
        }
        return card2.getName();
    }


    public boolean isCardSelected() {
        return selectedCard != null;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card c) {
        selectedCard = c;
    }

    public void setSelectedCardByName(String name) {
        if (card1.getName().equals(name)) {
            setSelectedCard(card1);
        } else if (card2.getName().equals(name)) {
            setSelectedCard(card2);
        } else {
            Util.printError("Card does not belong to player");
        }
    }

    public void movePiece(Piece p, Vector2D dest) {
        if (!pieces.contains(p)) {
            Util.printError("Piece is not owned by player");
            return;
        }
        
        p.setMapPosition(dest);

        update();
    }

    public void update() {
        pieceMap.updatePieces();
    }

    public String getStandByCard() {
        if (standBy == null)
            return null;
        
        return standBy.getName();
    }

    // animate the standyCard
    // to the selected position
    // return the selected previous position
    // the idea is to reuse the selected position
    // to start animating the other player stanby card
    // 
    // How it works ?
    // 1. we set the position of selected to stanby
    // 2. we animate the selected position from standby to normal
    public Vector2D animSelected() {
        Vector2D pos;
        if (selectedCard == card1) {
            pos = playerId == Constants.RED_PLAYER ? GameScene.placeholderPlayer1Card1.position : GameScene.placeholderPlayer2Card1.position;
        } else {
            pos = playerId == Constants.RED_PLAYER ? GameScene.placeholderPlayer1Card2.position : GameScene.placeholderPlayer2Card2.position;
        }
        selectedCard.setPos(standBy.getPos());
        selectedCard.startAnim(pos);

        return pos;
    }

    // set standby to selectedPos
    // animate standy to normal
    public void animStanby(Vector2D selectedPos) {
        Vector2D normalStandbyPosition = GameScene.placeholderStandByCard.position;
        standBy.setPos(selectedPos);
        standBy.startAnim(normalStandbyPosition);
    }

 
    public void setStandBy(String stb) {
        standBy.setName(stb);
        standBy.setVisible(true);
    }

    public void removeStandBy() {
        standBy.setName(null);
        standBy.setVisible(false);
    }

    public void loadState(State s) {
        loadPieces(s.getBoard());
        pieceMap.updatePieces();
        List<String> cards = s.getGameCards();
        card1.setName(cards.get(playerId * 2));
        card2.setName(cards.get(playerId * 2 + 1));
        if (s.getCurrentPlayer() == playerId) {
            setStandBy(cards.get(4));
        } else {
            removeStandBy();
        }
        
    }

    public void loadPieces(PieceType[][] pieces) {
        Sprite kingSprite, pawnSprite;
        if (playerId == Constants.RED_PLAYER) {
            kingSprite = new Sprite(Util.getImage("Onitama/res/Sprites/redKing.png"));   
            pawnSprite = new Sprite(Util.getImage("Onitama/res/Sprites/redPawn.png"));   
        } else {
            kingSprite = new Sprite(Util.getImage("Onitama/res/Sprites/blueKing.png"));   
            pawnSprite = new Sprite(Util.getImage("Onitama/res/Sprites/bluePawn.png"));
        }

        this.pieces = new ArrayList<>();

        for (int l = 0; l < 5; l++) {
            for (int c = 0; c < 5; c++) {
                if (ownPiece(pieces[l][c])) {
                    this.pieces.add(
                        new Piece(
                            pieceMap, 
                            pieces[l][c], 
                            new Vector2D(c,l), 
                            (
                                pieces[l][c] == PieceType.RED_KING || pieces[l][c] == PieceType.BLUE_KING ? 
                                kingSprite : pawnSprite
                            )
                        )
                    );
                }
            }
        }
    }

    private boolean ownPiece(PieceType p) {
        if (playerId == Constants.RED_PLAYER) {
            if (p == PieceType.RED_KING || p == PieceType.RED_PAWN)
                return true;
        } else {
            if (p == PieceType.BLUE_KING || p == PieceType.BLUE_PAWN)
                return true;
        }

        return false;
    }

    // i tried to call this function
    // after `initCards` but this doesn't works
    // so we duplicate the pos code
    public void resetPosCard() {

        Vector2D cardPos;

        if (playerId == Constants.RED_PLAYER) {
            cardPos = new Vector2D(
                
                (int)(GameScene.gameBoard.getPos().getIntX()/2) - (int)(selectedCardSprite.getWidth()/2),
                (int)(GameScene.gameBoard.getPos().getIntY() + (GameScene.gameBoard.getSize().height / 2) - (1.1*selectedCardSprite.getHeight()))
            );
        } else {
            cardPos = new Vector2D(
                (int)((GameScene.gameBoard.getPos().getIntX()/2) - (int)(selectedCardSprite.getWidth()/2) + GameScene.gameBoard.getPos().getIntX() + GameScene.gameBoard.getSize().height),
                (int)(GameScene.gameBoard.getPos().getIntY() + (GameScene.gameBoard.getSize().height / 2) - (1.1*idleCardSprite.getHeight()))
            );
        }

        this.card1.updatePosCard(cardPos);


        if (playerId == Constants.RED_PLAYER) {
            cardPos = new Vector2D(
                (int)(GameScene.gameBoard.getPos().getIntX()/2) - (int)(idleCardSprite.getWidth()/2),
                (int)(GameScene.gameBoard.getPos().getIntY() + (GameScene.gameBoard.getSize().height / 2) + (0.1*idleCardSprite.getHeight()))
            );
        } else {
            cardPos = new Vector2D(
                (int)((GameScene.gameBoard.getPos().getIntX()/2) - (int)(idleCardSprite.getWidth()/2) + GameScene.gameBoard.getPos().getIntX() + GameScene.gameBoard.getSize().height),
                (int)(GameScene.gameBoard.getPos().getIntY() + (GameScene.gameBoard.getSize().height / 2) + (0.1*idleCardSprite.getHeight()))
            );
        }

        this.card2.updatePosCard(cardPos);


        if (standBy != null) {
            cardPos = new Vector2D(
                (Main.engine.getResolution().width/2) - (int)(idleCardSprite.getWidth()/2),
                (int)(Main.engine.getResolution().height) -(int)(1.2*idleCardSprite.getHeight())
            );
            this.standBy.updatePosCard(cardPos);
        }
    }



    private void initCards(String card1Name, String card2Name, String standByName) {
        idleCardSprite = new Sprite(
            new Texture(
                Main.Palette.background, 
                (int)(Main.engine.getResolution().getHeight()/5),
                (int)(Main.engine.getResolution().getHeight()/5))
        );
        //idleCardSprite.setBorder(5, playerId == GameScene.PLAYER1 ? Main.Palette.red : Main.Palette.cyan, 10);
        idleCardSprite.setBorder(5, Main.Palette.selection, 10);

        selectedCardSprite = new Sprite(
            new Texture(
                Main.Palette.selection,
                (int)(Main.engine.getResolution().getHeight()/5),
                (int)(Main.engine.getResolution().getHeight()/5)) 
        );
        //selectedCardSprite.setBorder(5, playerId == GameScene.PLAYER1 ? Main.Palette.red.brighter() : Main.Palette.cyan.brighter(), 10);
        selectedCardSprite.setBorder(5, Main.Palette.selection.brighter(), 10);

        standBySprite = new Sprite(
            new Texture(
                Main.Palette.background, 
                (int)(Main.engine.getResolution().getHeight()/5),
                (int)(Main.engine.getResolution().getHeight()/5)
            )
        );
        standBySprite.setBorder(5, Main.Palette.selection, 10);
        
        selectedStandBySprite = new Sprite(
            new Texture(
                Main.Palette.selection, 
                (int)(Main.engine.getResolution().getHeight()/5),
                (int)(Main.engine.getResolution().getHeight()/5)
            )
        );
        selectedStandBySprite.setBorder(5, Main.Palette.selection.brighter(), 10);

        Vector2D cardPos;

        if (playerId == Constants.RED_PLAYER) {
            cardPos = GameScene.placeholderPlayer1Card1.getPos();
        } else {
            cardPos = GameScene.placeholderPlayer2Card1.getPos();
        }

        this.card1 = new Card(card1Name, cardPos, idleCardSprite, this);

        if (playerId == Constants.RED_PLAYER) {
            cardPos = GameScene.placeholderPlayer1Card2.getPos();
        } else {
            cardPos = GameScene.placeholderPlayer2Card2.getPos();
        }

        this.card2 = new Card(card2Name, cardPos, idleCardSprite, this);

        cardPos = GameScene.placeholderStandByCard.getPos();

        if (standByName == null) {
            standBy = new Card(null, cardPos, standBySprite, this);
            setVisible(false);
        } else {
            standBy = new Card(standByName, cardPos, standBySprite, this);
        }
        standBy.setStandBy(true);
    }

    private void initPieces() {
        Sprite kingSprite, pawnSprite;
        if (playerId == Constants.RED_PLAYER) {
            kingSprite = new Sprite(Util.getImage("Onitama/res/Sprites/redKing.png"));   
            pawnSprite = new Sprite(Util.getImage("Onitama/res/Sprites/redPawn.png"));   
        } else {
            kingSprite = new Sprite(Util.getImage("Onitama/res/Sprites/blueKing.png"));   
            pawnSprite = new Sprite(Util.getImage("Onitama/res/Sprites/bluePawn.png"));
        }
        pieceMap = new PieceMap(GameScene.gameBoard.getSize(), GameScene.gameBoard.getPos(), this);

        int line = playerId == Constants.RED_PLAYER ? 4 : 0;

        pieces = new ArrayList<>();
        pieces.add(new Piece(pieceMap, (playerId == Constants.RED_PLAYER ? PieceType.RED_PAWN : PieceType.BLUE_PAWN), new Vector2D(0, line), pawnSprite));
        pieces.add(new Piece(pieceMap, (playerId == Constants.RED_PLAYER ? PieceType.RED_PAWN : PieceType.BLUE_PAWN), new Vector2D(1, line), pawnSprite));
        pieces.add(new Piece(pieceMap, (playerId == Constants.RED_PLAYER ? PieceType.RED_KING : PieceType.BLUE_KING), new Vector2D(2, line), kingSprite));
        pieces.add(new Piece(pieceMap, (playerId == Constants.RED_PLAYER ? PieceType.RED_PAWN : PieceType.BLUE_PAWN), new Vector2D(3, line), pawnSprite));
        pieces.add(new Piece(pieceMap, (playerId == Constants.RED_PLAYER ? PieceType.RED_PAWN : PieceType.BLUE_PAWN), new Vector2D(4, line), pawnSprite));

        pieceMap.updatePieces();

    }

    public void addToScene(Scene s) {
        s.addComponent(this);
        s.addComponent(pieceMap);
        card1.addCardToScene(s);
        card2.addCardToScene(s);
        standBy.addCardToScene(s);
    }

     
}

