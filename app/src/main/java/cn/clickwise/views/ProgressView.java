package cn.clickwise.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import cn.clickwise.R;

/**
 * 自定义的进度条View组件
 * <p>
 * 支持自定义文本，当前进度值，最大进度值，半径大小，进度条宽度，文本大小，文本颜色，进度条颜色，内圆颜色，外圆颜色等
 */
public class ProgressView extends View {

    private String mText = "";
    private float mTextSize = 10f;
    private float mRadius = 10f;
    private float mStrokeWidth = 0f;
    private int mTextColor = Color.WHITE;
    private int mInnerColor = Color.GREEN;
    private int mOuterColor = Color.BLUE;
    private int mProgressColor = Color.WHITE;
    private int mCurrentProgress = 0;
    private int mMaxProgress = 100;
    private float scanfDegrees = 0;
    public Paint mInnerPaint;
    private Paint mOuterPaint;
    private Paint mProgressPaint;
    private TextPaint mTextPaint;
    private boolean isHideRadar;
    private Bitmap mRadarBitmap;
    private Paint mScanfPaint;
    public int scanfColor;

    public float getScanfDegrees() {
        return scanfDegrees;
    }

    public void setScanfDegrees(float scanfDegrees) {
        this.scanfDegrees = scanfDegrees;
    }

    public boolean isHideRadar() {
        return isHideRadar;
    }

    public void setHideRadar(boolean hideRadar) {
        isHideRadar = hideRadar;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public Paint getInnerPaint() {
        return mInnerPaint;
    }

    public void setInnerPaint(Paint innerPaint) {
        mInnerPaint = innerPaint;
    }

    public Paint getOuterPaint() {
        return mOuterPaint;
    }

    public void setOuterPaint(Paint outerPaint) {
        mOuterPaint = outerPaint;
    }

    public Paint getProgressPaint() {
        return mProgressPaint;
    }

    public void setProgressPaint(Paint progressPaint) {
        mProgressPaint = progressPaint;
    }

    public TextPaint getTextPaint() {
        return mTextPaint;
    }

    public void setTextPaint(TextPaint textPaint) {
        mTextPaint = textPaint;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        postInvalidate();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
        postInvalidate();
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        postInvalidate();
    }

    public int getInnerColor() {
        return mInnerColor;
    }

    public void setInnerColor(int innerColor) {
        mInnerColor = innerColor;
        postInvalidate();
    }

    public int getOuterColor() {
        return mOuterColor;
    }

    public void setOuterColor(int outerColor) {
        mOuterColor = outerColor;
        postInvalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        postInvalidate();
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        mCurrentProgress = currentProgress;
        postInvalidate();
    }

    public ProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressView, defStyle, 0);
        mText = a.getString(R.styleable.ProgressView_text);
        mTextColor = a.getColor(R.styleable.ProgressView_textColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.ProgressView_textSize, (int) mTextSize);
        mInnerColor = a.getColor(R.styleable.ProgressView_innerColor, mInnerColor);
        mOuterColor = a.getColor(R.styleable.ProgressView_outerColor, mOuterColor);
        mProgressColor = a.getColor(R.styleable.ProgressView_progressColor, mProgressColor);
        mRadius = a.getDimension(R.styleable.ProgressView_radius, (int) mRadius);
        mStrokeWidth = a.getDimension(R.styleable.ProgressView_strokeWidth, (int) mStrokeWidth);
        mCurrentProgress = a.getInteger(R.styleable.ProgressView_currentProgress, mCurrentProgress);
        mMaxProgress = a.getInteger(R.styleable.ProgressView_maxProgress, mMaxProgress);
        scanfColor=a.getColor(R.styleable.ProgressView_scanfColor,scanfColor);
        a.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setDither(true);
        mInnerPaint.setStyle(Paint.Style.FILL);
        //mInnerPaint.setStrokeWidth(mStrokeWidth);
        mInnerPaint.setColor(mInnerColor);

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(mStrokeWidth);
        mOuterPaint.setColor(mOuterColor);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mStrokeWidth);
        mProgressPaint.setColor(mProgressColor);
        mRadarBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.radar);

        mScanfPaint = new Paint();
        mScanfPaint.setAntiAlias(true);
        mScanfPaint.setColor(scanfColor);
    }

    public void startScanf() {
        isHideRadar = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isHideRadar) {
                    scanfDegrees = (scanfDegrees += 2) >= 360 ? 0 : scanfDegrees;
                    postInvalidate();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (getPaddingLeft() + mRadius * 2 + mStrokeWidth * 2 + getPaddingRight());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (getPaddingTop() + mRadius * 2 + mStrokeWidth * 2 + getPaddingBottom());
        }

        setMeasuredDimension(width, height);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int centerX = contentWidth / 2, centerY = contentHeight / 2;// view中心点位置
        float angle = mCurrentProgress / (mMaxProgress * 1.0f) * 360; // 进度条的圆弧角度
        //基础圆所在的椭圆对象同时决定进度圆环的位置
        RectF rect = new RectF();
        int viewSize = (int) (mRadius * 2 + mStrokeWidth);
        int left = (int) ((centerX - mRadius) - mStrokeWidth / 2);//(int) mStrokeWidth / 2;
        int top = (int) ((centerY - mRadius) - mStrokeWidth / 2);//(int) mStrokeWidth / 2;
        int right = left + viewSize;
        int bottom = top + viewSize;
        rect.set(left, top, right, bottom);
        //画基础圆
        canvas.drawCircle(centerX, centerY, mRadius + mStrokeWidth / 2, mOuterPaint);//stroke 左右两边扩展
        //画中间圆
        canvas.drawCircle(centerX, centerY, mRadius, mInnerPaint);
        //画进度圆环
        canvas.drawArc(rect, -90, angle, false, mProgressPaint);
        //画字
        canvas.drawText(mText, centerX, centerY, mTextPaint);
        //画扫描效果
        canvas.drawArc(rect,-90+scanfDegrees,60,true,mScanfPaint);

       /* Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect, paint);
        canvas.drawCircle(contentWidth / 2, contentHeight / 2, mRadius, paint);
        canvas.drawCircle(contentWidth / 2, contentHeight / 2, mRadius + mStrokeWidth, paint);*/
        if (!isHideRadar) {
            canvas.save();
            canvas.rotate(scanfDegrees,centerX,centerY);
            canvas.restore();
        }
    }
}
