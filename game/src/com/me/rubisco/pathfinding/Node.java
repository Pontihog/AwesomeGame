package com.me.rubisco.pathfinding;

import com.badlogic.gdx.math.Vector2;

public class Node {
	int fScore, gScore, h;
	int row;
	int col;
	
	Node parent;
	
	int cost;
	
	public Node(int row, int col){
		this.row = row;
		this.col = col;
		this.gScore = 0;
		this.fScore = 0;
		this.h = 0;
	}
	
	public int getFScore(){
		return fScore;
	}
	
	public int getGScore(){
		return gScore;
	}
	
	public Vector2 getPosition(){
		return new Vector2(row, col);
	}
	
	public void setCost(int cost){
		this.cost = cost;
	}
	
	public int getCost(){
		return cost;
	}
	
	public void setH(int h){
		this.h = h;
	}
	
	public void setParent(Node parent){
		this.parent = parent;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public int getH(){
		return h;
	}
	
	public void setGScore(int gScore){
		this.gScore = gScore;
	}
//----------------------------------------------
	
	@Override
	public boolean equals(Object object){
		boolean isEqual = false;
		
		if(object != null && object instanceof Node){
			Node compare = (Node)object;
			
			isEqual = (this.getPosition().equals(compare.getPosition()));
		}
		
		return isEqual;
	}
	
	@Override
	public int hashCode(){
		return this.getPosition().hashCode();
	}
}
