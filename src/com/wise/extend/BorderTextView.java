package com.wise.extend;

import com.wise.wawc.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TextView
 * @author honesty
 */
public class BorderTextView extends TextView{
	
	private  Paint p = null;  
	private int sroke_width = 1;
	boolean isLeft = false;
	boolean isTop = false;
	boolean isRight = false;
	boolean isBottom = false;
			
	
	public BorderTextView(Context context){
		super(context);
		initCustomView();
	}
	public BorderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCustomView();
//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom);
//		int indexCount = a.getIndexCount();
//		System.out.println("indexCount = " + indexCount);
//		for(int i = 0 ; i < indexCount; i++){
//			int index = a.getIndex(i);
//			switch (index) {
//			case R.styleable.custom_border_left:
//				isLeft = a.getBoolean(index, false);
//				break;
//			case R.styleable.custom_border_top:
//				isTop = a.getBoolean(index, false);
//				break;
//			case R.styleable.custom_border_right:
//				isRight = a.getBoolean(index, false);
//				break;
//			case R.styleable.custom_border_bottom:
//				isBottom = a.getBoolean(index, false);
//				break;
//			case R.styleable.custom_border_color:
//				break;
//			}
//		}
	}
	public void initCustomView(){  
        p = new Paint();  
        p.setAntiAlias(true);  
    } 

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();  
        //  将边框设为黑色  
        paint.setColor(getResources().getColor(R.color.white));  
        //  画TextView的4个边  
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        
	}
}
