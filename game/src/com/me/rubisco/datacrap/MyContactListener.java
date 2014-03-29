package com.me.rubisco.datacrap;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.me.rubisco.models.Bullet;
import com.me.rubisco.models.Enemy;
import com.me.rubisco.models.Player;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Object contactA = contact.getFixtureA().getBody().getUserData();
		Object contactB = contact.getFixtureB().getBody().getUserData();
		
		if(contactA instanceof Player && contactB instanceof Enemy){
			Enemy enemy = (Enemy) contactB;
			enemy.setFindPath(true);
		}
		
		if(contactA instanceof Bullet && !contact.getFixtureB().isSensor()){
			((Bullet) contactA).setDestory(true);
		}
		
		if(contactB instanceof Bullet){
			if(!(contactA instanceof Bullet))
				((Bullet) contactB).setDestory(true);
			
			if(contactA instanceof Enemy){
				((Enemy) contactA).decrementHealth(((Bullet) contactB).getDamage());
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Object contactA;
		Object contactB;
		
		try{
			contactA = contact.getFixtureA().getBody().getUserData();
		}catch(NullPointerException e){
			contactA = "nope";
		}
		
		try{
			contactB = contact.getFixtureB().getBody().getUserData();
		}catch(NullPointerException e){
			contactB = "nope";
		}
		
		if(contactA instanceof Player && contactB instanceof Enemy){
			Enemy enemy = (Enemy) contactB;
			enemy.setFindPath(false);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
