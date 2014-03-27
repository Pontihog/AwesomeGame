package com.me.rubisco.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Entity {
	private Body body;
	private Fixture fixture;
	private float width, height;
	private Vector2 velocity = new Vector2();
	private float movementForce = 10;
	
	public Entity(World world, float x, float y, float width, float height){
		this.width = width;
		height = width;
		
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
		
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}
	
	public void update(){
		body.setLinearVelocity(velocity);
	}
	
	public Body getBody(){
		return body;
	}
	
	public Fixture getFixture(){
		return fixture;
	}
}
