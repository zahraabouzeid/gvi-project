package com.gvi.project;

import com.gvi.project.models.entities.Player;
import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.questions.QuestionProvider;
import com.gvi.project.models.questions.QuestionService;
import com.gvi.project.models.tiles.TileManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GamePanel {
	final int originalTileSize = 16;  // 16x16 px ein Tile ist 16x16 Pixel groß
	final int scale = 3;// 3x16 = 48x48 px pro Tile

	public final int tileSize = originalTileSize * scale; // 48x48 px
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;

	public final int screenWidth = maxScreenCol * tileSize;
	public final int screenHeight = maxScreenRow * tileSize;

	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;

	int FPS = 60;

	double drawInterval = 1000000000.0 / FPS;

	public GameState gameState = GameState.TITLE;
	public TileManager tileManager = new TileManager(this);
	public KeyHandler keyHandler = new KeyHandler();
	Sound music = new Sound();
	Sound se = new Sound();

	public final Canvas canvas = new Canvas(screenWidth, screenHeight);
	public final GraphicsContext gc = canvas.getGraphicsContext2D();
	public final GameLoop gameLoop = new GameLoop(this);
	public final AssetSetter assetSetter = new AssetSetter(this);
	public final Player player = new Player(this, keyHandler);
	public final UI ui = new UI(this);
	public final CollisionChecker cChecker = new CollisionChecker(this);
	public final SuperObject[] obj = new SuperObject[20];
	public int interactingObjectIndex = -1;
	public final QuestionService questionProvider = new QuestionProvider();

	public GamePanel(){
		keyHandler.setupKeyListeners(canvas);
		gc.setImageSmoothing(false);
		setupGame();
		startGameLoop();
	}

	public void setupGame() {
		assetSetter.setObject();
		playMusic(0);
		se.preload(1);
		se.preload(2);
		se.preload(3);
		se.preload(4);
		se.preload(5);
		se.preload(6);
	}

	public void startGameLoop() {
		gameLoop.start();
	}

	public void stopGameLoop() {
		gameLoop.stop();
	}

	public void playMusic(int i){
		music.setFile(i);
		music.play();
		music.loop();
	}

	public void stopMusic(){
		music.stop();
	}

	public void playSE(int i){
		se.setFile(i);
		se.play();
	}
}
