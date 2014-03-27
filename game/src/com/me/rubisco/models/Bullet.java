package com.me.rubisco.models;

import net.dermetfan.utils.libgdx.graphics.Box2DSprite;

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

public class Bullet {
	
	private Body body;
	private Fixture fixture;
	private Vector2 velocity = new Vector2();
	private Vector2 unitVector = new Vector2();
	private float scalar = 15;
	public boolean destroy;
	int damage = 1;
	World world;
	
	public Bullet(World world, float x, float y, Vector2 unitVector){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x,y);
		this.world = world;
		
		float width = .2f;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, width);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0;
		fixtureDef.density = 0;
		fixtureDef.isSensor = true;
		
		body = world.createBody(bodyDef);
		
		
		
		fixture = body.createFixture(fixtureDef);
		
		
		velocity = unitVector.scl(scalar);
		body.setUserData(this);
		destroy = false;
	}
	
	
	public void update(){
		body.setLinearVelocity(velocity);
		
	}
	
	public Body getBody(){
		return body;
	}
	
	public void setDestory(boolean destroy){
		this.destroy = destroy;
	}
	
	public boolean getDestroy(){
		return destroy;
	}
	
	public int getDamage(){
		return damage;
	}
}
