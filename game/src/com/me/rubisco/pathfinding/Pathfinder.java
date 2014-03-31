package com.me.rubisco.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import com.badlogic.gdx.math.Vector2;

public class Pathfinder {
	
	int[][] map;
	
	public Stack<Vector2> findPath(Vector2 startPos, Vector2 end, int[][] arr){
		Comparator<Node> comparator = new NodeComparator();
		PriorityQueue<Node> openList = new PriorityQueue<Node>(10 ,comparator);
		ArrayList<Node> closedList = new ArrayList<Node>();
	
		end = new Vector2((int)end.x, (int)end.y);
		Node start = new Node((int)startPos.x, (int)startPos.y);
		
		openList.offer(start);
		
		this.map = arr;
		
		while(!openList.isEmpty()){
			if(openList.peek().getPosition().equals(end)){
				break;
			}
			
			Node current = openList.remove();
			closedList.add(current);
			
			ArrayList<Node> neighborList = checkNeighbors((int)current.getPosition().x, (int)current.getPosition().y, end);
			
			for(Node neighbor: neighborList){
				neighbor.setCost(current.getGScore() + neighbor.getCost());
				
				if(openList.contains(neighbor) && neighbor.getCost() < neighbor.getGScore()){
					openList.remove(neighbor);
				}
				
				if(closedList.contains(neighbor) && neighbor.getCost() < neighbor.getGScore()){
					closedList.remove(neighbor);
				}
				
				if(!openList.contains(neighbor) && !closedList.contains(neighbor)){
					neighbor.setGScore(neighbor.getCost());
					openList.offer(neighbor);
					neighbor.setParent(current);
				}
			}
		}
				
		ArrayList<Node> pathNode = new ArrayList<Node>();
		
		Node toPath = openList.peek();
		
		
		while(!toPath.equals(start)){
			pathNode.add(toPath);
			toPath = toPath.getParent();
		}
		
		Stack<Vector2> path = new Stack<Vector2>();
		
		for(Node n: pathNode){
			path.push(new Vector2(n.getPosition().x, n.getPosition().y));
		}
		
		
		return path;
	}
	
	public ArrayList<Node> checkNeighbors(int x, int y, Vector2 end){
		ArrayList<Node> neighbors = new ArrayList<Node>();
		
	 	for(int row = -1; row < 2; row++){
	 		for(int col = -1; col < 2; col++){
	 			if(!(row == 0 && col == 0)){
	 			
	 				int rx = x + row;
	 				int ry = y + col;
	 				
	 				if(rx >= 0 && ry >= 0 && rx < map.length && ry < map[0].length && map[rx][ry] == 0){
	 					Node node = new Node(rx, ry);
	 					
	 					if(row != 0 && col != 0){
	 						node.setCost(14);
	 					}else{
	 						node.setCost(10);
	 					}
	 					
	 					node.setH(calcH(end, node.getPosition()));
	 					
	 					neighbors.add(node);
	 				}
	 			}
	 		}
	 	}
	 	
	 	return neighbors;
			  
	}
	
	public int calcH(Vector2 target, Vector2 current){
		int dx = (int) Math.abs(target.x - current.x);
		int dy = (int) Math.abs(target.y - current.y);
		return dx+dy;
	}
	
	public void printarr(){
		for(int row = 0; row < map.length; row++){
			for(int col = 0; col < map[0].length; col++){
				System.out.print(map[row][col] + " ");
			}
			System.out.println();
		}
	}
}



