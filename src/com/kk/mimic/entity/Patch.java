package com.kk.mimic.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义一个patch
 * @author Krise
 */
public class Patch {
	// 为每个种群编号
	public int id;
	// 存放个体的集合
	public List<Agent> agents;

	/**
	 * @param id
	 */
	public Patch(int id) {
		this.id = id;
		agents = new ArrayList<Agent>();
	}

	/**
	 * @return
	 */
	public double getCurrentRhoA() {
		int count = 0;
		for (Agent agent : agents) {
			if ( agent.state.equals("AS") || agent.state.equals("AI")) {
				count++;
			}
		}
		//System.out.println("countA="+count);
		if (agents.size() == 0) {
			return 0.0;
		} else {
			return count * 1.0 / agents.size();
		}

	}
	/**
	 * @return
	 */
	public double getCurrentRhoI() {
		int count = 0;
		for (Agent agent : agents) {
			if (agent.state.equals("UI") || agent.state.equals("AI")) {
				count++;
			}
		}
		if (agents.size() == 0) {
			return 0.0;
		} else {
			return count * 1.0 / agents.size();
		}
		
	}
	
	


}//class
