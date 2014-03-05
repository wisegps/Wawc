package com.wise.customView;

import java.util.ArrayList;
import com.wise.data.EnergyItem;
import com.wise.extend.OnViewTouchListener;
import com.wise.pubclas.Constant;
import com.wise.wawc.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
/**
 * 我的车况油耗曲线
 * @author honesty
 */
public class EnergyCurveView extends View implements OnTouchListener{
	private static final String TAG = "EnergyCurveView";
	Context context;
	//private static final float SPACING_SCALE = 40f; // 缩放间距 用于背景图过大
	/**
	 * 距离顶部的高度
	 */
	private static final float SPACING_HEIGHT = 20;
	/**
	 * y轴最高刻度 距离 y轴绘制的 间距
	 */
	private static final float WEIGHT = 40;
	/**
	 * 距离左边的间距
	 */
	private static int SPACING = 35;
	private Bitmap mTrendLine; // 点击显示的竖线
	private Bitmap mLastPoint; // 曲线图最后一个绘制灰色圆点
	private Bitmap mMovePoint; // 曲线图移动中绘制的圆点
	/**
	 * 实际画布宽度
	 */
	private float mGradientWidth;
	private float mGradientHeight = 450; // 渐变条的高度
	private DisplayMetrics dm; // 手机屏幕的宽高
	private ArrayList<PointF> points; // 有消耗的电量时间点
	private ArrayList<EnergyItem> energyItems;
	private float spacingOfX; // X间距
	private float spacingOfY; // Y间距,每一度的间距
	private EnergyItem maxEnergy; //y坐标最大的单元
	private float moveXOfFirst; // 单个按下的X坐标
	OnViewTouchListener onViewTouchListener;
	//长按事件Runnable
	private Runnable mLongPressRunnable;
	//移动的阀值，炒作这个距离算移动
	private static int TOUCH_SLOP;
	//是否长按
	boolean isLongPress = false;
	boolean isUp = false;
	int fontSize;

	public EnergyCurveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		fontSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
		SPACING = fontSize * 2;
		setOnTouchListener(this);
		TOUCH_SLOP = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		mLongPressRunnable = new Runnable() {            
            @Override
            public void run() {
                isLongPress = true;
                invalidate();
            }
        };
	}
	int index = 0;
	float x = 0;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			moveXOfFirst = event.getX(0);
			x = moveXOfFirst;
			isLongPress = false;
			isUp = false;
			postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			moveXOfFirst = event.getX(0);
			if(Math.abs(moveXOfFirst-x) > TOUCH_SLOP) {  
                //移动超过阈值，则表示移动了  
                removeCallbacks(mLongPressRunnable);  
            }
			if(isLongPress){
			    getParent().requestDisallowInterceptTouchEvent(true);
			    invalidate();
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
		    isUp = true;
		    getParent().requestDisallowInterceptTouchEvent(false);
		    removeCallbacks(mLongPressRunnable);
			moveXOfFirst = event.getX(0);
			float x_Distance = 0;
			boolean isLast = true;
			for(int i = 0 ; i < points.size(); i++){
				PointF textPoint = points.get(i);
				float x = textPoint.x - moveXOfFirst;
				float x_abc = Math.abs(x);
				if(i == 0){
					//
				}else{
					if(x_abc > x_Distance){
						moveXOfFirst = points.get(i-1).x;
						index = i - 1;
						isLast = false;
						break;
					}
				}
				x_Distance = x_abc;
			}
			if(isLast){
			    if(points.size() != 0){
			        moveXOfFirst = points.get(points.size()-1).x;
	                index = points.size()-1;
			    }				
			}
			//TODO 计算松手后停靠那个点
			invalidate();
		}
		if(event.getAction() == MotionEvent.ACTION_CANCEL){
		    isLongPress = false;
		    getParent().requestDisallowInterceptTouchEvent(false);
            removeCallbacks(mLongPressRunnable);
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		Paint paint = new Paint();
		// 初始化绘制
		initDraw(canvas, paint);
		// 点击屏幕时 进行的操作, 单点，多点
		if(isLongPress && (energyItems.size() > 0)){
	        SinglePointTouch(canvas, paint);
	        if(!isUp){
	            // 绘制 移动的 点
	            onPointMove(canvas, paint, moveX);
	        }
		}
	}

	/**
	 * 初始化绘制
	 * @param canvas
	 * @param paint
	 */
	private void initDraw(Canvas canvas, Paint paint) {
		paint.setColor(0xff999999);
		paint.setAntiAlias(true);
		//TODO 绘制
		float dottedSpacing = mGradientHeight/5;//垂直大间隔,实线距离
		float smallDotted = dottedSpacing / 5;//里面虚线距离
		float value = maxEnergy.value/5;
		
		canvas.drawLine(SPACING, SPACING_HEIGHT, SPACING, mGradientHeight + SPACING_HEIGHT, paint);//y轴
		canvas.drawLine(SPACING, mGradientHeight + SPACING_HEIGHT, SPACING + mGradientWidth, mGradientHeight + SPACING_HEIGHT, paint);//x轴
		float xSpacing = mGradientWidth/6;
        paint.setColor(getResources().getColor(R.color.common));
        paint.setTextSize(fontSize);
		for(int i = 0 ; i <= 6 ; i++){
		    String Date = (i * 5 + 1) + "" ;
		    canvas.drawText(Date, SPACING + i * xSpacing - fontSize/2,mGradientHeight + SPACING_HEIGHT + fontSize,paint);
        }
		/* 水平线和文字 */
		for (int i = 0; i <= 5; i++) {
			paint.setStrokeWidth(3);
			canvas.drawText(""+(int)(value * i), SPACING - fontSize,mGradientHeight + SPACING_HEIGHT - dottedSpacing * i + fontSize/2,paint);
			//canvas.drawLine(SPACING, mGradientHeight + SPACING_HEIGHT - dottedSpacing * i, mGradientWidth, mGradientHeight + SPACING_HEIGHT - dottedSpacing * i, paint);
			paint.setStrokeWidth(1);
			paint.setColor(0xff999999);
			if(i != 0){
				for (int j = 1; j <= 4; j++) {
					//canvas.drawLine(SPACING, mGradientHeight + SPACING_HEIGHT - dottedSpacing * i + smallDotted * j,mGradientWidth, mGradientHeight + SPACING_HEIGHT - dottedSpacing * i + smallDotted * j,paint);
				}
			}			
		}
		
		/* 绘制曲线 覆盖 剪切后的锯齿 */
		for (int i = 0; i < points.size(); i++) {
			paint.setStrokeWidth(3);
			PointF startPoint = points.get(i);
			if (i + 1 == points.size()) {// 绘制 最后一个圆点 为剪切的图片
				//canvas.drawBitmap(mLastPoint,startPoint.x - mLastPoint.getWidth() / 2, startPoint.y - mLastPoint.getHeight() / 2, paint);
			    canvas.drawCircle(startPoint.x, startPoint.y, 3, paint);
			    break;
			}
            PointF endPoint = points.get(i + 1);
			if(i%2 == 0){
                //画阴影
                paint.setColor(getResources().getColor(R.color.gray_light_ten));// 设置灰色  
                paint.setStyle(Paint.Style.FILL);//设置填满  
                Path path = new Path();
                path.moveTo(startPoint.x, mGradientHeight + SPACING_HEIGHT);// 此点为多边形的起点  
                path.lineTo(startPoint.x, startPoint.y);  
                path.lineTo(endPoint.x, endPoint.y);  
                path.lineTo(endPoint.x, mGradientHeight + SPACING_HEIGHT);  
                path.close(); // 使这些点构成封闭的多边形  
                canvas.drawPath(path, paint); 
            }
			// 绘制曲线，并且覆盖剪切后的锯齿
			paint.setColor(getResources().getColor(R.color.common_blue));
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,paint);
			canvas.drawCircle(startPoint.x, startPoint.y, 3, paint);
		}
	}
	float moveX;
	/**
	 * 单点触控操作
	 * @param canvas
	 * @param paint
	 */
	private void SinglePointTouch(Canvas canvas, Paint paint) {
		//顶部的度数框
		if (moveXOfFirst < points.get(0).x) {
			moveXOfFirst = points.get(0).x;
		}
		if (moveXOfFirst > points.get(points.size() - 1).x) {
			moveXOfFirst = points.get(points.size() - 1).x;
		}
		moveX = moveXOfFirst;
		// 绘制度数框 背景后的 横线
		//canvas.drawBitmap(mTrendLine, SPACING, mGradientHeight + SPACING_HEIGHT - mTrendLine.getHeight() + 5, paint);
		
		// 绘制 变动的 能耗为多少
		float moveY = getMoveY(moveXOfFirst);
		float energyHeight = (float) (mGradientHeight + SPACING_HEIGHT) - moveY;

		String energyText = String.valueOf(energyHeight / spacingOfY);
		// 为了避免误差 如果单点 手指在X轴 在预定的 X点上 那么直接将显示读书设置为 服务器传回的数据
		EnergyItem energy = isInPoint(moveXOfFirst);
		if (energy != null) {
			energyText = String.valueOf(energy.value);
		}
		int indexOf = energyText.indexOf(".");
		String substring = energyText.substring(0, indexOf + 2);
		
		if(onViewTouchListener != null){
		    onViewTouchListener.OnViewTouch(substring,index);
		}
	}

	/**
	 * 点击绘制的 黄色的 移动圆点
	 */
	private void onPointMove(Canvas canvas, Paint paint, float moveX) {
		// 点住滑动 绘制的黄色线
		if (moveX < SPACING) {
			moveX = SPACING - mTrendLine.getWidth() / 2;
		}
		if (moveX > points.get(points.size() - 1).x) {
			moveX = points.get(points.size() - 1).x - mTrendLine.getWidth() / 2;
		}
		canvas.drawBitmap(mTrendLine,
				moveX - mTrendLine.getWidth() / 2, mGradientHeight
						+ SPACING_HEIGHT - mTrendLine.getHeight() + 5,
				paint);
		// 绘制移动中的点
		canvas.drawBitmap(mMovePoint,
				moveX - mMovePoint.getWidth() / 2, getMoveY(moveX)
						- mMovePoint.getWidth() / 2, paint);
	}
	/**
	 * 判断 目前手指在屏幕中X的点 是否在 结合点上
	 * @param moveX 手指移动中的点
	 * @return
	 */
	private EnergyItem isInPoint(float moveX) {
		EnergyItem energy = null;
		for (int i = 0; i < points.size(); i++) {
			if (moveX == points.get(i).x) {
				energy = energyItems.get(i);
				break;
			}
		}
		return energy;
	}
	/**
	 * 获取移动线条时 Y的值，
	 */
	private float getMoveY(float x) {
		float y = mGradientHeight + SPACING_HEIGHT;
		PointF first = null;
		PointF second = null;
		for (int i = 0; i < points.size() - 1; i++) {
			PointF point_1 = points.get(i);
			PointF point_2 = points.get(i + 1);
			if (point_1.x <= x && point_2.x >= x) {
				first = point_1;
				second = point_2;
				break;
			}
		}
		// 勾股定理 ：ｙ＝（ｙ２－ｙ１）／（ｘ２－ｘ１）＊（ｘ－ｘ１）＋ｙ１
		if (first != null || second != null)
			y = Math.abs((second.y - first.y) / (second.x - first.x)
					* (x - first.x) + first.y);
		return y;
	}
	/**
	 * 初始化 加载 view 的资源图片
	 */
	public void setImages() {
		// 背景渐变色大图
		int windowW = (int) (dm.widthPixels - SPACING * 2);
		mGradientWidth = windowW;
		mGradientHeight = (float) (windowW*0.7);
		Log.d(TAG, "dm.widthPixels = " + dm.widthPixels + ",mGradientWidth = " + mGradientWidth);
		// 移动的黄色的线条
		mTrendLine = BitmapFactory.decodeResource(getResources(),R.drawable.energy_trendline);
		int height = mTrendLine.getHeight();
		float trendLineScale = (float) mGradientHeight / height;
		mTrendLine = scaleBmp(mTrendLine, 1.0f, trendLineScale); // 缩放图层
		// 最后一个灰色的点
		mLastPoint = BitmapFactory.decodeResource(getResources(), R.drawable.energy_trendpoint_last);
		mMovePoint = BitmapFactory.decodeResource(getResources(), R.drawable.energy_trendpoint_move);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 图片缩放
	 * @param bitmap 需要缩放的图片
	 * @param scaleX X缩放比例
	 * @param scaleY Y缩放比例
	 * @return
	 */
	public Bitmap scaleBmp(Bitmap bitmap, float scaleX, float scaleY) {
		Bitmap scaleBmp = null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY);
		scaleBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,false);
		bitmap.recycle();
		return scaleBmp;
	}
	public void setWindowsWH(DisplayMetrics dm) {
		this.dm = dm;
		setImages();
	}

	
	/**
	 * 通过数据 预先存入需要绘制的 连线点
	 * @param powers
	 * @param date
	 */
	public void initPoints(ArrayList<EnergyItem> energys) {
		this.energyItems = energys;
		getSpacingOfXY(energys);
		points = new ArrayList<PointF>();
		for (int i = 0; i < energys.size(); i++) {
			float f = energys.get(i).value;
			float y = ((mGradientHeight + SPACING_HEIGHT) - f * spacingOfY);
			
			float x = (i * spacingOfX + SPACING + mLastPoint.getWidth());
			PointF point = new PointF(x, y);
			points.add(point);
		}
	}

	/**
	 * 获取X的间距 以及 Y的间距
	 * @param powers
	 * @param date
	 */
	private void getSpacingOfXY(ArrayList<EnergyItem> energys) {
		maxEnergy = findMaxPowers(energys);
		spacingOfX = mGradientWidth/ (energys.size() -1);
		spacingOfY = (mGradientHeight - WEIGHT) / maxEnergy.value;
		Log.d(TAG, "spacingOfY = " + spacingOfY + " , maxEnergy.value = " + maxEnergy.value);
	}

	/**
	 * 找到 数据集合中 最高能量 对应的脚标
	 * @param powers
	 * @return
	 */
	private static EnergyItem findMaxPowers(ArrayList<EnergyItem> energys) {
		EnergyItem energy = new EnergyItem();
		energy.value = 0;
		for (int i = 0; i < energys.size(); i++) {
			if (energys.get(i).value > energy.value) {
				energy = energys.get(i);
			}
		}
		return energy;
	}
	public void setOnViewTouchListener(OnViewTouchListener onViewTouchListener){
		this.onViewTouchListener = onViewTouchListener;
	}
	public void RefreshView(){
		invalidate();
	}
}