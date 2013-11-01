/**
 * 
 */
package com.example.mainui;


import android.app.Activity;
import android.content.pm.ActivityInfo;
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
		
		requestWindowFeature(getWindow().FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 setContentView(R.layout.activity_main);
		surfaceview = new CardSurfaceView(this);
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

}
