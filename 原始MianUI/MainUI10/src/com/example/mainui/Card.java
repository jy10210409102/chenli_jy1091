/**
 * 
 */
package com.example.mainui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 * @author Administrator
 *
 */
public class Card {

	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	
	private int vCount;
	private float radioh;
	private float radiow;
	private Transform transform = new Transform();
	private CardMap cardMap;
	
	public Card(CardMap cardMap){
		this.cardMap = cardMap;
		this.radioh = cardMap.height/Constant.HEIGHT;
		this.radiow = cardMap.width/Constant.WIDTH;
		initData();
	}
	
	public Transform getTransform(){
		return this.transform;
	}
	
	private void initData(){
		vCount = 4;
		float w = (cardMap.width*radiow);
		float h = (cardMap.height*radioh);
		float [] vertexs = {
				-w,-h,0,//4
				w,-h,0,//3
				w,h,0,//2
				-w,h,0,//1
		};
		Log.d("wh---hei", ""+w+"##"+h);
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length*4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);
		
		float [] textures = {
				0,1,//4
				1,1,//3
				1,0,//2
				0,0,//1
		};
		ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textures);
		textureBuffer.position(0);
	}
	
	public void drawSelf(GL10 gl){
		gl.glTranslatef(transform.translateX, 0, 0);
		gl.glTranslatef(0, transform.translateY, 0);
		gl.glTranslatef(0, 0, transform.translateZ);
		
		gl.glRotatef(transform.rotateX, 1, 0, 0);
		gl.glRotatef(transform.rotateY, 0, 1, 0);
		gl.glRotatef(transform.rotateZ, 0, 0, 1);
	//	Log.d("#!!!######tfControl.transform.rotateY===",""+transform.translateX+" "+transform.translateY+" "+transform.translateZ);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, cardMap.texId);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vCount);
	}
}
