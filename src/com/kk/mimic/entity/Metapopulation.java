package com.kk.mimic.entity;

import java.util.*;

import com.kk.utils.AdjMatrixUtils;

/**
 * 定义一个多种群类W
 ** @author Krise
 */
public final class Metapopulation {

	//信息层传播
	public double lambda;//信息传播率
	public double lambda_0;
	public double lambda_1;
	public double lambda_2;
	public double theta_0;//= 0.
	public double theta_1;//= 0.
	private double theta_2;//= 0.
	public double delta;//A态个体信息恢复率
	//物理层传播
	public double beta_U;//US态个体的感染率
	public double gamma;//比例系数
	public double beta_A = gamma * beta_U; //AS态个体的感染率
	public double mu; //S态个体的恢复率
	//迁移概率
	public double p;
	//网络大小
	public int N;
	// 定义一个邻接矩阵存储patch的相邻关系
	public double[][] adjMatrix_top;
	public double[][] adjMatrix_bot;
	// 转移矩阵
	public double R_top[][];
	public double R_bot[][];

	//装填所有patch的容器
	public ArrayList<Patch> patches = new ArrayList<Patch>(N);
	// 存放编号为id的patch各个邻居patch的id双列集合映射表
	public Map<Integer, ArrayList<Integer>> adjMap_top;

	// 转移矩阵的分布
	public double R_top_dis[][];
	public double R_bot_dis[][];

	/***********************************************************************************************************************************
	/***********************************************************************************************************************************
	/***********************************************************************************************************************************
	 * 
	 * 
	 */
	public Metapopulation(String filePathTop, String filePathBot, int N,//
			double lambda, double lambda_0, double lambda_1, double lambda_2, //
			double theta_0, double theta_1, double theta_2, double delta, //
			double beta_U, double gamma, double mu,//
			double p, boolean isHomo) {
		//
		this.lambda = lambda;
		this.lambda_0 = lambda_0;
		this.lambda_1 = lambda_1;
		this.lambda_2 = lambda_2;
		this.theta_0 = theta_0;
		this.theta_1 = theta_1;
		this.theta_2 = theta_2;
		this.delta = delta;
		this.beta_U = beta_U;
		this.gamma = gamma;
		this.beta_A = gamma * beta_U;
		this.mu = mu;
		this.p = p;
		this.N = N;
		//
		// 从文件中读入一个 随机网络的邻接矩阵
		this.adjMatrix_top = AdjMatrixUtils.loadMatrixFromFile(filePathTop, N);
		this.adjMatrix_bot = AdjMatrixUtils.loadMatrixFromFile(filePathBot, N);
		// 获取转移矩阵
		this.R_top = AdjMatrixUtils.get_R_Mtrix(adjMatrix_top);
		this.adjMap_top = AdjMatrixUtils.getNeigsMapByRows(adjMatrix_top);
		this.R_bot = AdjMatrixUtils.get_R_Mtrix(adjMatrix_bot);
		//转移分布矩阵0.00->1.00
		this.R_top_dis = AdjMatrixUtils.get_R_Dis_Mtrix(R_top);
		this.R_bot_dis = AdjMatrixUtils.get_R_Dis_Mtrix(R_bot);

		// 初始化各个patch， agent， 撒种子......
		if (isHomo) {
			this.seed_homo();
		} else {
			this.seed_heter();
		}
		// System.out.println("已初始化完毕..............");
	}

	/****************************************************************************************************************************************
	 * 
	 * @param networkSize
	 */
	private void seed_homo() {
		//每个patch设置人口为100人
		int patchsize = 100;
		double prob = 0.01;
		for (int i = 0; i < this.N; i++) {
			Patch patch = new Patch(i);
			for (int ind = 0; ind < patchsize; ind++) {
				Agent agent = null;
				if (Math.random() < prob) {
					agent = new Agent(ind, "UI");
				} else {
					agent = new Agent(ind, "US");
				}
				agent.moveToPatchId = i;
				patch.agents.add(agent);
			}
			patches.add(patch);
		}
	}

	/**
	 * 
	 * 初始化各个patch， 按照度数大小，撒种子......
	 * 
	 */
	private void seed_heter() {
		int total = 10000;
		double prob = 0.01;
		double totalWeight = AdjMatrixUtils.getTotalWeight(adjMatrix_top);
		for (int i = 0; i < N; i++) {
			double outWeight = 0;
			for (int c = 0; c < N; c++) {
				outWeight = outWeight + adjMatrix_top[i][c];
			}
			int num = 0;
			if (outWeight != 0) {
				num = (int) Math.round(outWeight * 1.0 / totalWeight * total);
			}
			//System.out.println(num);
			Patch patch = new Patch(i);
			patches.add(patch);
			//添加人口
			for (int ind = 0; ind < num; ind++) {
				Agent agent = null;
				if (Math.random() < prob) {
					agent = new Agent(ind, "UI");
				} else {
					agent = new Agent(ind, "US");
				}
				agent.moveToPatchId = patch.id;
				patch.agents.add(agent);
			}// for2
		}// for N
	}

	/****************************************************************************************************************************************
	/****************************************************************************************************************************************
	/****************************************************************************************************************************************
	*
	*  1. 个体迁移和疾病传播
	*/
	public void moveAndReactUnderBot() {
		//发生移动----------------------------------------------------------------------
		for (Patch patch : patches) {
			int pId = patch.id;
			for (Agent agent : patch.agents) {
				// 移动之前，设置所在patchID*******
				agent.moveToPatchId = pId;
				// 判断个体是否迁移
				if (this.p >= Math.random()) {
					// 迁移到哪个邻居node?
					moveTo: for (int destId = 0; destId < N; destId++) {
						if (R_bot_dis[pId][destId] >= Math.random()) {
							agent.moveToPatchId = destId;
							break moveTo;
						}
					}
				}//if

			}//for agent
		}//for patch
			//移动后反应-------------------------------------------------------------------------
		updateMap_notInfec();
		//
		for (Patch patch : patches) {
			for (Agent agent : patch.agents) {
				//move2Id
				double notInfect_U = map_notInfec.get(patches.get(agent.moveToPatchId))[0];
				double notInfect_A = map_notInfec.get(patches.get(agent.moveToPatchId))[1];
				// 1. 该agent为S态， 计算患病概率
				if (agent.state.equals("US")) { //-->UI
					if (1 - notInfect_U > Math.random()) {
						agent.state = "UI";
					}
					// 2. 该agent为I态，计算恢复的概率
				} else if (agent.state.equals("UI")) { //-->US
					if (mu > Math.random()) {
						agent.state = "US";
					}
				} else if (agent.state.equals("AS")) {//-->AI
					if (1 - notInfect_A > Math.random()) {
						agent.state = "AI";
					}
				} else if (agent.state.equals("AI")) {//-->AS
					if (mu > Math.random()) {
						agent.state = "AS";
					}
				}
			}// for in
		}
	}

	/**
	 * @return
	 */
	private Map<Patch, Double[]> map_notInfec = new LinkedHashMap<Patch, Double[]>(N);

	private void updateMap_notInfec() {
		for (Patch patch : patches) {
			int id_i = patch.id;
			//int n_j_i = 0
			double notInjetProb_U = 1.0;
			double notInjetProb_A = 1.0;
			for (int j = 0; j < R_bot.length; j++) {
				int n_j_i_I = 0;
				if (R_bot[j][id_i] > 0.0 || j == id_i) {
					Patch pTemp = patches.get(j);
					for (Agent agent : pTemp.agents) {
						if (agent.moveToPatchId == id_i) {
							//统计  j->i 的人口数
							if (agent.state.equals("UI") || agent.state.equals("AI")) {
								n_j_i_I++;
							}
						}
					}//
					notInjetProb_U = notInjetProb_U * Math.pow(1 - beta_U, n_j_i_I);
					notInjetProb_A = notInjetProb_A * Math.pow(1 - beta_A, n_j_i_I);
				}
			}//for each j
			Double[] dd = new Double[2];
			dd[0] = notInjetProb_U;
			dd[1] = notInjetProb_A;
			map_notInfec.put(patch, dd);
		}//each patch
	}

	/********************************************************************************************************************************************************
	*********************************************************************************************************************************************************
	*********************************************************************************************************************************************************
	*
	* 2. 种群上的意识传播
	*/
	public void moveAndReactUnderTop() {
		//
		update_map_r_i();
		//获得信息传播率
		for (Patch patch : patches) {
			double r_i = map_r_i.get(patch);
			for (Agent agent : patch.agents) {
				if (agent.state.equals("US")) { // U态个体被传播意识
					if (1 - r_i > Math.random()) {
						agent.state = "AS";
					}
				} else if (agent.state.equals("UI")) { // U态个体处于I态时被传播意识
					double informProb = 1 - (1 - lambda) * r_i;
					if (informProb > Math.random()) {
						agent.state = "AI";
					}
				} else if (agent.state.equals("AS")) {//-->AI
					if (delta > Math.random()) {
						agent.state = "US";
					}
				} else if (agent.state.equals("AI")) {//-->UI
					if (delta > Math.random()) {
						agent.state = "UI";
					}
				}
			}// for in
		}
	}

	/**-------------------------------------------------------------------------------------------------------
	 *
	 */
	private Map<Patch, Double> map_r_i = new LinkedHashMap<Patch, Double>(N);

	private void update_map_r_i() {
		//  map_lambda_star.clear();
		for (Patch patch : patches) {
			//0-simplex (有问题仔细检查检查)-------------------------------------------------------------------
			double r_0_i = 1- patch.getCurrentRhoA() * lambda_0;
			//System.out.println( "rhoA="+ patch.getCurrentRhoA()+"  lambda_0="+lambda_0+"    lambda_0_i="+ lambda_0_i  );
			//1-simplex------------------------------------------------------------------------------------
			List<Integer> neigIds = adjMap_top.get(patch.id);
			double r_1_i = 1.0;
			for (Integer neigId : neigIds) {
				r_1_i = r_1_i * (1 - patches.get(neigId).getCurrentRhoA() * lambda_1);
			}
			//2-simplex-----------------------------------------------------------------------------------
			double r_2_i = 1.0;
			//int count = 0;
			for (int j = 0; j < neigIds.size(); j++) {
				for (int k = j + 1; k < neigIds.size(); k++) {
					if (adjMatrix_top[neigIds.get(j)][neigIds.get(k)] > 0.0) {//
						r_2_i = r_2_i * (1 - patches.get(neigIds.get(j)).getCurrentRhoA() * patches.get(neigIds.get(k)).getCurrentRhoA() * lambda_2);
						//count++;
					}
				}
			}
			//----------------------------------------------------------------------------------------
			double r_i = theta_0 * r_0_i + (1 - theta_0) * theta_1 * r_1_i + (1 - theta_0) * (1 - theta_1) * r_2_i;
			//----------------------------------------------------------------------------------------
			map_r_i.put(patch, r_i);
		}
	}

	/*********************************************************************************************************************************************************************
	 *********************************************************************************************************************************************************************
	 *********************************************************************************************************************************************************************
	 * 
	 * 统计数据
	 */
	public String statistics() {
		int total_US = 0;
		int total_UI = 0;
		int total_AS = 0;
		int total_AI = 0;
		int total = 0;
		for (Patch patch : patches) {
			for (Agent agent : patch.agents) {
				if (agent.state.equals("US")) {
					total_US++;
				} else if (agent.state.equals("UI")) {
					total_UI++;
				} else if (agent.state.equals("AS")) {
					total_AS++;
				} else if (agent.state.equals("AI")) {
					total_AI++;
				}
				total++;
			}
		}
		String str = //
		/*    */total_US * 1.0 / total + "\t" + //
				total_UI * 1.0 / total + "\t" + //
				total_AS * 1.0 / total + "\t" + //
				total_AI * 1.0 / total + "\t" + //
				(total_US + total_UI) * 1.0 / total + "\t" + // U
				(total_AS + total_AI) * 1.0 / total + "\t" + // A
				(total_US + total_AS) * 1.0 / total + "\t" + // S
				(total_UI + total_AI) * 1.0 / total // I
		;
		//System.out.println(total_US + "\t" + total_UI + "\t" + total_AS + "\t" + total_AI + "\t" + total);
		return str;
	}

}// Class
