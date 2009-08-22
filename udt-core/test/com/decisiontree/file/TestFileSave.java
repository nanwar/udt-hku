package com.decisiontree.file;

import com.decisiontree.build.TreeNode;
import com.decisiontree.exceptions.DecisionTreeFileException;

public class TestFileSave {
	
	public static void main(String args []){
		
		double [] cls = {3,4,5};
		
		TreeNode treeNode = new TreeNode(cls ,100, 0.9  );
		treeNode.setNoChildren(2);
		treeNode.setMajorityCls(3);
		treeNode.setType(TreeNode.INTERAL);

		double [] cls2  = {1,3,2};
		double [] cls3  = {2,1,3};
		
		
		TreeNode childLeft = new TreeNode(treeNode, cls2, 60, 0.6, 1 );
		
		TreeNode childRight = new TreeNode(treeNode, cls3, 40, 0.5, 1 );
		
		treeNode.addChild(childLeft, 0);
		treeNode.addChild(childRight, 1);
		
		childLeft.setMajorityCls(1);
		childLeft.setType(TreeNode.INTERAL);
		childRight.setType(TreeNode.LEAF);
		childRight.setMajorityCls(2);
		

		double [] cls4  = {1,1,1};
		double [] cls5  = {0,2,1};
		
		TreeNode childLeftLeft = new TreeNode(childLeft, cls4, 30, 0.2, 2 );
		TreeNode childLeftRight = new TreeNode(childLeft, cls5, 30, 0.3, 2 );

		childLeft.setNoChildren(2);
		childLeftRight.setMajorityCls(2);
		childLeft.addChild(childLeftLeft, 0);
		childLeft.addChild(childLeftRight, 1);
		
		DecisionTreeStorage store = new DecisionTreeStorage();
			try {
				store.saveTreeToFile("test.xml", treeNode);
			} catch (DecisionTreeFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		TreeNode tree = null;
			try {
				tree = store.readTreeFromFile("test.xml");
			} catch (DecisionTreeFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		System.out.println(tree.getHeight());
		System.out.println(tree.getChild(0).getMajorityCls());
		System.out.println(tree.getChild(0).getChild(1).getMajorityCls());
		
	}
	
	
}
