package com.kk.utils;

import java.io.*;

public class FileUtils {

	/**
	 * @param filePath
	 * @param str
	 */
	public static void append2File(String filePath, String str) {
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new PrintWriter(new FileWriter(filePath, true), true));
			out.append(str);
			//out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}// method

}//C
