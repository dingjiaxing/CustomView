package com.jackting.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. 自定义属性:声明、设置，解析获取自定义值
 * 2. 测量：onMeasure
 * 3. 布局：在onLayout方法里面根据自己规则来确定children的位置
 * 4. 绘制：onDraw
 * 5. 处理LayoutParams
 * 6. 触摸反馈：滑动事件
 *
 * 滑动实现的三种方式
 * a. 通过View的ScrollBy和ScrollTo方法实现滑动
 * b. 通过动画给View添加位移效果来实现滑动（补间动画）
 * c. 通过改变View的 layoutparams 让View 重新布局，从而实现滑动
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG="FlowLayout";

    private List<View> lineViews; //每一行的子View
    private List<List<View>> views;//所有的行

    private List<Integer> heights;//存放每一行的高度


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    private void init(){
        views = new ArrayList<>();
        lineViews = new ArrayList<>();
        heights = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        //当前行的宽度和高度
        int rowWidth = 0;//宽度是当前行的子View的宽度之和
        int rowHeight = 0; //高度是当前行所有子View中高度的最大值

        //记录整个流式布局
        int flowLayoutWidth = 0;//所有行中宽度的最大值
        int flowLayoutHeight = 0;//所有行高度的累加

        // 初始化参数列表
        init();

        int childCount = this.getChildCount();
        //遍历所有子View,对子View进行测量，分配到具体的行
        for(int i=0;i<childCount;i++){
            View child=getChildAt(i);
            // 测量子VIew，获取到当前View的测量的宽度和高度
            measureChild(child,widthMeasureSpec,heightMeasureSpec);

            int childWidth=child.getMeasuredWidth();
            int childHeight=child.getMeasuredHeight();

            LayoutParams lp= (LayoutParams) child.getLayoutParams();
            //看一下当前行的剩余宽度是否可以容纳 下一个子View，
            // 如果放不下，换行，保存当前行的所有子View，累加行高，当前宽度高度置0
            if(rowWidth+childWidth > widthSize){
                //大于，需要换行
                views.add(lineViews);
                heights.add(rowHeight);
                lineViews = new ArrayList<>();
                flowLayoutWidth = Math.max(flowLayoutWidth,rowWidth);
                flowLayoutHeight += rowHeight;
                rowWidth = 0;
                rowHeight = 0;
            }
            lineViews.add(child);
            rowWidth += childWidth;

            if(lp.height != LayoutParams.MATCH_PARENT){
                rowHeight = Math.max(rowHeight,childHeight);
            }

            //最后一行
            if( i == childCount - 1){
                flowLayoutHeight += rowHeight;
                flowLayoutWidth = Math.max(flowLayoutWidth,rowWidth);
                heights.add(rowHeight);
                views.add(lineViews);
            }
        }
        Log.i(TAG,"flowLayoutWidth:"+flowLayoutWidth);
        Log.i(TAG,"flowLayoutHeight:"+flowLayoutHeight);
        setMeasuredDimension(widthMode==MeasureSpec.EXACTLY?widthSize:flowLayoutWidth,
                heightMode==MeasureSpec.EXACTLY?heightSize:flowLayoutHeight);

        //如果需要兼容子View中 layout_height_height = match_parent的情况
        //需要重新再测量一次
        remeasureChild(widthMeasureSpec,heightMeasureSpec);
    }

    private void remeasureChild(int widthMeasureSpec, int heightMeasureSpec){
        int lineSize = views.size();
        for(int i=0;i<lineSize;i++){
            int lineHeight = heights.get(i);//每一行行高
            List<View> lineViews = views.get(i);//每一行的子View
            int size = lineViews.size();

            for(int j=0;j<size;j++){
                View child = lineViews.get(j);
                LayoutParams lp= (LayoutParams) child.getLayoutParams();
                if(lp.height == LayoutParams.MATCH_PARENT){
//                    LayoutParams lp=child.getLayoutParams();
                    int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,0,lp.width);
                    int childHeightSpec = getChildMeasureSpec(heightMeasureSpec,0,lp.height);

                    child.measure(childWidthSpec,childHeightSpec);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = views.size();

        int currX = 0;
        int currY = 0;

        //遍历所有子View,对子View进行测量，分配到具体的行
        for(int i=0;i<lineCount;i++) {
            List<View> lineViews=views.get(i);//取出一行
            int lineHeight=heights.get(i);
            //遍历当前行的子View
            for(int j=0;j<lineViews.size();j++){
                //布局当前行的每一个view
                View child=lineViews.get(j);
                int left =currX;
                int top=currY;
                int right=left+child.getMeasuredWidth();
                int bottom=top+child.getMeasuredHeight();
                Log.i(TAG,"onLayout: i="+i+";j="+j);
                Log.i(TAG,"left:"+left);
                Log.i(TAG,"top:"+left);
                Log.i(TAG,"right:"+right);
                Log.i(TAG,"bottom:"+bottom);
                child.layout(left,top,right,bottom);
                //确定下一个view的left
                currX+=child.getWidth();
            }
            currY += lineHeight;
            currX = 0;
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new FlowLayout.LayoutParams((MarginLayoutParams) p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayout.LayoutParams(getContext(),attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p);
    }



    public static class LayoutParams extends MarginLayoutParams{
        int gravity=-1;

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a=c.obtainStyledAttributes(attrs,R.styleable.FlowLayout_Layout);

            try {
                gravity=a.getInt(R.styleable.FlowLayout_Layout_android_layout_gravity,-1);

            }finally {
                a.recycle();
            }
        }
    }
}
