/**
 * 
 */
package com.example.mainui;

import android.graphics.Bitmap;

/**
 * 回调接口
 * @author zhonghong.chenli
 */
public interface CardSurfaceViewListen {
	/**
	 * 获得图片个数
	 * @return 图片个数
	 */
	public int getCount();
	/**
	 * 目前没用到
	 * @param position position
	 * @return long
	 */ 
	public long getItemId(int position);
	
	/**
	 * 根据索引获得图片
	 * @param csv 显示的主类
	 * @param position 索引
	 * @return 指定的图片
	 */
	public Bitmap getViewBitMap(CardSurfaceView csv,int position);
	/**
	 * 回调获得默认图片
	 * @param csv 显示的主类
	 * @return 默认图片
	 */
	public Bitmap getDefViewBitMap(CardSurfaceView csv);
	
	/**
	 * 
	 * @param index 
	 */
	public void onPlaying(int index);
}
