/**
 * 
 */
package com.example.mainui;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * 加载图片得到CardMap数组 CardMap数组记录所有要现实的的图片纹理和大小
 * @author Administrator
 *
 */
public class BitmapLoader {
	
//	private static int [] drawables = {
//			R.drawable.default_card_bmp_00,R.drawable.default_card_bmp_01,
//			R.drawable.default_card_bmp_02,R.drawable.default_card_bmp_03,
//			R.drawable.default_card_bmp_04,R.drawable.default_card_bmp_05,
//			R.drawable.default_card_bmp_06,R.drawable.default_card_bmp_07,
//			R.drawable.default_card_bmp_08,R.drawable.default_card_bmp_09,
//			R.drawable.default_card_bmp_10,R.drawable.default_card_bmp_11,
//			R.drawable.default_card_bmp_12,R.drawable.default_card_bmp_13,
//			R.drawable.default_card_bmp_14,R.drawable.default_card_bmp_15,
//			R.drawable.default_card_bmp_16,R.drawable.default_card_bmp_17,
//			R.drawable.default_card_bmp_18,R.drawable.default_card_bmp_19,
//			R.drawable.default_card_bmp_20
//	};
	
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
	 * 得到CardMap数组并把数组存在静态Constant<br/>
	 * 陈立<br/>
	 * @param gl 	GL10接口包含了Java（TM）程序语言为OpenGL绑定的核心功能。
	 * @param res	context
	 */
	public static void loadTexturing(GL10 gl,Resources res){
		CardMap [] cardMaps = new CardMap[drawables.length];
		for(int i=0;i<drawables.length;i++){
			cardMaps[i] = initTextureId(gl, res, drawables[i]);   //获得纹理
		}
		Constant.cardMaps = cardMaps;
	}
	
	/**
	 * 获得CardMap<br/>
	 * 陈立<br/>
	 * 纹理绑定
	 * 在为纹理生成名称后，在为纹理提供图像数据之前，我们必须绑定纹理。绑定使得指定纹理处于活动状态。一次只能激活一个纹理。活动的或“被绑定”的纹理是绘制多边形时使用的纹理，也是新纹理数据将加载其上纹理，所以在提供图像数据前必须绑定纹理。
	 *  gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
	 *  绑定纹理数据，传入指定图片
	 *  GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);   
	 * @param gl  	GL10接口包含了Java（TM）程序语言为OpenGL绑定的核心功能。
	 * @param res  	context
	 * @param id    图片资源id
	 * @return  	CardMap
	 */
	private static CardMap initTextureId(GL10 gl,Resources res,int id){
		CardMap cardMap = new CardMap();
		int [] textures = new int[1];
		gl.glGenTextures(1, textures, 0);   // glGenTextures () 函数生成一个唯一 号 
		int curtextureId = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, curtextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
		InputStream is = res.openRawResource(id);
		Bitmap bitmap = null;
		try{
			bitmap = BitmapFactory.decodeStream(is);
			cardMap.texId = curtextureId;  
			cardMap.width = 350;//bitmap.getWidth();
			cardMap.height = 300;//bitmap.getHeight();
			Log.d("with", ""+cardMap.width+"height"+cardMap.height);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		return cardMap;
	}
}
