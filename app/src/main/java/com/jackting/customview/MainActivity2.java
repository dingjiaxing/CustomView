package com.jackting.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private LinearLayout root;

    private Button mBtnScrollTo;
    private Button mBtnScrollBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        root = findViewById(R.id.root);

        mBtnScrollTo=findViewById(R.id.btn_scrollto);
        mBtnScrollBy=findViewById(R.id.btn_scrollby);

        mBtnScrollTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"1 mBtnScrollTo onClick getScrollY:"+root.getScrollX()+"; getScrollY"+root.getScrollY());
                root.scrollTo(50,50);
                Log.i(TAG,"2 mBtnScrollTo onClick getScrollY:"+root.getScrollX()+"; getScrollY"+root.getScrollY());
            }
        });
        mBtnScrollBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"1 mBtnScrollBy onClick getScrollY:"+root.getScrollX()+"; getScrollY"+root.getScrollY());
                root.scrollBy(50,50);
                Log.i(TAG,"2 mBtnScrollBy onClick getScrollY:"+root.getScrollX()+"; getScrollY"+root.getScrollY());

            }
        });
    }
}
