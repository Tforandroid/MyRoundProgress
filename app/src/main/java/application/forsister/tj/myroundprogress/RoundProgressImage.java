package application.forsister.tj.myroundprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by tj on 2017/10/30.
 */

public class RoundProgressImage extends View {

    private Paint mPaint;
    private TypedArray mTypedArray;
    private int ringColor;
    private int ringProgressColor;
    private int ringWidth;
    private int maxProgress;
    private int startAngle;
    private int maxAngle;
    private int startColor;
    private int endColor;
    private RectF mWheelRectangle;
    private int rightAndBottom;
    BarAnimation anim;//动画
    private int TIME = 1000;//时间
    private int progressStrokeWidth;
    private Paint mProgressPaint;
    private int currentProgress;
    private int endAngle;
    private float endAngleper;

    public RoundProgressImage(Context context) {
        this(context,null);
    }

    public RoundProgressImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //自定义的一些属性,如果用户有设置值就使用，没有就是用默认值
        mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressImage);

        //圆圈的矩形范围
        mWheelRectangle = new RectF();
        //圆环的颜色
        ringColor = mTypedArray.getColor(R.styleable.RoundProgressImage_ringColor, getResources().getColor(R.color.light_orange));
        //圆环进度条的颜色(如果固定颜色使用，需要将渐变色代码修改）
        ringProgressColor = mTypedArray.getColor(R.styleable.RoundProgressImage_ringProgressColor, getResources().getColor(R.color.orange));
        //默认圆环宽度
        ringWidth = (int)mTypedArray.getDimension(R.styleable.RoundProgressImage_ringWidth, 10);
        //进度圆环的宽度
        progressStrokeWidth = (int)mTypedArray.getDimension(R.styleable.RoundProgressImage_ringWidth, 8);
        //最大进度
        maxProgress =mTypedArray.getInt(R.styleable.RoundProgressImage_maxProgress, 100);
        //当前进度
      currentProgress =mTypedArray.getInt(R.styleable.RoundProgressImage_currentProgress, 30);
        //开始的角度
        startAngle =mTypedArray.getInt(R.styleable.RoundProgressImage_startAngle, -180);
        //最大角度
        maxAngle =mTypedArray.getInt(R.styleable.RoundProgressImage_maxAngle, 180);
        //渐变色
        startColor =mTypedArray.getInt(R.styleable.RoundProgressImage_startColor,  getResources().getColor(R.color.light_orange));
        endColor =mTypedArray.getInt(R.styleable.RoundProgressImage_endColor,  getResources().getColor(R.color.orange));
        //默认圆
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿画笔
        mPaint.setColor(ringColor);
        mPaint.setStyle(Paint.Style.STROKE);//空心
        mPaint.setStrokeWidth(ringWidth);//默认圆环宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND);//开启显示边缘为圆形
        //进度圆环的画笔
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(startColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);//空心
        mProgressPaint.setStrokeWidth(progressStrokeWidth);//进度画笔
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);//开启显示边缘为圆形

        //释放回收资源
        mTypedArray.recycle();
        anim=new BarAnimation();
        anim.setDuration(TIME);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rightAndBottom = min - ringWidth;

        mWheelRectangle.set(ringWidth ,ringWidth,
                rightAndBottom, rightAndBottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取圆心坐标
        int centerXY = getWidth() / 2;

        canvas.drawArc(mWheelRectangle, -180, 180, false, mPaint);//固定的圆弧

        //颜色渐变效果
        SweepGradient sweepGradient = new SweepGradient(centerXY, centerXY, startColor, endColor);
        mProgressPaint.setShader(sweepGradient);
        //进度圆弧
        endAngle = maxAngle*currentProgress/maxProgress;
        canvas.drawArc(mWheelRectangle, startAngle, endAngleper, false, mProgressPaint);//进度的圆弧

    }
    public class BarAnimation extends Animation {
        /**
         * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
         * 1 will collapse view and set to gone
         */
        public BarAnimation() {

        }

        //        * 动画类利用了applyTransformation参数中的interpolatedTime参数(从0到1)的变化特点，
//                * 实现了该View的某个属性随时间改变而改变。原理是在每次系统调用animation的applyTransformation()方法时，
//                * 改变endAngleper
//                * 然后调用postInvalidate()不停的绘制view。
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //mSweepAnglePer，mCount这两个属性只是动画过程中要用到的临时属性，
            //mText和mSweepAngle才是动画结束之后表示扇形弧度和中间数值的真实值。
            if (interpolatedTime < 1.0f) {
                endAngleper = interpolatedTime * endAngle;
            } else {
                endAngleper = endAngle;
            }
            postInvalidate();
        }
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        this.startAnimation(anim);
    }
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * 设置最大进度
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }


    public int getRingColor() {
        return ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }



    public int getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(int maxAngle) {
        this.maxAngle = maxAngle;
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public int getRightAndBottom() {
        return rightAndBottom;
    }

    public void setRightAndBottom(int rightAndBottom) {
        this.rightAndBottom = rightAndBottom;
    }

    public BarAnimation getAnim() {
        return anim;
    }

    public void setAnim(BarAnimation anim) {
        this.anim = anim;
    }

    public int getTIME() {
        return TIME;
    }

    public void setTIME(int TIME) {
        this.TIME = TIME;
    }

    public int getProgressStrokeWidth() {
        return progressStrokeWidth;
    }

    public void setProgressStrokeWidth(int progressStrokeWidth) {
        this.progressStrokeWidth = progressStrokeWidth;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }
}
