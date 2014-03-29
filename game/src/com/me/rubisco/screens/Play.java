package com.me.rubisco.screens;

import java.util.LinkedList;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.me.rubisco.datacrap.MyContactListener;
import com.me.rubisco.models.Bullet;
import com.me.rubisco.models.Enemy;
import com.me.rubisco.models.Player;
import com.me.rubisco.models.Wall;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera worldCamera, tileCamera;

	private SpriteBatch batch;
	
	private final float TIMESTEP = 1/60f;
	private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;
	
	private Player player;
	private LinkedList<Enemy> enemies;
	public LinkedList<Bullet> bullets;
	
	private ShapeRenderer sr;
	
	int[][] arrMap;
	private TiledMap map;
	private OrthogonalTiledMapRenderer tileRenderer;
	
	private Array<Body> tmpBodies = new Array<Body>();
	
	
	
	@Override //This method is called continuously
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); //Clear the screen for another round of render
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);//Step though the physics sim
		
		//-----
		player.update();//Update our entities
		
		Enemy[] tempEnemies = enemies.toArray(new Enemy[enemies.size()]);
		
		for(Enemy e: enemies){
			e.update();
			if(e.getHealth() < 1){
				enemies.remove(e);
				world.destroyBody(e.getBody());
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.NUM_1)){
			player.setFireType(1);
		}else if(Gdx.input.isKeyPressed(Keys.NUM_2)){
			player.setFireType(2);
		}else if(Gdx.input.isKeyPressed(Keys.NUM_0)){
			player.setFireType(0);
		}else if(Gdx.input.isKeyPressed(Keys.NUM_3)){
			player.setFireType(3);
		}
		
		bullets = player.getBullets();
		
		Bullet[] tempBullets = bullets.toArray(new Bullet[bullets.size()]);
		for(Bullet b: tempBullets){
			b.update();
			if(b.getDestroy() == true){
				bullets.remove(b);
				world.destroyBody(b.getBody());
			}
		}
		//-----
		
		
		worldCamera.position.set(player.getBody().getPosition().x,player.getBody().getPosition().y, 0);//Set the camera's position to the player
		tileCamera.position.set(player.getBody().getPosition().x+.5f,player.getBody().getPosition().y+.5f, 0);//We shift this over to compensate the orgin offset
		
		tileRenderer.setView(tileCamera);//Set the tiled renderer's view to the tile camera
		tileRenderer.render(); //Render our tiled map
		
		debugRenderer.render(world, worldCamera.combined);//Render our debug outline
		
		worldCamera.update();//Update our cameras 
		tileCamera.update();
		
		for(Enemy e: enemies){
			findPath2(e);
			printPath(e);
		}
		
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		world.getBodies(tmpBodies);
		for(Body body: tmpBodies){
			Array<Fixture> fixList = body.getFixtureList();
			for(Fixture fixture: fixList){
				if(fixture.getUserData() instanceof Sprite){
					Sprite sprite = (Sprite) fixture.getUserData();
					sprite.setPosition(fixture.getBody().getPosition().x-sprite.getWidth()/2, fixture.getBody().getPosition().y-sprite.getWidth()/2);
					
					if(body.getUserData() instanceof Player){
						Player p = (Player) body.getUserData();
						sprite.setRotation(p.getRotation());
					}
					
					sprite.draw(batch);
				}
			}
		}
		
		batch.end();
		
		//findPath2(enemy);//Find a path
	}
	
	public void findPath2(Enemy e){
		e.findPath(player.getBody().getPosition(), arrMap);
	}
	
	public void printPath(Enemy e){
		Stack<Vector2> path = e.getPath();
		
		if(!path.isEmpty()){
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
		
		MyContactListener list = new MyContactListener();
		world.setContactListener(list);
		
		debugRenderer = new Box2DDebugRenderer(); //Init our debug renderer
		
		batch = new SpriteBatch(); //Setup our sprite batch
		
		worldCamera = new OrthographicCamera(); //Init our cameras
		tileCamera = new OrthographicCamera();
		
		map = new TmxMapLoader().load("maps/TestMap.tmx");//Load our map
		
		tileRenderer = new OrthogonalTiledMapRenderer(map, 1/32f);//Init our tile renderer to the map, and then scale it down 1/32
		
		addObstacles();//Get collision layer from the map, and add collision to it
		
		//--------------------
		
		enemies = new LinkedList<Enemy>();
		bullets = new LinkedList<Bullet>();
		
		enemies.add(new Enemy(world, 5, 5, .5f));

		
		
		player = new Player(world, 10f, 3f, .5f); //Init objects

		
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
			public boolean scrolled(int amount){
				//worldCamera.zoom += amount/10f;
				//tileCamera.zoom += amount/10f;
				y+=amount;
				if(y > 3)
					y = 0;
				
				if(y < 0)
					y = 3;
				
				player.setFireType(y);
				//System.out.println(y);
				return true;
			}
			
		}, player));
		
		//--------------------

		
	}
	
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
					//**Could init defs outside of loop to save some mem***
					
					
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

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		sr.dispose();
	}

}
