package com.me.rubisco.pathfinding;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node>{
	@Override
	public int compare(Node x, Node y) {
		if(x.getGScore() + x.getH() < y.getGScore() + y.getH()){
			return -1;
		}
		
		if(x.getGScore() + x.getH() > y.getGScore() + y.getH()){
			return 1;
		}
		
		return 0;
	}
}