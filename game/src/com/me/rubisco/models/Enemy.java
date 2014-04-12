package com.me.rubisco.models;


import java.util.LinkedList;
import java.util.Stack;

import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.me.rubisco.GameAudio;
import com.me.rubisco.datacrap.Animator;
import com.me.rubisco.pathfinding.Pathfinder;

public class Enemy{
		
	private Body body;
	private Fixture fixture;
	private Fixture sensor;
	
	private final float width, height;
	private Vector2 velocity = new Vector2();
	private float movementForce = 10;
	int HEALTH;
	boolean findPath = false;
	GameAudio audio = new GameAudio();
	float rotation;
	
	World world;
	
	Stack<Vector2> path;
	Vector2 playerPos;
	
	private LinkedList<Bullet> bullets;
	
	public Enemy(World world, float x, float y, float width, int HEALTH){
		this.width = width;
		height = width;
		this.HEALTH = HEALTH;
		
		this.world = world;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(x,y);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0;
		fixtureDef.density = 0;
		
		rotation = 0;
		
		body = world.createBody(bodyDef);
		
		
		fixture = body.createFixture(fixtureDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(10);
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;
		
		bullets = new LinkedList<Bullet>();
		
		Animator animator = new Animator();
		Animation enemy = animator.getAnimation("catrobot", 3, 3, true);
		AnimatedSprite animatedSprite = new AnimatedSprite(enemy);
		AnimatedBox2DSprite b2dSprite = new AnimatedBox2DSprite(animatedSprite);
		b2dSprite.setSize(5,5);
		b2dSprite.setOrigin(b2dSprite.getWidth()/2, b2dSprite.getHeight()/2);
		fixture.setUserData(b2dSprite);
		
		sensor = body.createFixture(fixtureDef);
		
		path = new Stack<Vector2>();
		body.setUserData(this);
		
		playerPos = new Vector2();
	}
	
	int waypoint = 1;
	boolean startFire;
	float fireTime;
	int countTime = 100;
	
	public void update(){
		if(!findPath){
			while(!path.isEmpty()){
				path.pop();
			}
		}
		
		Vector2 rotationVector = new Vector2(body.getPosition().x - playerPos.x, body.getPosition().y - playerPos.y);
		rotationVector.nor();
		rotationVector.x = -rotationVector.x;
		rotationVector.y = -rotationVector.y;
		
		rotation = rotationVector.angle()+90;

		
		
		if(findPath){
			startFire = true;
		}else{
			startFire = false;
			countTime = 100;
		}
		
		
		if(startFire){
			if(countTime % 50 == 0){
				bullets.add(new Bullet(world, body.getPosition().x+rotationVector.x, body.getPosition().y+rotationVector.y, rotationVector, rotation));
				audio.laser();
			}
			countTime = countTime+2;
		}
		
		if(path.size() > 1 && findPath){
			waypoint = path.size()-2;
			Vector2 coord = path.get(waypoint);
			int speed = 5;
			float angle = (float) Math.atan2(coord.y - body.getPosition().y, coord.x - body.getPosition().x);
			velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
			
			Vector2 blah = new Vector2(body.getPosition().x + velocity.x  * Gdx.graphics.getDeltaTime(), body.getPosition().y + velocity.y  * Gdx.graphics.getDeltaTime());
			
			body.setTransform(blah, 0);
			
			if(isWaypointReached()){
					waypoint--;
			}
		}
		
	}
	
	public void findPath(Vector2 target, int[][] map){
		Pathfinder pathfinder = new Pathfinder();
		if(findPath){
			path = pathfinder.findPath(body.getPosition(), target, map);
		}
	}
	
	public void setFindPath(boolean findPath){
		this.findPath = findPath;
	}
	
	public Stack<Vector2> getPath(){
		Stack<Vector2> derp = (Stack<Vector2>) path.clone();
		return derp;
	}
	
	public float getRotation(){
		return rotation;
	}
	
	public Body getBody(){
		return body;
	}
	
	public Fixture getFixture(){
		return fixture;
	}
	
	public Fixture getSensor(){
		return sensor;
	}
	
	public int getHealth(){
		return HEALTH;
	}
	
	public void decrementHealth(int dec){
		HEALTH = HEALTH - dec;
	}
	
	public boolean isWaypointReached(){
		return path.get(waypoint).x - body.getPosition().x  >= 0.1 && path.get(waypoint).y - body.getPosition().y >= 0.1;
	}
	
	public void setHealth(int health){
		HEALTH = health;
	}
	
	public void setPlayerPos(Vector2 playerPos){
		this.playerPos = playerPos;
	}
	
	public LinkedList<Bullet> getBullets(){
		return bullets;
	}
	
}

