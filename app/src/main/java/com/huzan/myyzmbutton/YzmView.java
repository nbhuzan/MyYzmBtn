package com.huzan.myyzmbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by huzan on 2016/11/18 14:23.
 */

public class YzmView extends Button {

    private String mText="";
    private String oldText="";
    private int timeColor;
    private int time;
    private View v ;
    /**
     * 画笔,文本绘制范围
     */
    private Rect rect;
    private Paint paint;

    public YzmView(Context context) {
        this(context, null);
    }

    public YzmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.YzmView);
        mText = ta.getString(R.styleable.YzmView_textTime);
        oldText = mText;
        time =ta.getInteger(R.styleable.YzmView_time,60);
        timeColor =ta.getColor(R.styleable.YzmView_timeColor,Color.BLACK);
        ta.recycle();
        v = this;

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                v.setEnabled(false);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        int t = 0;
                        while (t<=time){

                            mText = time-t+"s后重试";
                            handler.sendEmptyMessage(0);
                            t+=1;
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(t>time){
                            handler.sendEmptyMessage(1);
                        }
                    }
                }.start();

            }
        });


        rect = new Rect();
        paint = new Paint();
        this.setPadding(100,50,100,50);


    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    requestLayout();
                    postInvalidate();
                    break;
                case 1:
                    mText=oldText;
                    v.setEnabled(true);
                    requestLayout();
                    postInvalidate();
                    break;
            }
        }
    };


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paint.setTextSize(this.getTextSize());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.getTextBounds(mText, 0, mText.length(), rect);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heighSize = MeasureSpec.getSize(heightMeasureSpec);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int heigh = 0;
        if(widthMode==MeasureSpec.EXACTLY){
            width = widthSize;
        }else{

            int textWidth =  rect.width();
            width = getPaddingLeft()+getPaddingRight()+textWidth;
        }

        if(heighMode==MeasureSpec.EXACTLY){
            heigh = heighSize;
        }else{
            int textWidth =  rect.height();
            heigh = getPaddingTop()+getPaddingBottom()+textWidth;
        }


        setMeasuredDimension(width,heigh);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(timeColor);
        float width = paint.measureText(mText);
        System.out.println(mText);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        float heigh = getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2;

        canvas.drawText(mText,getWidth()/2-width/2,heigh,paint);


    }
}
