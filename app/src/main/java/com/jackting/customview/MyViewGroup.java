package com.jackting.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MyViewGroup extends ViewGroup{

    //缩进尺寸
    private static final int OFFSET=100;

    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1. 计算自身
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 2. 为每个子View计算测量的限制信息 mode size
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

//        3. 把上一步的限制信息，传递给子View，测量子View
        //子View的个数
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View child=getChildAt(i);
            ViewGroup.LayoutParams lp=child.getLayoutParams();
            int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,0,lp.width);
            int childHeightSpec = getChildMeasureSpec(heightMeasureSpec,0,lp.height);

            child.measure(childWidthSpec,childHeightSpec);
        }
        int width=0;
        int height=0;
        // 4. 获取子View测量尺寸
        // 5。ViewGroup根据自身的情况，计算自己的尺寸
        switch (widthMode){
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                for(int i=0;i<childCount;i++){
                    View child=getChildAt(i);
                    int widthAddOffset=i*OFFSET+child.getMeasuredWidth();
                    width=Math.max(width,widthAddOffset);
                }
                break;
            default:
                break;
        }

        switch (heightMode){
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                for(int i=0;i<childCount;i++){
                    View child=getChildAt(i);
                    height+=child.getMeasuredHeight();
                }
                break;
            default:
                break;
        }
        // 6. 保存自身尺寸
        setMeasuredDimension(width,height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        1 遍历子View

//        2 确定自己的规则
//        3 子View的测量尺寸
//        4 确定四个值：left,top,right,bottom
//        5 child.layout

        int left =0;
        int top=0;
        int right=0;
        int bottom=0;


        int childCount=getChildCount();
        for(int i=0;i<childCount;i++){
            View child=getChildAt(i);
            left=i*OFFSET;
            right=left+child.getMeasuredWidth();
            bottom=top+child.getMeasuredHeight();



            child.layout(left,top,right,bottom);
            top+=child.getMeasuredHeight();
        }
    }
}
