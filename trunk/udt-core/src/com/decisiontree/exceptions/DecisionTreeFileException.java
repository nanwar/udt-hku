package com.decisiontree.exceptions;

public class DecisionTreeFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2845603787524817184L;

	public DecisionTreeFileException(){
		super();
	}
	
    public DecisionTreeFileException(String msg){
        super(msg);
      }

    public DecisionTreeFileException(String msg, Throwable t){
        super(msg,t);
    } 
    
    public DecisionTreeFileException(Throwable t){
    	super(t);
    }
}
