package com.me.rubisco.models;

import java.awt.Button;
import java.util.LinkedList;

import net.dermetfan.utils.libgdx.graphics.Box2DSprite;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends InputAdapter{
		
	private Body body;
	private Fixture fixture;
	private final float width, height;
	private Vector2 velocity = new Vector2();
	private float movementForce = 10;
	private World world;
	private float rotation = 0;
	int fireType = 0;
	Sprite boxSprite;

	
	private LinkedList<Bullet> bullets;
	
	public Player(World world, float x, float y, float width){
		this.width = width;
		height = width;
		
		this.world = world;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x,y);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0;
		fixtureDef.density = 0;
		
		bullets = new LinkedList<Bullet>();
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		
		boxSprite = new Sprite(new Texture("data/luigi.png"));
		boxSprite.setSize(1, 1);
		boxSprite.setOrigin(boxSprite.getWidth()/2, boxSprite.getHeight()/2);
		
		Box2DSprite box2DSprite = new Box2DSprite(boxSprite);
		
		fixture.setUserData(box2DSprite);
		
		body.setUserData(this);
	}
	
	Vector2 unitVect = new Vector2();
	
	boolean startFire;
	float fireTime;
	int countTime = 100;
	
	public void update(){
		
		body.setLinearVelocity(velocity);
		
		Vector2 mouse = new Vector2(Gdx.input.getY()-(Gdx.graphics.getHeight()/2), Gdx.input.getX()-(Gdx.graphics.getWidth()/2));
		unitVect = mouse.nor();
		float x = -unitVect.x;
		unitVect.x = unitVect.y;
		unitVect.y = x;
		
		rotation = unitVect.angle()-90;
		
		if(startFire == true){
			switch(fireType){
			case 1:
				if(countTime % 50 == 0){
					bullets.add(new Bullet(world, body.getPosition().x+unitVect.x, body.getPosition().y+unitVect.y, unitVect));
				}
				countTime = countTime+2;
				break;
			case 2:
				if(countTime % 10 == 0){
					bullets.add(new Bullet(world, body.getPosition().x+unitVect.x, body.getPosition().y+unitVect.y, unitVect));
				}
				countTime = countTime+ 2;
				break;
			case 3:
				if(countTime % 100 == 0){
					Vector2 b1 = unitVect.cpy();
					Vector2 b2 = unitVect.cpy();
					Vector2 b3 = unitVect.cpy();
				
					b1.setAngle(b1.angle()+10);
					b3.setAngle(b3.angle()-10);
				
					Vector2 b1s = b1.cpy().scl(1f);
					Vector2 b2s = b2.cpy().scl(1f);
					Vector2 b3s = b3.cpy().scl(1f);
				
				
					bullets.add(new Bullet(world, body.getPosition().x+b1s.x, body.getPosition().y+b1s.y, b1));
					bullets.add(new Bullet(world, body.getPosition().x+b2s.x, body.getPosition().y+b2s.y, b2));
					bullets.add(new Bullet(world, body.getPosition().x+b3s.x, body.getPosition().y+b3s.y, b3));
				}
				countTime = countTime + 2;
				break;
			}
		}
	}
	
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		Vector2 posUnitVect = unitVect.scl(1);
		float angle = posUnitVect.angle();
		switch(button){
		case Buttons.LEFT:
			startFire = true;
			break;
		default:
				return false;
		}
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if(button == Buttons.LEFT){
			startFire = false;
			countTime = 100;
		}else{
			return false;
		}
		return true;
	}
	
	public LinkedList<Bullet> getBullets(){
		return bullets;
	}
	
	@Override
	public boolean keyDown(int keycode){
			switch(keycode){
			case Keys.W:
				velocity.y = movementForce;
				break;
			case Keys.S:
				velocity.y = -movementForce;
				break;
			case Keys.A:
				velocity.x = -movementForce;
				break;
			case Keys.D:
				velocity.x = movementForce;
				break;
			default:
				return false;
			}
			return true;
	}
	
	@Override
	public boolean keyUp(int keycode){
		if(keycode == Keys.A || keycode == Keys.D){
			velocity.x = 0;
		}else if(keycode == Keys.W || keycode == Keys.S){
			velocity.y = 0;
		}else{
			return false;
		}
		return true;
	}
	
	public Body getBody(){
		return body;
	}
	
	public Fixture getFixture(){
		return fixture;
	}
	
	public float getRotation(){
		return rotation;
	}
	
	public void setFireType(int fireType){
		this.fireType = fireType;
	}
	
}
