/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.test;

import java.util.ArrayList;
import java.util.List;

import org.gridsofts.json.JacksonHelper;

/**
 * @author lei
 */
public class TestJacksonHelper {

	public static void main(String[] args) {
		List<User> list = new ArrayList<User>();
		for (int i = 0; i < 10; i++) {
			User u = new User();
			u.setName("u" + i);

			List<Score> scoreList = new ArrayList<Score>();
			u.setScoreList(scoreList);
			scoreList.add(new Score());
			scoreList.add(new Score());
			scoreList.add(new Score());

			list.add(u);
		}

		String s = JacksonHelper.newInstance().toJson(list);
		System.out.println(s);

		List<User> l = JacksonHelper.newInstance().getList(s, User.class);
		System.out.println(l.size());
	}

	public static class User {
		private String name;
		private List<Score> scoreList;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the scoreList
		 */
		public List<Score> getScoreList() {
			return scoreList;
		}

		/**
		 * @param scoreList
		 *            the scoreList to set
		 */
		public void setScoreList(List<Score> scoreList) {
			this.scoreList = scoreList;
		}
	}

	public static class Score {
		private float v = 5;

		/**
		 * @return the v
		 */
		public float getV() {
			return v;
		}

		/**
		 * @param v
		 *            the v to set
		 */
		public void setV(float v) {
			this.v = v;
		}
	}
}
