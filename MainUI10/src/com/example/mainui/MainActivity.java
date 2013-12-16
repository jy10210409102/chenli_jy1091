/**
 * 
 */
package com.example.mainui;


import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class MainActivity extends Activity {

	CardSurfaceView surfaceview;
	LinearLayout GlLinear;
	Button test;
	static TextView nameid;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initCardSurfaceView();
		requestWindowFeature(getWindow().FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 setContentView(R.layout.activity_main);
		
		//surfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		nameid = (TextView)findViewById(R.id.nameid);
		test = (Button)findViewById(R.id.test);
		test.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				surfaceview.zoomlistener();
			}
		});
		GlLinear = (LinearLayout)findViewById(R.id.gllinear);
		GlLinear.addView(surfaceview);
		
//		
//		setContentView(surfaceview);
//		
		
		//chenli  获取屏幕信息
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;
		Constant.WIDTH = width;
		Constant.HEIGHT = height;
//		if(width>height){
//			Constant.WIDTH = width;
//			Constant.HEIGHT = height;
//		}else{
//			Constant.WIDTH = height;
//			Constant.HEIGHT = width;
//		}
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//surfaceview.onPause();
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		surfaceview.onResume();
	} 
	
	
	/**
	 * 初始化surfaceview 并作其它操作
	 */
	private void initCardSurfaceView(){
		surfaceview = new CardSurfaceView(this);
		surfaceview.setCardSurfaceViewListen(msurfaceViewListen);
		
	}
	
	private CardSurfaceViewListen msurfaceViewListen =new CardSurfaceViewListen(){

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return drawables.length;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Bitmap getViewBitMap(CardSurfaceView csv, int position) {
			// TODO Auto-generated method stub
			Bitmap bm=null;
			bm= BitmapFactory.decodeResource(getResources(), drawables[position]);//获得图片
			//画图  添加歌曲信息
			
			return drawMusicInfo(bm);
		}

		@Override
		public Bitmap getDefViewBitMap(CardSurfaceView csv) {
			Bitmap bm=null;
			bm= BitmapFactory.decodeResource(getResources(), R.drawable.music_photo);
			return bm;
		}

		@Override
		public void onPlaying(int i) {
			// TODO Auto-generated method stub
			if(i == -1){
				if( surfaceview.getVisibility() == View.VISIBLE){
					//发handler 隐藏 surfaceview
				}
			}else {
				//暂不处理
			}
		}
		
	};
	
	/**
	 * 图片资源
	 */
	private static int [] drawables = {
		R.drawable.app,R.drawable.btm,
		R.drawable.dvd,R.drawable.gps,
		R.drawable.ipod,R.drawable.jsq,
		R.drawable.phone,R.drawable.radio,
		R.drawable.rl,R.drawable.set,
		R.drawable.tv,R.drawable.video,
		R.drawable.weather,R.drawable.photo,
		R.drawable.music
		};
	
	/**
	 * 拼接图片 组合歌曲信息 	画图顺序【1.先画有效图片 2.再画图片框 3.再画文字 4.最后画倒影】
	 * @param bitmap  图片底
	 * @return  	  拼接后的图片
	 */
	private  Bitmap drawMusicInfo(Bitmap bitmap){
		Bitmap defBitmap=msurfaceViewListen.getDefViewBitMap(null);  //获得默认图片底
		WeakReference<Bitmap> mweakReference = new WeakReference<Bitmap>(defBitmap);  //尝试用弱引用
		int width = mweakReference.get().getWidth();
		int height = mweakReference.get().getHeight();
		//构成原始图片大小的画布
		Bitmap defaultSizeBitmap = Bitmap.createBitmap(width, height,Config.ARGB_8888);
		Canvas canvas = new Canvas(defaultSizeBitmap);   //  原始背景图片大小

		//最终画布
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,(height + height / 4 - 10), Config.ARGB_8888);   //设置画布大小  
		Canvas endCanvas = new Canvas(bitmapWithReflection);   //  整个大小的画布
		
		//画有效图片有效图片起始位置    x:14 y:12    有效图片大小 142 140
		bitmap =drawSetSizePhoto(bitmap,142,140);
		canvas.drawBitmap(bitmap, 14, 12, null);
		// 画出原始图像
		canvas.drawBitmap(defBitmap, 0, 0, null);//画原始图片
		//原始图片上画上歌曲信息    歌曲名起始位置 15  119    允许的最大大小：141 32
		String[] musicInfo = getMusicInfo();
		Paint deafaultPaint = new Paint(); 
		deafaultPaint.setColor(Color.WHITE);
		deafaultPaint.setAntiAlias(true);  
		
		//歌曲名
		deafaultPaint.setTextSize(16);
		float[] musicNameLoction =getCenterLoction(deafaultPaint, musicInfo[0], 15, 130, 141, 32);
		canvas.drawText(musicInfo[0], musicNameLoction[0], musicNameLoction[1], deafaultPaint);  //歌曲名
		
		//歌手名起始位置 13 157  允许最大大小 154 27
		deafaultPaint.setTextSize(18);
		float[] songerNameLoction =getCenterLoction(deafaultPaint, musicInfo[1], 13, 157+15, 154, 27);
		canvas.drawText(musicInfo[1], songerNameLoction[0], songerNameLoction[1], deafaultPaint);  //歌手名
		
		//专辑名起始位置 13 185  允许最大大小 147 16 
		deafaultPaint.setTextSize(16);
		float[] specialNameLoction =getCenterLoction(deafaultPaint, musicInfo[2], 13, 185+15, 147, 16);
		canvas.drawText(musicInfo[2], specialNameLoction[0], specialNameLoction[1], deafaultPaint);  //专辑名
		
		//获得倒影图片
		Bitmap invertImage=getInvertImage(defaultSizeBitmap,(float)0.25);
		
		//画正立的图片
		endCanvas.drawBitmap(defaultSizeBitmap, 0, 0, null);
		 
		//画倒影图片
		endCanvas.drawBitmap(invertImage, 0, height-10, null);
	
		Matrix mMatrix = new Matrix();
		endCanvas.setMatrix(mMatrix);
		//画阴影效果
		Paint shadowPaint=new Paint();
		LinearGradient shader = new LinearGradient(0, height-10, width, bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		shadowPaint.setShader(shader);
		shadowPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 画一个长方形使用油漆与我们的线性梯度
		endCanvas.drawRect(0, height-10, width,bitmapWithReflection.getHeight(), shadowPaint);
		
		// 解决图片的锯齿现象
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		return bitmapWithReflection;
	}
   

    
	
	/**
	 * 设置原始图片 和 变换后的图片宽高获得bitmap对象
	 * @param bitmap  	原始图片
	 * @param newWidth  新款
	 * @param newHeight	新高
	 * @return  		改变后的图片
	 */
	private Bitmap drawSetSizePhoto(Bitmap bitmap,int newWidth , int newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return bitmap;
	}
	
	/**
	 * 返回模拟的歌曲信息   【歌曲名、歌手、专辑名、】
	 * @return 歌曲信息
	 */
	public String[] getMusicInfo(){
		String[] strs = {"这是歌曲名","这是歌手名","这是专辑名"};
		return strs;
	} 
	
	
	/**
	 * 获取文字居中时的位置
	 * @param mPaint    画文字的画笔
	 * @param showTxt   要画的文字
	 * @param startX	文字范围在X轴上的起始位置
	 * @param startY	文字范围在Y轴上的起始位置
	 * @param maxW		文字范围允许最大的宽
	 * @param maxH		文字范围允许最大的高
	 * @return			文字画的位置 x,y
	 */
	public float[] getCenterLoction(Paint mPaint , String showTxt ,float startX , float startY ,float maxW, float maxH){
		float[] mLoction =new float[2];
		float textSize =mPaint.getTextSize(); 
		float textW = textSize*showTxt.length();
		float textH = textSize;
		if(textW<maxW){
			mLoction[0]=startX+(maxW-textW)/2;
		}else{
			mLoction[0]=startX;
		}
		mLoction[1]=startY+(maxH-textH)/2;
		return mLoction;
	}
	
	/**
	 * 返回传入图片的倒影，scale范围为0 ~ 1
	 * @param bitmap 传入图片
	 * @param scale  倒影占原图的比例   
	 * @return 		 倒影图片
	 */
	private Bitmap getInvertImage(Bitmap bitmap ,float scale) {
		if (scale > 1) {
			scale = 1;
		}
		if(scale < 0){
			 throw new IllegalArgumentException("sacle 范围：0 ~ 1"); 
		}
		Matrix matrix = new Matrix();
		// 解析 ：图片缩放，x轴缩小0.5倍，y轴扩大2.5倍：mMatrix.setScale(0.5f, 2.5f);效果：
		matrix.preScale(1, -1);
		float height = bitmap.getHeight();
		int width = bitmap.getWidth();
		int startHeight = (int)(height-height*scale);
		int photoHeight = (int)(height*scale);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0,startHeight, width, photoHeight, matrix, false); // chenli 倒影
		return reflectionImage;
	}
}
