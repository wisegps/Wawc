package com.wise.extend;

import com.wise.wawc.ImageActivity;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * android.widget.Gallery
 * @author Mr.Wang
 */
public class MyGallery extends Gallery {
	private MyImageView imageView;
	
	private Camera mCamera = new Camera();     // 是用来做类3D效果处理,比如z轴方向上的平移,绕y轴的旋转等
    private int mMaxRotationAngle = 60;     // 是图片绕y轴最大旋转角度,也就是屏幕最边上那两张图片的旋转角度
    private int mMaxZoom = -380;             // 是图片在z轴平移的距离,视觉上看起来就是放大缩小的效果.
    private int mCoveflowCenter;
    private boolean mAlphaMode = true;
    private boolean mCircleMode = false;
	public MyGallery(Context context) {
		super(context);

	}
	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = MyGallery.this.getSelectedView();
				if (view instanceof MyImageView) {
					imageView = (MyImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;
								imageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));
							}
						}
					}
				}
				return false;
			}

		});
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = MyGallery.this.getSelectedView();
		if (view instanceof MyImageView) {
			imageView = (MyImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			float left, right;
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			if ((int) width <= ImageActivity.screenWidth && (int) height <= ImageActivity.screenHeight)// ���ͼƬ��ǰ��С<��Ļ��С��ֱ�Ӵ��?���¼�
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);
				
				if (distanceX > 0)
				{
				if (r.left > 0) {
				super.onScroll(e1, e2, distanceX, distanceY);
				} else if (right <= ImageActivity.screenWidth) {
				super.onScroll(e1, e2, distanceX, distanceY);
				} else {
				imageView.postTranslate(-distanceX, -distanceY);
				}
				} else if (distanceX < 0)
				{
				if (r.right == 0) {
				super.onScroll(e1, e2, distanceX, distanceY);
				} else {
				imageView.postTranslate(-distanceX, -distanceY);
				}
				}
			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		super.onFling(e1, e2, velocityX/2, velocityY);
		return true;
		}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			View view = MyGallery.this.getSelectedView();
			if (view instanceof MyImageView) {
				imageView = (MyImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= ImageActivity.screenWidth && (int) height <= ImageActivity.screenHeight)// ���ͼƬ��ǰ��С<��Ļ��С���жϱ߽�
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (top > 0) {
					imageView.postTranslateDur(-top, 200f);
				}
				Log.i("lyc", "bottom:" + bottom);
				if (bottom < ImageActivity.screenHeight) {
					imageView.postTranslateDur(ImageActivity.screenHeight - bottom, 200f);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
