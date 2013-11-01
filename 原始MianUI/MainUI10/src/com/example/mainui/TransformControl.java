/**
 * 
 */
package com.example.mainui;

import static com.example.mainui.Constant.MA;
import static com.example.mainui.Constant.MC;
import static com.example.mainui.Constant.MX;
import static com.example.mainui.Constant.MZ;
import android.content.Context;
import android.content.Intent;
import android.util.Log;




/**
 * @author Administrator
 *
 */
public class TransformControl {

	public Transform transform;
	
	private final static int MOVE_STATIC = 0;
	private final static int MOVE_RIGHT = 1;
	private final static int MOVE_LEFT = 2;
	
	private int zoom_ant = 0;//wendan add 
	private Context context;
	private int move = MOVE_STATIC;
	int targetindex = 0,currentindex=0,zoomindex=0;
	
	public TransformControl(Context context){
		transform = new Transform();
		this.context = context;
	}
	
	public void transformMove(float dx){
		
	}
	
	public void transformDown(float dx){
		move = MOVE_LEFT;
	}
//
	public void transformMove(float dx,long time){
		int dv = Math.round(dx/(float)time);
	//	targetindex = Math.abs(dv*2);
	//	currentindex = CardSurfaceView.currentId;
		if(dx<0){
		//	CardSurfaceView.currentId += 1;
			
			setMoveLeft();
		}else{
			//CardSurfaceView.currentId -= 1;
			//targetindex = -targetindex;
			setMoveRight();
		}
		
	}
//
	public void transformUp(float dx,long time){
		int dv = Math.round(dx/(float)time);
		int index = Math.abs(dv*2);
		if(index == 0){
			index = 1;
		}
		if(dx<0){
			//CardSurfaceView.currentId += index;
			setMoveLeft();
		}else{
			//CardSurfaceView.currentId -= index;
			setMoveRight();
		}
		//�ƶ��ٶȿ���
		if(dv>=2)
		{
			CardSurfaceView.currentId -= 2;	
		}else if(dv<=-2)
		{
			CardSurfaceView.currentId += 2;	
		}
		else if(dv>=1)
		{
			CardSurfaceView.currentId -= 1;	
		}else if(dv<=-1)
		{
			CardSurfaceView.currentId += 1;	
		}
		
		//Log.d("==========", "======dx="+dx+"==time="+time+"===currentId="+CardSurfaceView.currentId);
		
	}
	
	public boolean isMoveRight(){
		return move == MOVE_RIGHT;
	}
	
	public boolean isMoveLeft(){
		return move == MOVE_LEFT;
	}
	
	public boolean isMoving(){
		return move != MOVE_STATIC;
	}
	
	private void setMoveRight(){
		move = MOVE_RIGHT;
	}
	
	private void setMoveLeft(){
		move = MOVE_LEFT;
	}
	
	public void setZoomAnt()
	{
		zoom_ant = 1;
	}
	
	public void transformShow(){
		Thread thread = new Thread(new Runnable(){
			boolean isShowFlag = true;
			public void run() {
				float dy = Constant.HEIGHT/2;
				while(isShowFlag){
					dy = dy-30;
					if(dy<=0){
						dy = 0;
						isShowFlag = false;
					}
					transform.translateY = dy;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	public float getTransformX(int currentId,int i){
		float rx = (i-currentId)*MC;
		if(rx<0){
			rx += -MX;
		}else if(rx > 0){
			rx += MX;
		}else{
			rx = 0;
		}
		return rx;
	}
	
	public float getTransformZ(int currentId,int i){
		float rz = Math.abs((i-currentId))*MZ;
		return -rz;
	}
	
	public float getTransformA(int currentId,int i){
		if(i>currentId){
			return -MA;
		}else if(i<currentId){
			return MA;
		}
			return 0;
	}
	
	public void initTransform(Transform tf,int currentId,int i){
		float rx = getTransformX(currentId, i);
		float rz = getTransformZ(currentId, i);
		float ra = getTransformA(currentId, i);
		tf.translateX = rx;
		tf.translateZ = rz;
		tf.rotateY = ra;
	}
	
	//wendan  //�¼�����
	public void tranformzoomRun(Transform btf,int currentId,int i){
		if(zoom_ant!=1)
		{
			return;
		}
		if((i==currentId-3||i==currentId+3)&&zoomindex==0)
		{
			Log.d("#########1111", ""+i+"  "+btf.translateX);
			if(btf.translateX>0)
			{
				btf.translateX-=15;	
			}else
			{
				btf.translateX+=15;
			}
			if(Math.abs(btf.translateX)<=15)
			{
				zoomindex++;//������һ����
			}
			if(Math.abs(btf.translateX)<=2*MC)
			{
				btf.translateX = 0;
			}
		}else if((i==currentId-2||i==currentId+2)&&(zoomindex==1))
		{
			Log.d("#########222222", ""+i+"  "+btf.translateX);
			if(btf.translateX>0)
			{
				btf.translateX-=15;	
			}else
			{
				btf.translateX+=15;
			}
			if(Math.abs(btf.translateX)<=15)
			{
				zoomindex++;//������һ����
			}
			if(Math.abs(btf.translateX)<=MC)
			{
				btf.translateX = 0;
			}
		}else if((i==currentId-1||i==currentId+1)&&zoomindex==2)
		{
			Log.d("#########3333", ""+i+"  "+btf.translateX);
			if(btf.translateX>0)
			{
				btf.translateX-=15;	
			}else
			{
				btf.translateX+=15;
			}
			if(Math.abs(btf.translateX)<=15)
			{
				zoomindex=0;//��������
				zoom_ant=0;
				Intent intent=new Intent();
				intent.setClassName("com.zhonghong.ipod", "com.zhonghong.ipod.MainActivity");
				context.startActivity(intent);
			}
		}
		
	}
	//move
	public void tranformmoveRun(Transform btf, int currentId, int i,
			int rightleft) {
		if (!isMoving()) {
			return;
		}
		if (rightleft == 1) {
			btf.translateX += 10;

		} else {
			btf.translateX -= 10;
		}

		if (rightleft == 1) {
			if (btf.translateX >= 0) {
				btf.translateZ -= 8;
			} else {
				btf.translateZ += 8;
			}
			if (Math.abs(btf.translateX) < MC) {
				btf.rotateY -= 4;
			}
		} else {
			if (btf.translateX <= 0) {
				btf.translateZ -= 8;
			} else {
				btf.translateZ += 8;
			}
			if (Math.abs(btf.translateX) < MC) {
				btf.rotateY += 4;
			}
		}

		if (Math.abs(btf.translateX) < 10) {
			btf.translateZ = 0;
			btf.rotateY = 0;
		}
		if (Math.abs(btf.rotateY) == 12) {

		}
		if (btf.translateX <= MC / 2 + 5 && btf.translateX > MC / 2 - 5) {
			if (rightleft == 1) {
				CardSurfaceView.currentId -= 1;
			} else {
				CardSurfaceView.currentId += 1;
			}
		}

		// if(rightleft==1)
		// {
		//
		// btf.rotateY -=5;
		//
		// }else
		// {
		// btf.rotateY+=5;
		// }
		if (btf.rotateY > MA) {
			btf.rotateY = MA;
		}
		if (btf.rotateY < -MA) {
			btf.rotateY = -MA;
		}
	}

	// end
	
	public void tranformRun(Transform btf,int currentId,int i,int rightleft){
//		if(!isMoving()){
//			return;
//		}
		
		float targetX=getTransformX(currentId,i);
		float targetZ=getTransformZ(currentId,i);
		float targetA=getTransformA(currentId,i);
				
		if(Math.abs((targetX - btf.translateX)) < 0.1f){
			btf.translateX = targetX;
		}else{
			btf.translateX += ((float)(targetX - btf.translateX)/4);
		}
		
		if(Math.abs((targetZ - btf.translateZ)) < 0.1f){
			btf.translateZ = targetZ;
		}else{
			btf.translateZ += ((float)(targetZ - btf.translateZ)/5);
		}
		
		if(Math.abs((targetA - btf.rotateY)) < 0.1f){
			btf.rotateY = targetA;
		}else{
			btf.rotateY += ((float)(targetA - btf.rotateY)/4);
		}
//		if((i==currentId&&btf.rotateY ==targetA)&&rightleft!=2)
//		{
//			Log.d("11232", "qq##############"+CardSurfaceView.currentId);
//			if(rightleft==1)
//			{
//				CardSurfaceView.currentId --;
//				setMoveLeft();
//			}
//			else
//			{
//				CardSurfaceView.currentId ++;
//				setMoveRight();
//			}
//		}
	}
}
