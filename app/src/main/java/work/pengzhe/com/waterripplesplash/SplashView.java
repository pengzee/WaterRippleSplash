package work.pengzhe.com.waterripplesplash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created on 2017/6/15 10:49
 *
 * @author PengZee
 */

public class SplashView extends View {

    private int centerX;
    private int centerY;

    private float roundCircleRadius;
    private float smallCircleRadius;
    private float holeCircleRadius;
    private float mCurrentRotationAngle;

    private int animDuration;
    private float diagonal;

    private int[] circleColors;

    private Paint backgroundPaint;
    private Paint animPaint;

    private Status mStatus;
    private int backgroudColor = Color.WHITE;

    public SplashView(Context context) {
        super(context);
        init();
    }

    private void init() {
        circleColors = getResources().getIntArray(R.array.colors_array);
        roundCircleRadius = 90;
        smallCircleRadius = 18;
        animDuration = 1200;
        backgroundPaint = new Paint();
        animPaint = new Paint();
        animPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2;
        centerY = h / 2;
        diagonal = (float) Math.sqrt(centerX * centerX + centerY * centerY);
    }

    public void disppear() {
        ((RotatationStatus) mStatus).cancel();
        mStatus = new DisppearStatus();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mStatus) {
            mStatus = new RotatationStatus();
        }
        mStatus.drawCanvas(canvas);
    }

    public abstract class Status {
        void drawCanvas(Canvas canvas) {
        }
    }

    public class RotatationStatus extends Status {

        ValueAnimator valueAnimator;

        public RotatationStatus() {
            valueAnimator = ValueAnimator.ofFloat((float) (2 * Math.PI), 0);//0-360度旋转
            valueAnimator.setDuration(animDuration);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            valueAnimator.setRepeatCount(ValueAnimator.RESTART);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationAngle = (float) animation.getAnimatedValue(); //获取当前大圆的旋转角度
                    invalidate();
                }
            });
            valueAnimator.start();
        }

        @Override
        void drawCanvas(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }

        void cancel() {
            valueAnimator.cancel();
        }

    }

    private void drawCircles(Canvas canvas) {
        float rotationAngle = (float) (2 * Math.PI / circleColors.length); //每个小圆之间的间隔角度
        for (int i = 0; i < circleColors.length; i++) {
            double angle = rotationAngle * i + mCurrentRotationAngle; //每个小圆i*间隔角度 + 旋转的角度 = 当前小圆的真是角度
            float cx = (float) (roundCircleRadius * Math.sin(angle) + centerX);
            float cy = (float) (roundCircleRadius * Math.cos(angle) + centerY);
            animPaint.setColor(circleColors[i]);
//            Log.i("pengzhe", "roundCircleRadius * Math.sin(angle): " + roundCircleRadius * Math.sin(angle));
            canvas.drawCircle(cx, cy, smallCircleRadius, animPaint);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(backgroudColor);
    }

    public class DisppearStatus extends Status {
        public DisppearStatus() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, roundCircleRadius);//大圆的半径到0
            valueAnimator.setDuration(animDuration);
//            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            valueAnimator.setRepeatCount(ValueAnimator.RESTART);
            valueAnimator.setInterpolator(new OvershootInterpolator(10)); //弹力伸缩
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    roundCircleRadius = (float) animation.getAnimatedValue(); //获取当前大圆的旋转角度
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mStatus = new ExpandStatus();
                }
            });
            valueAnimator.reverse();
        }

        @Override
        void drawCanvas(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }

    public class ExpandStatus extends Status {
        public ExpandStatus() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, diagonal);
            valueAnimator.setDuration(animDuration);
//            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            valueAnimator.setRepeatCount(ValueAnimator.RESTART);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holeCircleRadius = (float) animation.getAnimatedValue(); //获取空心圆的半径
                    invalidate();
                }
            });
            valueAnimator.start();
        }

        @Override
        void drawCanvas(Canvas canvas) {
//            animPaint.setColor(Color.WHITE);
//            //画笔的宽度 = 对角线/2 - 空心圆的半径
//            float strokeWidth = diagonal - holeCircleRadius;
//            animPaint.setStrokeWidth(strokeWidth);
//            animPaint.setStyle(Paint.Style.STROKE);
//            //画圆的半径 = 空心圆的半径 + 画笔的宽度/2
//            float radius = holeCircleRadius + strokeWidth / 2;
//            Log.i("pengzhe", "strokeWidth: " + strokeWidth);
//            Log.i("pengzhe", "radius: " + radius);
//            canvas.drawCircle(centerX, centerY, radius, animPaint);

            // paint画笔style为Stroke时circle的半径分析 http://blog.csdn.net/u013597998/article/details/50998164
            // 在canvas.drawCircle(200,900,200,mPaint);时设置圆的半径为200，而实际内部的圆的半径为，200 - StrokeWidth/2。
            // 也就是外环的宽度的一半加上内部圆半径，总和为200.

            animPaint.setColor(Color.WHITE);
            animPaint.setStrokeWidth(2 * (diagonal - holeCircleRadius));
            animPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, diagonal, animPaint);

            Log.i("pengzhe", "diagonal: " + diagonal);
            Log.i("pengzhe", "holeCircleRadius: " + holeCircleRadius);


        }
    }


}
