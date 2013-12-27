package com.wise.extend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MyImageView extends ImageView{
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    /**λͼ����*/
    private Bitmap bitmap = null;
    /** ��Ļ�ķֱ���*/
    private DisplayMetrics dm;

    /** ��С���ű���*/
    float minScaleR = 1.0f;
    
    /** ������ű���*/
    static final float MAX_SCALE = 15f;

    /** ��ʼ״̬*/
    static final int NONE = 0;
    /** �϶�*/
    static final int DRAG = 1;
    /** ����*/
    static final int ZOOM = 2;
    
    /** ��ǰģʽ*/
    int mode = NONE;

    /** �洢float���͵�x��yֵ����������µ�����X��Y*/
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    
    public MyImageView(Context context) {
		super(context);
		setupView();
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}
	
	
	public void setupView(){
		Context context = getContext();
		//��ȡ��Ļ�ֱ���,��Ҫ��ݷֱ�����ʹ��ͼƬ����
		dm = context.getResources().getDisplayMetrics();
		
		//���MyImageView����ȡbitmap����
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd != null){
			bitmap = bd.getBitmap();
		}
		
		//����ScaleTypeΪScaleType.MATRIX����һ������Ҫ
		this.setScaleType(ScaleType.MATRIX);
		this.setImageBitmap(bitmap);
		this.setImageMatrix(matrix);
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 switch (event.getAction() & MotionEvent.ACTION_MASK) {
			        // ���㰴��
			        case MotionEvent.ACTION_DOWN:
			            savedMatrix.set(matrix);
			            prev.set(event.getX(), event.getY());
			            mode = DRAG;
			            break;
			        // ���㰴��
			        case MotionEvent.ACTION_POINTER_DOWN:
			            dist = spacing(event);
			            // �����������������10�����ж�Ϊ���ģʽ
			            if (spacing(event) > 10f) {
			                savedMatrix.set(matrix);
			                midPoint(mid, event);
			                mode = ZOOM;
			            }
			            break;
			        case MotionEvent.ACTION_UP:{
			        	break;
			        }
			        case MotionEvent.ACTION_POINTER_UP:
			            mode = NONE;
			            //savedMatrix.set(matrix);
			            break;
			        case MotionEvent.ACTION_MOVE:
			            if (mode == DRAG) {
			                matrix.set(savedMatrix);
			                matrix.postTranslate(event.getX() - prev.x, event.getY()
			                        - prev.y);
			            } else if (mode == ZOOM) {
			                float newDist = spacing(event);
			                if (newDist > 10f) {
			                    matrix.set(savedMatrix);
			                    float tScale = newDist / dist;
			                    matrix.postScale(tScale, tScale, mid.x, mid.y);
			                }
			            }
			            break;
			        }
				    MyImageView.this.setImageMatrix(matrix);
			        CheckView();
			        return true;
			}
		});
	}
    
    /**
     * ���������С���ű����Զ�����
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
            	//Log.d("", "��ǰ���ż���:"+p[0]+",��С���ż���:"+minScaleR);
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
            	//Log.d("", "��ǰ���ż���:"+p[0]+",������ż���:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
    }
    
    
    /**
     * ����ľ���
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * ������е�
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
