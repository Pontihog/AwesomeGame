package com.me.rubisco.models;

import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.me.rubisco.datacrap.Animator;

public class Bullet {
	
	private Body body;
	private Fixture fixture;
	private Vector2 velocity = new Vector2();
	private Vector2 unitVector = new Vector2();
	private float scalar = 15;
	public boolean destroy;
	int damage = 1;
	World world;
	public float rotation;
	
	public Bullet(World world, float x, float y, Vector2 unitVector, float rotation){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x,y);
		this.world = world;
		this.rotation = rotation;
		
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
		
		Texture texture = new Texture("sprites/Fire.png");
		Box2DSprite b2d = new Box2DSprite(texture);
		b2d.setOrigin(texture.getWidth()/2, texture.getHeight()/2);
		b2d.setScale(.03f,.03f);
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(b2d);

		
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
	
	public float getRotation(){
		return rotation;
	}
}
