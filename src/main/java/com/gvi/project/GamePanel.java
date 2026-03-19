package com.gvi.project;

import com.gvi.project.helper.SaveManager;
import com.gvi.project.models.core.Entity;
import com.gvi.project.models.game_maps.GameMap;
import com.gvi.project.models.game_maps.GameMapLoader;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.game_maps.GameMaps;
import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.questions.QuestionProvider;
import com.gvi.project.models.questions.QuestionService;
import com.gvi.project.manager.SpriteManager;
import com.gvi.project.systems.AnimationSystem;
import com.gvi.project.systems.RenderSystem;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class GamePanel {

	public GeneralSettings generalSettings = new GeneralSettings();



	public GameState gameState = GameState.TITLE;
	public SpriteManager spriteManager = new SpriteManager();
	public KeyHandler keyHandler = new KeyHandler();
	public GameMap currentMap;
	Sound music = new Sound();
	Sound se = new Sound();

	public final List<Entity> entityList = new ArrayList<>();
	public final List<SuperObject> obj = new ArrayList<>();
	public final Canvas canvas = new Canvas(generalSettings.screenWidth, generalSettings.screenHeight);
	public final GraphicsContext gc = canvas.getGraphicsContext2D();
	public final GameLoop gameLoop = new GameLoop(this);
	public final Player player = new Player(this, keyHandler);
	public final UI ui = new UI(this);
	public final CollisionChecker cChecker = new CollisionChecker(this);
	public final SaveManager saveManager = new SaveManager();
	public int interactingObjectIndex = -1;
	public final QuestionService questionProvider = new QuestionProvider();
	public final RenderSystem renderSystem = new RenderSystem(this);
	public final AnimationSystem animationSystem = new AnimationSystem(this);

	public GamePanel(){
		keyHandler.setupKeyListeners(canvas);
		gc.setImageSmoothing(false);

		setupGame();
		startGameLoop();
	}

	public void setupGame() {
		loadMap(GameMaps.MAP_00);
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

	public void loadMap(GameMaps map){
		obj.clear();
		GameMapLoader mapLoader = new GameMapLoader(this);
		this.currentMap = mapLoader.loadMap(map.getConfigFileName());
//		cChecker.printCollisionMap();
	}
}
