package com.kk.mimic.entity;

/**
 * 
 * @author Krise
 * 
 */
public class Agent {
	//
	public int id;
	public String state;
	public int moveToPatchId;

	/**
	 * @param id
	 * @param state
	 */
	public Agent(int id, String state) {
		this.id = id;
		this.state = state;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Agent-[id=" + id + ", state=" + state + ", moveToPatchId=" + moveToPatchId + "]";
	}
}
