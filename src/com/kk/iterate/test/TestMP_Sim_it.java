package com.kk.iterate.test;

import org.junit.Test;

import com.kk.iterate.entity.ITMetapopulation;

/**
 * @author Krise
 * 生产资源，并分配
 */
public class TestMP_Sim_it {

	private ITMetapopulation i_mp;
	private String filePathTop = "folder/WSimplex2D_100_0.063_0.0001";
	private String filePathBot = "folder/WER_100_5.6";

	/**
	 * 普通测试
	 * 
	 */
	@Test
	public void test_with_time_it() {
		i_mp = new ITMetapopulation(filePathTop, filePathBot, /*N*/100, //
				/*λ:自我意识信息传播率*/0, /*λ_0:0-阶*/0.2, /*λ_1:1-阶 */0.1, /*λ_2:2-阶 */0.2, //
				/*----------------------- θ_0:0-阶 */0.0, /*θ_1:1-阶*/0.0, /*θ_2:2-阶*/0.0, /*δ 意识恢复率*/0.2, //
				/*β_U 疾病感染率*/0.02, /*γ*/0.2, /*μ 疾病恢复*/0.2, //
				/*p 迁移概率*/ 0.2, true);
		int timeStep = 0;
		while (true) {
			System.out.println(timeStep + "\t" + i_mp.statistics());
			i_mp.statistics();
			i_mp.moveInteractReturn();//反应过程
			if (timeStep++ >= 100)
				break;
		} //

	}

	/****************************************************************
	 * 迭代不同的 betaU 仿真 获得阈值...
	 * λ 个体意识
	 * β 个体感染率
	 * 
	 */
	@Test
	public void test_threshold_it() {
		double step = 0.0001;
		//int count = 0;
		for (double betaU = 0.0000; betaU <= 0.0200; betaU = betaU + step) {
			i_mp = new ITMetapopulation(filePathTop, filePathBot, /*N*/100, //
					/*λ:自我意识信息传播率*/0.01, /*λ_0:0-阶*/0.2, /*λ_1:1-阶 */0.1, /*λ_2:2-阶 */0.2, //
					/*-----------------------  θ_0:0-阶 */0, /*θ_1:1-阶*/1, /* θ_2:2-阶*/0.0, /*δ 意识恢复率*/0.20, //
					/*β_U 疾病感染率*/betaU, /*γ*/0.20, /*μ 疾病恢复*/0.20, //
					/*p 迁移概率*/0.2, false);
			int timeStep = 0;
			while (true) {
				i_mp.moveInteractReturn();//反应过程
				if (timeStep++ > 50)
					break;
			}
			System.out.println(betaU + "\t" + i_mp.statistics());
		} //for
	}

}// Class Test
