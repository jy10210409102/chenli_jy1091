/**
 * 
 */
package com.example.mainui;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class CardSurfaceView extends GLSurfaceView {

	public MyRenderer mRenderer;
	public CardMap [] cardMaps;
	public static int currentId;
	private TransformControl tfControl;
	int left_right = 0,downId=0;//判断左右，有没有move
	
	private float previousX = 0f,temp=0f;
	private long previousTime = 0l;
	private Context context;
	public CardSurfaceView(Context context) {
		super(context);
		this.context=context;
		mRenderer = new MyRenderer();
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setZOrderOnTop(true);//linearlayout 背景显示全
		this.setRenderer(mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		this.requestRender();
	}
@Override
public void onResume() {
	// TODO Auto-generated method stub
	Log.d("###########resume", "2222");

	super.onResume();
	left_right = 2;
}
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		long time = System.currentTimeMillis();
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				downId=0;//判断是否move
				previousX = x;
				temp=x;
				previousTime = time;
				//tfControl.transform.rotateY=50;
				Log.d("XXX===", ""+x);
				break;
			case MotionEvent.ACTION_MOVE:{
			//	tfControl.transformUp(x-previousX,time-previousTime);
				downId++;
				if((int)x-1>(int)temp)
				{
					tfControl.transformMove(x-previousX,time-previousTime);
					left_right = 1;
					Log.d("#########", "right"+x);
				}
				if((int)x+1<(int)temp)
				{
					tfControl.transformMove(x-previousX,time-previousTime);
					left_right = -1;
					Log.d("#########", "left"+x);
				}
				temp = x;
					break;
				}
			case MotionEvent.ACTION_UP:{
				//tfControl.transform.rotateY = 0;
				left_right = 2;
				tfControl.transformUp(x-previousX,time-previousTime);
				if(downId<3)
				{

						indexlistener(x,time-previousTime); 
					
				}
				MainActivity.nameid.setText("NameId"+currentId);
				break;
			}
		}
		return true;
	}
	public void zoomlistener()
	{
		left_right=0;
		tfControl.setZoomAnt();
	}
	public void indexlistener(float index,long time)
	{
//		if(currentId==0||currentId==14)
//		{
//			return;
//		}
		if(index>0.0f&&index<40.0f)
		{
			currentId-=3;
		}else if(index>40&&index<120){
			currentId-=2;
		}else if(index>120&&index<240.0f){
			currentId-=1;
		}else if(index>240f&&index<750f){
			//中件事件处理……跳转
			if(time>1000)
			{
				Toast.makeText(context, "long time click", 1000).show();//长按
			}else
			{
				//短按
				Intent intent=new Intent();
				intent.setClassName("com.zhonghong.ipod", "com.zhonghong.ipod.MainActivity");
				context.startActivity(intent);
			}
				
			
		}else if(index>750f&&index<900.0f){
			currentId+=1;
		}else if(index>900f&&index<970.0f){
			currentId+=2;
		}else if(index>970f&&index<1024.0f){
			currentId+=3;
		}
	}
	public class MyRenderer implements Renderer {
		private Card [] cards;
		public void refereshList(int size,int id){
			currentId = id;
			cards = new Card[size];
			int j = 0;
			for(int i=0;i<size;i++){
				if(j>=Constant.cardMaps.length){
					j = 0;
				}
				cards[i] = new Card(Constant.cardMaps[j]);
				tfControl.initTransform(cards[i].getTransform(), currentId, i);
				j++;
			}
		}
		
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			gl.glClear(GL10.GL_DEPTH_BUFFER_BIT|GL10.GL_COLOR_BUFFER_BIT);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glEnable(GL10.GL_LIGHTING);
			
			initLight(gl);
			initMaterial(gl);
			float [] positionParams = {0,0,250,1};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams, 0);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			if(cards != null && cards.length>0){//移不到最后
				if(currentId > cards.length){
					currentId = cards.length-1;
				}else if(currentId<-1)
				{
					currentId =0;
				}
				for(int i=0;i<cards.length;i++){
					Card card = cards[i];
					int nexti = i;
//					if(tfControl.isMoveLeft()){
//						nexti = i+1;
//					}else if(tfControl.isMoveRight()){
//						nexti = i-1;
//					}
					if(left_right==1||left_right==-1)
					{
						tfControl.tranformmoveRun(card.getTransform(),currentId,nexti,left_right);
					}
					else if(left_right==2)
					{
						tfControl.tranformRun(card.getTransform(),currentId,nexti,left_right);
					}
					
					tfControl.tranformzoomRun(card.getTransform(),currentId,nexti);
					
						
					drawCard(gl,card);

				}
				if(left_right!=2)
				{
					left_right = 0;
				}
				//Log.d("wendan$########", ""+currentId);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void drawCard(GL10 gl,Card card){
			gl.glLoadIdentity();
			gl.glPushMatrix();
			drawTransform(gl);
			card.drawSelf(gl);
			gl.glPopMatrix();
		}
		
		private void drawTransform(GL10 gl){
			gl.glTranslatef(tfControl.transform.translateX, 0, 0);
			gl.glTranslatef(0, -50, 0);
			gl.glTranslatef(0, 0, tfControl.transform.translateZ);
			
			gl.glRotatef(tfControl.transform.rotateX, 1, 0, 0);
			gl.glRotatef(tfControl.transform.rotateY, 0, 1, 0);
			gl.glRotatef(tfControl.transform.rotateZ, 0, 0, 1);
			//Log.d("########tfControl.transform.rotateY===",""+tfControl.transform.translateX );
		}
		
		private void initLight(GL10 gl){
			gl.glEnable(GL10.GL_LIGHT1);
			float [] ambientParams = {0.1f,0.1f,0.1f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams, 0);
			float [] diffuseParams = {0.5f,0.5f,0.5f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams,0);
			float [] specularParams = {1.0f,1.0f,1.0f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams, 0);
		}
		
		private void initMaterial(GL10 gl){
			float [] ambientMaterial = {0.4f,0.4f,0.4f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial, 0);
			float [] diffuseMaterial = {0.8f,0.8f,0.8f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial, 0);
			float [] specularMaterial = {1.0f,1.0f,1.0f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial, 0);
			float [] shininessMaterial = {1.5f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininessMaterial, 0);
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			height=545;
			gl.glViewport(0, 0, width, height);

			Log.d("#############", ""+width+height);
			float radio = (float)(width/height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-radio, radio, -1, 1, 1, 500);
			GLU.gluLookAt(gl, 0, 0, 250, 0, 0, 0, 0, 1, 0);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
			// TODO Auto-generated method stub
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			gl.glClearColor(0, 0, 0, 0);
			gl.glShadeModel(GL10.GL_SMOOTH);
			//wendan //图片边源没黑边
		    gl.glEnable(GL10.GL_BLEND);  
		    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  
		  
		    gl.glEnable(GL10.GL_DEPTH_TEST);  
		    gl.glEnable(GL10.GL_ALPHA_TEST);  // Enable Alpha Testing (To Make BlackTansparent)  
		  
		    gl.glAlphaFunc(GL10.GL_GREATER,0.4f);  
			//end
			//if(Constant.cardMaps == null || Constant.cardMaps.length == 0){
				BitmapLoader.loadTexturing(gl, getResources());
		//	}
			tfControl = new TransformControl(context);
			tfControl.transformShow();
			refereshList(15,7);
		}
	}
}
