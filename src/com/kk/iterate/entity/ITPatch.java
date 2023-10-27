package com.kk.iterate.entity;

/**
 * 定义一个patch
 * 
 * @author Krise
 * 
 */
public class ITPatch {
	//
	public int id;// 为每个种群编号
	//
	public double numberOfUS = 0.0;// US态的个体数
	public double numberOfUI = 0.0;// UI态的个体数
	public double numberOfAS = 0.0;// AS态的个体数
	public double numberOfAI = 0.0;// AI态的个体数
	public double population = 0.0;// 总人口数

	/**
	 * @param id
	 */
	public ITPatch(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public double getCurrentRhoA() {
		if (population > 0) {
			return (numberOfAS + numberOfAI) / population;
		} else {
			return 0.0;
		}
	}

	/**
	 * 
	 * 得到当前patch I态个体的比例
	 * @return
	 */
	public double getCurrentRhoI() {
		if (population > 0) {
			return (numberOfUI + numberOfAI) / population;
		} else {
			return 0.0;
		}//
	}//

}//Class
