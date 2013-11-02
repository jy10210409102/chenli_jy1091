/**
 * 
 */
package com.example.mainui;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 显示主类<br/>
 * GLSurfaceView的大部分代码和代码结构设计对你在以后的编码过程中设计自己的功能模块时有很好的借鉴作用。
 * @author Administrator
 *
 */
public class CardSurfaceView extends GLSurfaceView {
																	//有两个缓存 一个背后加载 一个当前显示
	private final int CACHED_OBJ_NUM = 31;  						//不可见的缓存大小  个数
	private final int VISABLE_OBJ_NUM = 11;							//可见的缓存大小  个数
	private final int ENTRY_SIZE = 4;								//初始思想 类似c中sizeof获得的单位大小
	private final int lrucachesize = CACHED_OBJ_NUM * ENTRY_SIZE;   //缓存大小
	// chenli
	public View playView = null;
	/** 回调接口CardSurfaceViewListen 对象 */
	public CardSurfaceViewListen mcardsvl = null;
	/** Cache保存一个强引用来限制内容数量，每当Item被访问的时候，此Item就会移动到队列的头部。当cache已满的时候加入新的item时，在队列尾部的item会被回收。*/
	private LruCache<String, Card> mcardCache;// 
	/** card数组*/
	public Card[] defCard = null;
	/** 默认的纹理id*/
	public int idefTexId = -1; 					
	// chenli end
	
	
	
	public MyRenderer mRenderer;
	public CardMap [] cardMaps;
	public static int currentId;
	/** 变形控制类对象*/
	private TransformControl tfControl;
	int left_right = 0,downId=0;//�ж����ң���û��move
	
	private float previousX = 0f,temp=0f;
	private long previousTime = 0l;
	private Context context;
	
	public CardSurfaceView(Context context) {
		super(context);
		this.context=context;
		mRenderer = new MyRenderer();
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);//指定red ,green, blue, alpha, depth ,stencil 支持的位数，缺省为RGB_565 ,16 bit depth buffer.
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setZOrderOnTop(true);//linearlayout ������ʾȫ
		this.setRenderer(mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); //连续不断渲染模式
		this.requestRender();
		/*
		 * GLSurfaceView 的渲染模式有两种，一种是连续不断的更新屏幕，
		 * 另一种为on-demand ，只有在调用requestRender()  在更新屏幕。 
		 * 缺省为RENDERMODE_CONTINUOUSLY 持续刷新屏幕。
		 * */
		mcardCache =new LruCache<String, Card>(lrucachesize){			//初始化缓存类
			@Override
			protected int sizeOf(String key, Card value) {
				// TODO Auto-generated method stub
				return ENTRY_SIZE;
			}
			
		};
	}
	
	/**
	 *  陈立start 
	 */
	/**
	 * 清除缓存 并设置缓存中的card纹理id为默认图片纹理
	 */
	public void clearCardCache(){
		synchronized(mcardCache){
			for(int i=0;i<CACHED_OBJ_NUM;i++){
				defCard[i].changeCardTexId(idefTexId);
			}
		}
		
	}
	
	/**
	 * 获得默认图片纹理 并存在idefTexId中 初始化 defCard
	 */
	public void genDefTextureCard(GL10 gl){
		CardMap cmap = null;
		Bitmap defbm = null;
		defCard = new Card[CACHED_OBJ_NUM];  //初始化defCard
		if((gl !=null) && mcardsvl != null){   
			defbm = mcardsvl.getDefViewBitMap(null);
			idefTexId = genTextureId(gl,defbm); //获得默认图片的纹理
		}
		
		for(int i=0; i<CACHED_OBJ_NUM;i++){
			if( gl != null && tfControl != null){
				cmap =new CardMap();
				cmap.texId = idefTexId;
				cmap.width = 200;
				cmap.height = 250;
				defCard[i] =new Card(cmap);
				tfControl.initTransform(defCard[i].getTransform(),currentId, i);     // 变形 unknown 
			}else{
				Log.e("chenli", " tfControl="+tfControl +" gl"+gl );
			}
		}
	}
	
	/**
	 * 返回指定图片的纹理Id
	 * @param gl opengl
	 * @param bm 指定的图片
	 * @return 纹理Id
	 */
	private  int genTextureId(GL10 gl , Bitmap bm){
		int curtextureId=-1;
		if(( gl != null ) && ( bm != null)){
			int [] textures = new int[1];
			gl.glGenTextures(1, textures, 0);   // glGenTextures () 函数生成一个唯一 号 
			curtextureId = textures[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, curtextureId);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);
			bm.recycle();
		}else{
			Log.e("chenli", " gl="+gl+" bm="+bm);
		}
		return curtextureId;
	}
	/**
	 * 陈立 end
	 */
	
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
		
		if(playView!=null){ 						//chenli
			playView.getParent().requestDisallowInterceptTouchEvent(true);//，也就是ViewPager不要拦截该控件上的触摸事件。
		}
		
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				downId=0;//�ж��Ƿ�move
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
				//chenli
				if(currentId<0){
					 currentId=0;
				}
				if(currentId>=mcardsvl.getCount()){
					currentId=mcardsvl.getCount()-1;
				}
				if(mcardsvl!=null){
					mcardsvl.onPlaying(currentId);
				}
				//chenli end
				
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
			//�м��¼����?����ת
			if(time>1000)
			{
				Toast.makeText(context, "long time click", 1000).show();//����
			}else
			{
				//�̰�
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
				if(j>=Constant.cardMaps.length/*test mcardsvl.getCount()*/){
					j = 0;
				}
				cards[i] = new Card(Constant.cardMaps[j]);
				tfControl.initTransform(cards[i].getTransform(), currentId, i);
				j++;
			}
		}
		
		//record
		/**
		 * 定义实际的绘图操作    一直在跑
		 */
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			//语句的作用是用当前缓冲区清除值，也就是glClearColor或者glClearDepth等函数所指定的值来清除指定的缓冲区
			gl.glClear(GL10.GL_DEPTH_BUFFER_BIT|GL10.GL_COLOR_BUFFER_BIT);
			gl.glShadeModel(GL10.GL_SMOOTH); //选择恒定或光滑着色模式。  这里是光滑着色模式
			gl.glEnable(GL10.GL_DEPTH_TEST); //启用服务器端GL功能。 做深度比较和更新深度缓存
			gl.glEnable(GL10.GL_CULL_FACE);  //如果启用，基于窗口坐标采集多边形
			gl.glEnable(GL10.GL_LIGHTING);	 //如果启用，用当前光线参数计算顶点颜色
			
			initLight(gl);
			initMaterial(gl);
			float [] positionParams = {0,0,250,1};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams, 0);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			if(cards != null && cards.length>0){//�Ʋ������
				if(currentId > cards.length){
					currentId = cards.length-1;
				}else if(currentId<-1)
				{
					currentId =0;
				}
				
				int j=0; //chenli
//				for(int i=0;i<cards.length;i++){
				//取离中间 左右相距VISABLE_OBJ_NUM/2以内的card
				for(int i=mcardsvl.getCount()-1;i>=0;i--){
					if(i<=currentId){
						j=currentId-i;
					}else{
						j=i;
					}
					if (Math.abs(j - currentId) < VISABLE_OBJ_NUM / 2) {

						Card card = getCard(gl, j);// cards[i];
						int nexti = i;
						// if(tfControl.isMoveLeft()){
						// nexti = i+1;
						// }else if(tfControl.isMoveRight()){
						// nexti = i-1;
						// }
						// 变形
						if (left_right == 1 || left_right == -1) {
							tfControl.tranformmoveRun(card.getTransform(),
									currentId, nexti, left_right);
						} else if (left_right == 2) {
							tfControl.tranformRun(card.getTransform(),
									currentId, nexti, left_right);
						}

						tfControl.tranformzoomRun(card.getTransform(),
								currentId, nexti);

						// 画card
						drawCard(gl, card);
					}

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
		
		
		private Card getCard(GL10 gl,int index){
			Card card=null;
			Bitmap bm=null;
			int texId=-1;
			synchronized (mcardCache) {
				//如果缓冲中取出来不为空
				if((card=mcardCache.get(Integer.toString(index)))!=null){
					return card;
				}
			}
			//如果缓冲中取出来为空 则从所有中取
			card =defCard[index%CACHED_OBJ_NUM];
			bm=mcardsvl.getViewBitMap(null, index);
			if(bm!=null){
				synchronized (mcardCache) {
					removeDumpCachedcard(gl,card);
				}
			}
			
		}
		
		/**
		 * 删除mcardCache中的card和对应gl中的唯一纹理ID
		 * @param gl opengl
		 * @param card mcardCache要移除的card
		 */
		private void removeDumpCachedcard(GL10 gl,Card card){
			
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
		
		
		/**
		 * 设置光线参数
		 */
		private void initLight(GL10 gl){
			gl.glEnable(GL10.GL_LIGHT1);//如果启用，包含光线i在光线方程的评价中
			float [] ambientParams = {0.1f,0.1f,0.1f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams, 0);
			float [] diffuseParams = {0.5f,0.5f,0.5f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams,0);
			float [] specularParams = {1.0f,1.0f,1.0f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams, 0);
		}
		
		/**
		 * 为光线模式指明材质参数（矩阵版本）
		 */
		private void initMaterial(GL10 gl){
			float [] ambientMaterial = {0.4f,0.4f,0.4f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial, 0);  //为光线模式指明材质参数（矩阵版本）
			float [] diffuseMaterial = {0.8f,0.8f,0.8f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial, 0);
			float [] specularMaterial = {1.0f,1.0f,1.0f,1.0f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial, 0);
			float [] shininessMaterial = {1.5f};
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininessMaterial, 0);
		}

		//record
		/**
		 * 如果设备支持屏幕横向和纵向切换，这个方法将发生在横向<->纵向互换时。此时可以重新设置绘制的纵横比率。
		 */
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

		//record
		/**
		 * 在这个方法中主要用来设置一些绘制时不常变化的参数，比如：背景色，是否打开 z-buffer等。
		 */
		public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
			// TODO Auto-generated method stub
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			gl.glClearColor(0, 0, 0, 0);
			gl.glShadeModel(GL10.GL_SMOOTH);
			//wendan //ͼƬ��Դû�ڱ�
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
