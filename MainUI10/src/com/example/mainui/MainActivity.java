/**
 * 
 */
package com.example.mainui;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
			bm= BitmapFactory.decodeResource(getResources(), drawables[position]);
			return bm;
		}

		@Override
		public Bitmap getDefViewBitMap(CardSurfaceView csv) {
			// TODO Auto-generated method stub
			Bitmap bm=null;
			bm= BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
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
}
