package com.king.photo.util;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 存放所有的list在最后退出时一起关闭
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:50:49
 */
public class PublicWay {
	public static List<AppCompatActivity> activityList = new ArrayList<AppCompatActivity>();
	
	public static int num = 9;


	/**
	 * 结束指定的Activity
	 */
	private static void finishActivity(AppCompatActivity activity) {
		if (activity != null) {
			activityList.remove(activity);
			if(!activity.isFinishing()) {
				activity.finish();
			}
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls) {
		for (AppCompatActivity activity : activityList) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


	/**
	 * 结束除了指定类名的Activity
	 */
	public static void finishButActivity(Class<?> cls) {
		for (AppCompatActivity activity : activityList) {
			if (!activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


}
