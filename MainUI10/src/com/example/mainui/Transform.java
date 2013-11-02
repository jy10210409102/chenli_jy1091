/**
 * 
 */
package com.example.mainui;

/**
 * @author Administrator
 *
 */
public class Transform {
	/** X轴的位置*/
	public float translateX;
	/** Y轴的位置  如果只是横向动作 则为零 不变*/
	public float translateY;
	/** Z轴 景深效果  即调整大小*/
	public float translateZ;
	
	/** 绕X轴旋转角度*/
	public float rotateX;
	/** 绕Y轴旋转角度*/
	public float rotateY;
	/** 绕Z轴旋转角度*/
	public float rotateZ;
}
