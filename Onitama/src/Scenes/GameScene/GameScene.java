package Onitama.src.Scenes.GameScene;


import java.awt.Color;
import java.awt.Dimension; 
import java.util.*;

import Engine.Core.Renderer.Scene;
import Engine.Entities.GameObject;
import Engine.Entities.UI.ColorArea;
import Engine.Entities.UI.MenuFrame;
import Engine.Global.Util;
import Engine.Structures.Vector2D;
import Onitama.src.Scenes.GameScene.Interface.StandByArrow;
import Onitama.src.Scenes.GameScene.Interface.TopBar;
import Onitama.src.Scenes.GameScene.Interface.TurnLabel;
import Onitama.src.JsonReader;
import Onitama.src.Scenes.GameScene.Scripts.Card.CardInfo;
import Onitama.src.Scenes.GameScene.Scripts.History.History;
import Onitama.src.Scenes.GameScene.Scripts.States.Action;
import Onitama.src.Scenes.GameScene.Scripts.States.Config;
import Onitama.src.Scenes.GameScene.Scripts.States.State;
import Onitama.src.Scenes.GameScene.Interface.GameOverMenu;
import Onitama.src.Main;
import Onitama.src.Scenes.GameScene.Constants.PlayerType;
import Onitama.src.Scenes.GameScene.Entities.Board.Board;
import Onitama.src.Scenes.GameScene.Entities.Board.Piece;
import Onitama.src.Scenes.GameScene.Entities.Board.Thrones;
import Onitama.src.Scenes.GameScene.Entities.Board.Piece.PieceType;
import Onitama.src.Scenes.GameScene.Entities.Card.Card;
import Onitama.src.Scenes.GameScene.Entities.Card.CardPlaceholder;
import Onitama.src.Scenes.GameScene.Entities.Player.Player;

public class GameScene extends Scene {

    public Config gameConfig;

    public static int currentPlayer = Constants.RED_PLAYER;
    public static int winner;

    public static GameObject placeholderPlayer1Card1;
    public static GameObject placeholderPlayer1Card2;
    public static GameObject placeholderPlayer2Card1;
    public static GameObject placeholderPlayer2Card2;
    public static GameObject placeholderStandByCard;

    public static Player player1;
    public static Player player2;

    public static Board gameBoard;

    public static HashMap<String, CardInfo> gameCards;

    public static TurnLabel leftTurnLabel;
    public static TurnLabel rightTurnLabel;

    public static StandByArrow leftArrow;
    public static StandByArrow rightArrow;

    public static ColorArea leftPlayerFade;
    public static ColorArea rightPlayerFade;

    public static TopBar topBar;

    public History history;

    public GameScene(Config config) {
        // Create game
        
        // Set game config
        gameConfig = config.clone();

        // Pick game cards 
        chooseCards();

        // Set player player
        currentPlayer = gameConfig.firstPlayer;
        
        // Instantiate game entities
        createBoard();
        createCardPlaceholders();
        createPlayersFade();
        createPlayers();

        // Set player types
        if (gameConfig.redDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.RED_PLAYER, gameConfig.redDifficulty.deatph());
        }
        if (gameConfig.blueDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.BLUE_PLAYER, gameConfig.blueDifficulty.deatph());
        }

        player1.addToScene(this);
        player2.addToScene(this);
        addComponent(gameBoard);

        // Add GUI
        createGUI();
        
        // Add background
        ColorArea background = new ColorArea(Main.Palette.background, new Dimension(Main.engine.getResolution().width, Main.engine.getResolution().height));
        addComponent(background);

        history = new History();
        
        updateGUI();
    }

    public GameScene(State state) {
        gameConfig = new Config(PlayerType.HUMAN, PlayerType.HUMAN, 0);

        // Pick game cards 
        loadCards(state.getGameCards());

        // Set player player
        currentPlayer = gameConfig.firstPlayer;

        // Instantiate game entities
        createBoard();
        createCardPlaceholders();
        createPlayersFade();
        createPlayers();

        // Set player types
        if (gameConfig.redDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.RED_PLAYER, gameConfig.redDifficulty.deatph());
        }
        if (gameConfig.blueDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.BLUE_PLAYER, gameConfig.blueDifficulty.deatph());
        }

        player1.addToScene(this);
        player2.addToScene(this);
        addComponent(gameBoard);

        // Add GUI
        createGUI();

        // Add background
        ColorArea background = new ColorArea(Main.Palette.background,
                new Dimension(Main.engine.getResolution().width, Main.engine.getResolution().height));
        addComponent(background);

        history = new History();

        loadGameState(state);

        updateGUI();
    }

    public GameScene(State state, History hist) {
        gameConfig = new Config(PlayerType.HUMAN, PlayerType.HUMAN, 0);

        // Pick game cards 
        loadCards(state.getGameCards());

        // Set player player
        currentPlayer = gameConfig.firstPlayer;
        
        // Instantiate game entities
        createBoard();
        createCardPlaceholders();
        createPlayersFade();
        createPlayers();

        // Set player types
        if (gameConfig.redDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.RED_PLAYER, gameConfig.redDifficulty.deatph());
        }
        if (gameConfig.blueDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.BLUE_PLAYER, gameConfig.blueDifficulty.deatph());
        }

        player1.addToScene(this);
        player2.addToScene(this);
        addComponent(gameBoard);

        // Add GUI
        createGUI();
        
        // Add background
        ColorArea background = new ColorArea(Main.Palette.background, new Dimension(Main.engine.getResolution().width, Main.engine.getResolution().height));
        addComponent(background);

        history = hist;

        loadGameState(state);
        
        updateGUI();
    }

    public GameScene() {
        JsonReader jReader = new JsonReader();
        List<CardInfo> listOfCards = jReader.readJson("Onitama/res/Cards/cards.json");
        gameCards = new HashMap<>();
        for (CardInfo card : listOfCards)
            gameCards.put(card.getName(), card);
    }

    public State getGameState() {
        ArrayList<String> cards = new ArrayList<>();

        cards.add(player1.getFirstCard());
        cards.add(player1.getSecondCard());
        cards.add(player2.getFirstCard());
        cards.add(player2.getSecondCard());
        if (currentPlayer == Constants.RED_PLAYER) {
            cards.add(player1.getStandByCard());
        } else {
            cards.add(player2.getStandByCard());
        }

        return new State(getPlayerPieces(Constants.RED_PLAYER), getPlayerPieces(Constants.BLUE_PLAYER), cards, currentPlayer, gameConfig);
    }

    public void loadGameState(State s) {
        gameConfig = s.getGameConfig();
        
        player1.loadState(s);
        player2.loadState(s);

        // Set player types
        if (gameConfig.redDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.RED_PLAYER, gameConfig.redDifficulty.deatph());
        }
        if (gameConfig.blueDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.BLUE_PLAYER, gameConfig.blueDifficulty.deatph());
        }

        currentPlayer = s.getCurrentPlayer();

        updateGUI();
    }

    public void enablePlayerAI(int player, int difficulty) {
        switch (player) {
            case Constants.RED_PLAYER:
                player1.enableAI(difficulty);
                break;
            
            case Constants.BLUE_PLAYER:
                player2.enableAI(difficulty);
                break;

            default:
                Util.printError("Invalid player");
                break;
        }
    }

    public static int getCurrentPlayer() {
        return currentPlayer;
    }

    public PlayerType getPlayerType(int player) {
        if (player == Constants.RED_PLAYER) {
            return gameConfig.redDifficulty;
        } else {
            return gameConfig.blueDifficulty;
        }
    }

    public Config getGameConfig() {
        return gameConfig.clone();
    }

    public void setGameConfig(Config config) {
        gameConfig = config.clone();

        // Set player types
        if (gameConfig.redDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.RED_PLAYER, gameConfig.redDifficulty.deatph());
        } else {
            player1.disableAI();
        }
        if (gameConfig.blueDifficulty != PlayerType.HUMAN) {
            enablePlayerAI(Constants.BLUE_PLAYER, gameConfig.blueDifficulty.deatph());
        } else {
            player2.disableAI();
        }

        setAction(null); 
        
        updateGUI();
    }

    public int getWinner() {
        if (!gameOver()) {
            return -1;
        }

        return winner;
    }

    public static HashMap<String, CardInfo> getGameCards() {
        return gameCards;
    } 

    public static Piece getPiece(int l, int c) {
        Piece piece = player1.checkPiecePresence(l, c);
        if (piece != null) {
            return piece;
        }

        piece = player2.checkPiecePresence(l, c);
        if (piece != null) {
            return piece;
        }

        return null;
    }

    public static boolean isPieceSelected() { 
        return gameBoard.getSelectedTile() != null;
    }

    public static Piece getSelectedPiece() {
        if (gameBoard.getSelectedTile() == null)
            return null;
        
        return getPiece(gameBoard.getSelectedTile());
    }

    public static Piece getPiece(Vector2D position) {
        int l = position.getIntY();
        int c = position.getIntX();

        return getPiece(l, c);
    }

    public static boolean isCardSelected() {
        return (player1.getSelectedCard() != null) || (player2.getSelectedCard() != null);
    }

    public static Card getSelectedCard() {
        Card card = player1.getSelectedCard();

        if (card != null)
            return card;

        card = player2.getSelectedCard();
        
        return card;
    }

    public static void clearSelectedCard() {
        player1.setSelectedCard(null);
        player2.setSelectedCard(null);
    }

    public static boolean isActionSelected() {
        return gameBoard.getSelectedAction() != null;
    }

    public static Vector2D getSelectedAction() {
        return gameBoard.getSelectedAction();
    }
    

    public static ArrayList<Piece> getPlayerPieces(int player) {
        ArrayList<Piece> pieces = null;

        if (player == Constants.RED_PLAYER) {
            pieces = player1.getPieces();
        } else if (player == Constants.BLUE_PLAYER) {
            pieces = player2.getPieces();
        } else {
            Util.printError("Invalid player");
        }

        return pieces;
    }

    public static int getNextPlayer() {
        return (currentPlayer + 1) % 2;
    }

    public static void changePlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
    }

    public static boolean gameOver() {
        return conqueredKing() || capturedKing();
    }

    private static boolean conqueredKing() {
        for (Piece p : player1.getPieces()) {
            if (p.getType() == PieceType.RED_KING && p.getPosition().equals(Constants.BLUE_THRONE)) {
                return true;
            }
        }

        for (Piece p : player2.getPieces()) {
            if (p.getType() == PieceType.BLUE_KING && p.getPosition().equals(Constants.RED_THRONE)) {
                return true;
            }
        }

        return false;
    }

    private static boolean capturedKing() {

        return ((!checkPresence(PieceType.BLUE_KING) && checkPresence(PieceType.RED_KING))
                || (checkPresence(PieceType.BLUE_KING) && !checkPresence(PieceType.RED_KING)));
    }

    public static boolean checkPresence(PieceType type) {
        for (Piece p : player1.getPieces()) {
            if (p.getType() == type) {
                 return true;
            }
        }

        for (Piece p : player2.getPieces()) {
            if (p.getType() == type) {
                 return true;
            }
        }

        return false;
    }

    public static void exchangeCards() {
        if (currentPlayer == Constants.RED_PLAYER) {
            Vector2D selectedPos = player1.animSelected();
            player2.setStandBy(getSelectedCard().getName());
            getSelectedCard().setName(player1.getStandByCard());
            player1.removeStandBy();
            player2.animStanby(selectedPos);
        } else {
            Vector2D selectedPos = player2.animSelected();
            player1.setStandBy(getSelectedCard().getName());
            getSelectedCard().setName(player2.getStandByCard());
            player2.removeStandBy();
            player1.animStanby(selectedPos);
        }
    }
    
    public void updateMatch() {
        if (!isPieceSelected() || !isCardSelected() || !isActionSelected()) {
            Util.printWarning("Update match called without update conditon met");
            if (!isPieceSelected())
                Util.printWarning("Piece not selected");
            if (!isCardSelected())
                Util.printWarning("Card not selected");
            if (!isActionSelected())
                Util.printWarning("Action not selected");
            return;
        }

        history.addState(this.getGameState());

        if (getPiece(getSelectedAction()) != null) {
            for (Piece p : getPlayerPieces(getNextPlayer())) {
                if (p.getPosition().equals(getSelectedAction())) {
                    getPlayerPieces(getNextPlayer()).remove(p);
                    break;
                }
            }
        }

        if (currentPlayer == Constants.RED_PLAYER) {
            player1.movePiece(getSelectedPiece(), getSelectedAction());
            
        } else {
            player2.movePiece(getSelectedPiece(), getSelectedAction());
        }

        player1.update(); player2.update();
        
        gameBoard.setSelectedTile(null); gameBoard.setSelectedAction(null);

        exchangeCards();

        player1.setSelectedCard(null); player2.setSelectedCard(null);

        if (gameOver()) {
            //System.out.println("Player " + (currentPlayer == Constants.RED_PLAYER ? "RED" : "BLUE") + " won");
            winner = currentPlayer;
            createGameOverMenu();
             
        }

        changePlayer(); 

        updateGUI();

    }

    public static void setAction(Action act) {
        if (act == null) {
            player1.setSelectedCard(null);
            player2.setSelectedCard(null);
            gameBoard.setSelectedTile(null);
            gameBoard.setSelectedAction(null);
            return;
        }

        if (currentPlayer == Constants.RED_PLAYER) {
            player1.setSelectedCardByName(act.getCard());
        } else {
            player2.setSelectedCardByName(act.getCard());
        }

        gameBoard.setSelectedTile(act.getPiece());
        gameBoard.setSelectedAction(act.getMove());
    }

    public void updateGUI() {
        updatePlayerFade();
        updateTurnLabels();
        updateStandByCardArrows();
        updateIteractableEntities();
    }

    public static void updateTurnLabels() {
        if (currentPlayer == Constants.RED_PLAYER) {
            leftTurnLabel.setRedTurn();
            rightTurnLabel.clearTurn();
        } else {
            leftTurnLabel.clearTurn();
            rightTurnLabel.setBlueTurn();
        }
    }

    public static void updatePlayerFade() {
        if (currentPlayer == Constants.RED_PLAYER) {
            leftPlayerFade.setVisible(false);
            rightPlayerFade.setVisible(true);
        } else {
            leftPlayerFade.setVisible(true);
            rightPlayerFade.setVisible(false);
        }
    }

    public static void updateStandByCardArrows() {
        if (currentPlayer == Constants.RED_PLAYER) {
            leftArrow.toggleLeftArrow();
            rightArrow.clearArrow();
        } else {
            leftArrow.clearArrow();
            rightArrow.toggleRightArrow();
        }
    }

    public void updateIteractableEntities() {
        if (currentPlayer == Constants.RED_PLAYER) {
            if (player1.isAiEnabled()) {
                player1.setCardsInteractable(false);
                player1.setPiecesInteractable(false);
                player2.setCardsInteractable(false);
                player2.setPiecesInteractable(false);
                gameBoard.setIteractable(false);
                updateInterfaceButtons(false);
            } else {
                player1.setCardsInteractable(true);
                player1.setPiecesInteractable(false);
                player2.setCardsInteractable(false);
                player2.setPiecesInteractable(true);
                gameBoard.setIteractable(true);
                updateInterfaceButtons(true);
            }
        } else {
            if (player2.isAiEnabled()) {
                player1.setCardsInteractable(false);
                player1.setPiecesInteractable(false);
                player2.setCardsInteractable(false);
                player2.setPiecesInteractable(false);
                gameBoard.setIteractable(false);
                updateInterfaceButtons(false);
            } else {
                player1.setCardsInteractable(false);
                player1.setPiecesInteractable(true);
                player2.setCardsInteractable(true);
                player2.setPiecesInteractable(false);
                gameBoard.setIteractable(true);
                updateInterfaceButtons(true);
            }
        }
    }

    public void updateInterfaceButtons(boolean state) {
        topBar.setEnabledHint(state);
        
        if (history.canUndo()) {
            topBar.setEnabledUndo(true);
        } else {
            topBar.setEnabledUndo(false);
        }

        if (history.canRedo()) {
            topBar.setEnabledRedo(true);
        } else {
            topBar.setEnabledRedo(false);
        }
    }

    public void setEnabledGUI(boolean state) {
        player1.setCardsInteractable(state);
        player1.setPiecesInteractable(state);
        player2.setCardsInteractable(state);
        player2.setPiecesInteractable(state);
        gameBoard.setIteractable(state);
        topBar.setEnabledButtons(state);
    }

    private void createBoard() {
        // Compute board position and area
        Dimension boardArea = new Dimension(
            (int)(2.3 * Main.engine.getResolution().height / 4),
            (int)(2.3 * Main.engine.getResolution().height / 4)

        );
        Vector2D boardPos = new Vector2D(
             (Main.engine.getResolution().width/2) - (boardArea.width/2),
            (Main.engine.getResolution().height / 2.5) - (boardArea.height/2.3)
        );

        // Create game board
        addComponent(new Thrones(5, 5, boardArea, boardPos));
        gameBoard = new Board(boardArea, boardPos);
    }

    private void chooseCards() {
        JsonReader jReader = new JsonReader();
        List<CardInfo> listOfCards = jReader.readJson("Onitama/res/Cards/cards.json");

        gameCards = new HashMap<>();
        Set<Integer> set = new HashSet<>();

        Random random = new Random();

        int i = 0;  
        while (i<5)
        {
            int cardIndex = random.nextInt(16);
            if (!set.contains(cardIndex)) {
                CardInfo card = listOfCards.get(cardIndex);
                gameCards.put(card.getName(), card);
                set.add(cardIndex);
                ++i;
            }
        }
        return;
    }

    private void loadCards(List<String> cards) {
        JsonReader jReader = new JsonReader();
        List<CardInfo> listOfCards = jReader.readJson("Onitama/res/Cards/cards.json");

        gameCards = new HashMap<>();
        Set<Integer> set = new HashSet<>();

        gameCards.put(cards.get(0), listOfCards.get(findCard(listOfCards, cards.get(0))));
        gameCards.put(cards.get(1), listOfCards.get(findCard(listOfCards, cards.get(1))));
        gameCards.put(cards.get(2), listOfCards.get(findCard(listOfCards, cards.get(2))));
        gameCards.put(cards.get(3), listOfCards.get(findCard(listOfCards, cards.get(3))));
        gameCards.put(cards.get(4), listOfCards.get(findCard(listOfCards, cards.get(4))));
    }

    private int findCard(List<CardInfo> listOfCards, String card) {
        for (CardInfo c : listOfCards) {
            if (c.getName().equals(card)) {
                return listOfCards.indexOf(c);
            }
        }
        
        return -1;
    }

    private void createCardPlaceholders() {
        double cardSize = Main.engine.getResolution().getHeight() / 5;

        placeholderPlayer1Card1 = new CardPlaceholder(new Vector2D(
            (int)(gameBoard.getPos().getIntX()/2) - (int)(cardSize/2),
            (int)(gameBoard.getPos().getIntY() + (gameBoard.getSize().height / 2) - (1.1*cardSize))
        ));

        placeholderPlayer2Card1 = new CardPlaceholder(new Vector2D(
            (int)((gameBoard.getPos().getIntX()/2) - (int)(cardSize/2) + gameBoard.getPos().getIntX() + gameBoard.getSize().height),
            (int)(gameBoard.getPos().getIntY() + (gameBoard.getSize().height / 2) - (1.1*cardSize))
        ));

        placeholderPlayer1Card2 = new CardPlaceholder(new Vector2D(
            (int)(gameBoard.getPos().getIntX()/2) - (int)(cardSize/2),
            (int)(gameBoard.getPos().getIntY() + (gameBoard.getSize().height / 2) + (0.1*cardSize))
        ));

        placeholderPlayer2Card2 = new CardPlaceholder(new Vector2D(
            (int)((gameBoard.getPos().getIntX()/2) - (int)(cardSize/2) + gameBoard.getPos().getIntX() + gameBoard.getSize().height),
            (int)(gameBoard.getPos().getIntY() + (gameBoard.getSize().height / 2) + (0.1*cardSize))
                
        ));

        placeholderStandByCard = new CardPlaceholder(new Vector2D(
            (Main.engine.getResolution().width/2) - (int)(cardSize/2),
            (int)(Main.engine.getResolution().height) -(int)(1.2*cardSize)
        ));

        addComponent(placeholderPlayer1Card1);
        addComponent(placeholderPlayer1Card2);
        addComponent(placeholderPlayer2Card1);
        addComponent(placeholderPlayer2Card2);
        addComponent(placeholderStandByCard);
    }

    private void createPlayers() {
        Iterator<String> cardIter = gameCards.keySet().iterator();
        
        player1 = new Player(Constants.RED_PLAYER, cardIter.next(), cardIter.next(), null);

        player2 = new Player(Constants.BLUE_PLAYER, cardIter.next(), cardIter.next(), null);

        if (currentPlayer == Constants.RED_PLAYER) {
            player1.setStandBy(cardIter.next());
            player2.removeStandBy();
        } else {
            player2.setStandBy(cardIter.next());
            player1.removeStandBy();
        }
    } 

    private void createGUI() {

        // PLAYER SIDES
        
        Dimension sideDimension = new Dimension(
            (int)(Main.engine.getResolution().width /4) + 32,
            (int)(5 * Main.engine.getResolution().height / 8)
        );
        Vector2D sideOffset = new Vector2D(
            (int)(Main.engine.getResolution().width - sideDimension.width + 32),
            (int)(Main.engine.getResolution().height / 8)
        );
        MenuFrame blueSide = new MenuFrame(sideDimension, sideOffset);
        blueSide.setMainColor(new Color(139,233,253,5));
        blueSide.setAccentColor(new Color(139,233,253,100));
        blueSide.setCurvature(30, 30);
        blueSide.setBorderWidth(8);
    
        addComponent(blueSide);

        sideOffset = new Vector2D(
            (int)(-32),
            (int)(Main.engine.getResolution().height / 8)
        );
        MenuFrame redSide = new MenuFrame(sideDimension, sideOffset);
        redSide.setMainColor(new Color(255, 85, 85,5));
        redSide.setAccentColor(new Color(255, 85, 85,200));
        redSide.setCurvature(30, 30);
        redSide.setBorderWidth(8);

        addComponent(redSide);
        

        // TOP BAR

        // Dimension for top bar
        Dimension topBarArea = new Dimension(
            Main.engine.getResolution().width/3,
            (int) (Main.engine.getResolution().height /10)
        );
        Vector2D topBarPos = new Vector2D(
            (int)(Main.engine.getResolution().width/2 - (topBarArea.width/2)),
            0
        );
        topBar = new TopBar(topBarArea, topBarPos);
        addComponent(topBar); 


       // TURN LABELS

        Dimension turnLabelDimension = new Dimension(
            (int)(Main.engine.getResolution().width /3),
            (int)(Main.engine.getResolution().height / 10)
        );

        Vector2D turnLabelOffset = new Vector2D(
            15,
            0
        );

        leftTurnLabel = new TurnLabel(turnLabelDimension, turnLabelOffset);

        addComponent(leftTurnLabel);

        turnLabelOffset = new Vector2D(
            (int)(Main.engine.getResolution().width - turnLabelDimension.width)-15,
            0
        );

        rightTurnLabel = new TurnLabel(turnLabelDimension, turnLabelOffset);

        addComponent(rightTurnLabel);


        // STAND BY CARD ARROW INDICATOR

        Dimension arrowDimension = new Dimension(
            (int)(Main.engine.getResolution().height / 10),
            (int)(Main.engine.getResolution().height / 8)
        );

        Vector2D arrowOffset = new Vector2D(
            (Main.engine.getResolution().width/2) - (int)(1.6*arrowDimension.getWidth()),
            (int)(Main.engine.getResolution().height) - (int)(1.6*arrowDimension.getHeight())
        );

        leftArrow = new StandByArrow(arrowDimension, arrowOffset);
        leftArrow.toggleLeftArrow();
        addComponent(leftArrow);

        arrowOffset = new Vector2D(
            (Main.engine.getResolution().width/2) + (int)(arrowDimension.getWidth()/ 1.6),
            (int)(Main.engine.getResolution().height) - (int)(1.6*arrowDimension.getHeight())
        );

        rightArrow = new StandByArrow(arrowDimension, arrowOffset);
        rightArrow.toggleRightArrow();
        addComponent(rightArrow);
    }

    private void createPlayersFade() {
        Dimension sideDimension = new Dimension(
            (int)(Main.engine.getResolution().width /4) + 32,
            (int)(5 * Main.engine.getResolution().height / 8)
        );
        Vector2D sideOffset = new Vector2D(
            (int)(Main.engine.getResolution().width - sideDimension.width +32),
            (int)(Main.engine.getResolution().height / 8)
        );
        rightPlayerFade = new ColorArea(new Color(0,0,0,100), sideDimension, sideOffset);
        rightPlayerFade.setCurvature(36, 36);

        addComponent(rightPlayerFade);

        sideOffset = new Vector2D(
            (int)(-32),
            (int)(Main.engine.getResolution().height / 8)
        );
        leftPlayerFade = new ColorArea(new Color(0,0,0,100), sideDimension, sideOffset);
        leftPlayerFade.setCurvature(36, 36);

        addComponent(leftPlayerFade);
    }

    private void createGameOverMenu() {
        Dimension menuArea = new Dimension(
            (int)(Main.engine.getResolution().width / 3),
            (int)(Main.engine.getResolution().height / 2)
        );

        Vector2D menuOffset = new Vector2D(
            (Main.engine.getResolution().width/2) - (menuArea.width/2),
            (Main.engine.getResolution().height/2) - (menuArea.height/2)
        ); 

        GameOverMenu menu = new GameOverMenu(menuArea, menuOffset);

        addComponent(menu);

        setEnabledGUI(false);
    }
}
