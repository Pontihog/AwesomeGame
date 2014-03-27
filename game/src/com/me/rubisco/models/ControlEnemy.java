package com.me.rubisco.models;


import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class ControlEnemy extends InputAdapter{
		
	private Body body;
	private Fixture fixture;
	private final float width, height;
	private Vector2 velocity = new Vector2();
	private float movementForce = 10;
	
	public ControlEnemy(World world, float x, float y, float width){
		this.width = width;
		height = width;
		
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
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}
	
	public void update(){
		body.setLinearVelocity(velocity);
	}
	
	@Override
	public boolean keyDown(int keycode){
			switch(keycode){
			case Keys.UP:
				velocity.y = movementForce;
				break;
			case Keys.DOWN:
				velocity.y = -movementForce;
				break;
			case Keys.LEFT:
				velocity.x = -movementForce;
				break;
			case Keys.RIGHT:
				velocity.x = movementForce;
				break;
			default:
				return false;
			}
			return true;
	}
	
	@Override
	public boolean keyUp(int keycode){
		if(keycode == Keys.LEFT || keycode == Keys.RIGHT){
			velocity.x = 0;
		}else if(keycode == Keys.UP || keycode == Keys.DOWN){
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
	
}

