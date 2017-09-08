package com.superschool.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by XIAOHAO on 2017/8/2.
 */

public class CircleImageView extends ImageView {
   private Paint mPaintBitmap=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix=new Matrix();

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap=getBitmap(getDrawable());
        if(bitmap!=null){
            int viewWidth=getWidth();
            int viewHeight=getHeight();
            int viewMinSize=Math.min(viewWidth,viewHeight);
            float dstWidth=viewMinSize;
            float dstHeight=viewHeight;
            if(mShader==null||bitmap.equals(mRawBitmap)){
                mRawBitmap=bitmap;
                mShader=new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);


            }
            if(mShader!=null){
                mMatrix.setScale(dstWidth/bitmap.getWidth(),dstHeight/bitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);
            }
            mPaintBitmap.setShader(mShader);
            float radius=viewMinSize/2.0f;
            canvas.drawCircle(radius,radius,radius,mPaintBitmap);

        }else {
            super.onDraw(canvas);
        }




    }
    private Bitmap getBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return (Bitmap) ((BitmapDrawable) drawable).getBitmap();
        }else if(drawable instanceof ColorDrawable){
            Rect rect=drawable.getBounds();
            int width=rect.right-rect.left;
            int height=rect.bottom-rect.top;
            int color=((ColorDrawable) drawable).getColor();
            Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color),Color.red(color),Color.green(color),Color.blue(color));
             return bitmap;

        }

        return null;
    }
}
