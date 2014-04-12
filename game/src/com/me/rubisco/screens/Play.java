package com.me.rubisco.screens;

import java.util.LinkedList;
import java.util.Stack;

import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.me.rubisco.MyGdxGame;
import com.me.rubisco.datacrap.MyContactListener;
import com.me.rubisco.models.Bullet;
import com.me.rubisco.models.Enemy;
import com.me.rubisco.models.Gun;
import com.me.rubisco.models.Player;
import com.me.rubisco.models.Wall;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera worldCamera, tileCamera;
	private MyGdxGame myGame;

	private SpriteBatch batch;
	private SpriteBatch HUDBatch = new SpriteBatch();
	
	private final float TIMESTEP = 1/60f;
	private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;
	
	private Player player;
	private LinkedList<Enemy> enemies;
	public LinkedList<Bullet> bullets;
	public LinkedList<Bullet> enemyBullets;
	public LinkedList<Gun> guns;
	
	private ShapeRenderer sr;
	
	int[][] arrMap;
	private TiledMap map;
	private OrthogonalTiledMapRenderer tileRenderer;
	
	private Array<Body> tmpBodies = new Array<Body>();
	
	private int ENEMY_HEALTH = 20;
	
	BitmapFont font = new BitmapFont(Gdx.files.internal("font/white.fnt"), Gdx.files.internal("font/white_0.png"), false);
	
	
	public Play(MyGdxGame myGame){
		this.myGame = myGame;
	}
	
	@Override //This method is called continuously
	public void render(float delta) {
		if(player.getHealth() > 0){

		Gdx.gl.glClearColor(0, 0, 0, 1); //Clear the screen for another round of render
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);//Step though the physics sim
		
		

		worldCamera.position.set(player.getBody().getPosition().x,player.getBody().getPosition().y, 0);//Set the camera's position to the player
		tileCamera.position.set(player.getBody().getPosition().x+.5f,player.getBody().getPosition().y+.5f, 0);//We shift this over to compensate the orgin offset
		
		tileRenderer.setView(tileCamera);//Set the tiled renderer's view to the tile camera
		tileRenderer.render(); //Render our tiled map
		
		debugRenderer.render(world, worldCamera.combined);//Render our debug outline
		
		worldCamera.update();//Update our cameras 
		tileCamera.update();
		
		
		//-----
		player.update();//Update our entities
		
		Enemy[] tempEnemies = enemies.toArray(new Enemy[enemies.size()]); //Working with linked lists can get a little wonky
		//So we just cast it to an array and everything goes smoothly
		
		font.setScale(.2f);
		HUDBatch.begin();
		font.draw(HUDBatch, "Health:" + (int)player.getHealth(), Gdx.graphics.getWidth()-190,Gdx.graphics.getHeight());
		font.draw(HUDBatch, "Ammo: "+player.getAmmo(), Gdx.graphics.getWidth() - 160, Gdx.graphics.getHeight()-(Gdx.graphics.getHeight())+30);
		HUDBatch.end();
		
		//For every enemy in the world
		for(Enemy e: tempEnemies){
			
			e.update();
			Bullet[] tempEBullets = e.getBullets().toArray(new Bullet[e.getBullets().size()]);
			//Get their bullets and add them to the total array
			for(Bullet b: tempEBullets){
				b.update();
				if(b.getDestroy() == true){
					e.getBullets().remove(b);
					world.destroyBody(b.getBody());
				}
			}
			
			//Remove them if they are dead
			if(e.getHealth() < 1){
				addRandomGun(e);
				addRandomEnemy();
				enemies.remove(e);
				world.destroyBody(e.getBody());
			}
		}
		//Add the players bullets to the screen
		bullets = player.getBullets();
		
		//Cast all the bullets in the world
		Bullet[] tempBullets = bullets.toArray(new Bullet[bullets.size()]);
		for(Bullet b: tempBullets){
			b.update();
			//If it is called to be destroyed, we destroy it
			if(b.getDestroy() == true){
				bullets.remove(b);
				world.destroyBody(b.getBody());
			}
		}
		
		//Cast the guns
		Gun[] tempGuns = guns.toArray(new Gun[guns.size()]);
		for(Gun g: tempGuns){
			//If we run over the guns, we destroy it
			if(g.getDestory() == true){
				guns.remove(g);
				world.destroyBody(g.getBody());
			}
		}
		//-----
		
		
		
		//Find a path to the player if we are in the radius
		for(Enemy e: enemies){
			findPath2(e);
			printPath(e);
			e.setPlayerPos(player.getBody().getPosition());
		}
		
		//Set up the projection for drawing sprites
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		world.getBodies(tmpBodies);
		//For every body in the world
		for(Body body: tmpBodies){
			//Grab it's fixtures, which hold the sprites
			Array<Fixture> fixList = body.getFixtureList();
			for(Fixture fixture: fixList){
				//If it is an animation
				if(fixture.getUserData() instanceof AnimatedBox2DSprite){
					AnimatedBox2DSprite sprite = (AnimatedBox2DSprite) fixture.getUserData();
					
					//Make it go with the rotation of the object
					if(body.getUserData() instanceof Player){
						Player p = (Player) body.getUserData();
						sprite.setRotation(p.getRotation());
					}
					
					if(body.getUserData() instanceof Enemy){
						Enemy e = (Enemy) body.getUserData();
						sprite.setRotation(e.getRotation());

					}
					
					//Draw it
					sprite.draw(batch, body.getFixtureList().first());
					
				}else if(fixture.getUserData() instanceof Sprite){
					Sprite sprite = (Sprite) fixture.getUserData();
					sprite.setPosition(fixture.getBody().getPosition().x-sprite.getWidth()/2, fixture.getBody().getPosition().y-sprite.getWidth()/2);
					
					if(body.getUserData() instanceof Player){
						Player p = (Player) body.getUserData();
						sprite.setRotation(p.getRotation());
					}
					
					if(body.getUserData() instanceof Bullet){
						Bullet b = (Bullet) body.getUserData();
						sprite.setRotation(b.getRotation());
					}
					
					sprite.draw(batch);
				}
				
				
				
			}
		}
		
		batch.end();
		
		//findPath2(enemy);//Find a path
		}else{
			
			Gdx.gl.glClearColor(0, 0, 0, 1); //Clear the screen for another round of render
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			font.setColor(Color.RED);
			font.setScale(.5f);
			
			
			
			
			HUDBatch.begin();
			
			
			font.draw(HUDBatch, "YOU DIED", Gdx.graphics.getWidth()/2-150, Gdx.graphics.getHeight()/2+50);
			font.setScale(.2f);
			font.draw(HUDBatch, "Space to Try Again", Gdx.graphics.getWidth()/2-150, Gdx.graphics.getHeight()/2-30);

			if(Gdx.input.isKeyPressed(Keys.SPACE)){
				myGame.setScreen(new Play(myGame));
			}
			HUDBatch.end();
		}
	}
	
	public void addRandomGun(Enemy e){
		int num = (int)(Math.random()*3)+1;
		String gunName = "";
				
		if(num == 1){
			gunName = "shotty";
		}else if(num == 2){
			gunName = "uzi";
		}else if(num == 3){
			gunName = "pistol";
		}
		
		guns.add(new Gun(world, (int)e.getBody().getPosition().x, (int)e.getBody().getPosition().y, gunName));
	}
	
	public void findPath2(Enemy e){
		e.findPath(player.getBody().getPosition(), arrMap);//Find the path to the player
	}
	
	//This draws the path
	public void printPath(Enemy e){
		Stack<Vector2> path = e.getPath();//Get a copy of the path
		
		if(!path.isEmpty()){//If it is not empty, we draw lines going to the various points
			Vector2 previous = path.pop();
			while(!path.isEmpty()){
				Vector2 current = path.pop();
			
				sr.setProjectionMatrix(worldCamera.combined);
				sr.setColor(Color.BLUE);
				sr.begin(ShapeType.Line);
				sr.line(previous, current);
				sr.end();
			
				previous = current;
			}
		}
	}
	

	@Override //Resize our screen so that we can see
	public void resize(int width, int height) {
		worldCamera.viewportWidth = width/64;
		worldCamera.viewportHeight = height/64;
		
		tileCamera.viewportWidth = width/64;
		tileCamera.viewportHeight = height/64;
		worldCamera.update();
		tileCamera.update();
	}
	
	

	@Override //This method is called only once, and initlizes everything in the screen
	public void show(){
		Gdx.gl.glClearColor(0, 0, 0, 1); //Refresh screen
		
		sr = new ShapeRenderer(); //Setup shape renderer
		
		world = new World(new Vector2(0, 0), true); //Create a new World
		
		MyContactListener list = new MyContactListener();//Sets up the collision detection
		world.setContactListener(list);
		
		debugRenderer = new Box2DDebugRenderer(); //Init our debug renderer
		
		batch = new SpriteBatch(); //Setup our sprite batch
		
		worldCamera = new OrthographicCamera(); //Init our cameras
		tileCamera = new OrthographicCamera();
		
		map = new TmxMapLoader().load("maps/NoSnow.tmx");//Load our map
		
		tileRenderer = new OrthogonalTiledMapRenderer(map, 1/32f);//Init our tile renderer to the map, and then scale it down 1/32
		
		addObstacles();//Get collision layer from the map, and add collision to it
		
		//--------------------
		
		//Inits the various arrays
		enemies = new LinkedList<Enemy>();
		bullets = new LinkedList<Bullet>();
		guns = new LinkedList<Gun>();
		
		//Init the things in the world
		
		addRandomEnemy();
		addRandomEnemy();


		int x = (int)(Math.random()*(arrMap.length-3));
		int y = (int)(Math.random()*(arrMap[0].length-3));
		player = new Player(world,x, y, .5f);

		
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter(){ //Combine the inputs of all our items
			
		int y = 0;
			@Override
			public boolean keyDown(int keycode){
				switch(keycode){
				case Keys.ESCAPE:
					Gdx.app.exit();
					break;
				}
				return false;
			}
			
			@Override
			public boolean scrolled(int amount){//Using the scroll wheel
				//worldCamera.zoom += amount/10f;
				//tileCamera.zoom += amount/10f;
				//Used through scrolling through various weapon types
				y+=amount;
				if(y > 3)
					y = 0;
				
				if(y < 0)
					y = 3;
				
				player.setFireType(y);//Sets the player wepon type
				return true;
			}
			
		}, player));
		
		//--------------------

		
	}
	
	//This adds all the static obstacles. 
	public void addObstacles(){
		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get(1); //Get collision layer
		
		int height = collisionLayer.getHeight();
		int width = collisionLayer.getWidth();
		
		
		arrMap = new int[height][width];

		
		//--------------------
		
		for(int x = 0; x < height; x ++){ //For each tile in the layer
			for(int y = 0; y < width; y ++){
				try{
					arrMap[x][y] = collisionLayer.getCell(x, y).getTile().getId(); //Try to get it

					//Create a new static body at the tile place
					
					//Create the wall at the selected place
					Wall wall = new Wall(world, x, y, .5f);
					
					}catch(NullPointerException e){
					arrMap[x][y] = 0;
				}
				//System.out.print("[" + arrMap[x][y] + "]");
			}
			//System.out.println();
		}
		
		//--------------------
		
		///#Create the boundry surrounding the map#
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixDef = new FixtureDef();
		
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(-.5f, -.5f);
		
		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] {new Vector2(0,0), new Vector2(0, height), new Vector2(width, height), new Vector2(width, 0), new Vector2(0,0)});
		
		fixDef.shape = groundShape;
		
		world.createBody(bodyDef).createFixture(fixDef);
		
		groundShape.dispose();
		
	}
	
	//Adds a random enemy in the world
	public void addRandomEnemy(){
		int x = (int)(Math.random()*arrMap.length);
		int y = (int)(Math.random()*arrMap[0].length);
		
		
		while(arrMap[x][y] != 0){
			x = (int)(Math.random()*arrMap.length);
			y = (int)(Math.random()*arrMap[0].length);
		}
		
		enemies.add(new Enemy(world, x, y, .5f, ENEMY_HEALTH));

	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	//Gets rid of everything we don't need
	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		sr.dispose();
	}

}
