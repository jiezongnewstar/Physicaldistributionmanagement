package com.xibei.physicaldistributionmanagement.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.xibei.physicaldistributionmanagement.R;
import com.xibei.physicaldistributionmanagement.view.CircleProgressbar;

import static com.xibei.physicaldistributionmanagement.Constant.BASEURL;

public class SplashActivity extends AppCompatActivity {

    CircleProgressbar tv_red_skip;

    SimpleDraweeView simpleDraweeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.TRANSPARENT);
            setContentView(R.layout.activity_splash);

            initView();

        }
    }

    private void initView() {
        tv_red_skip = findViewById(R.id.tv_red_skip);
        simpleDraweeView = findViewById(R.id.simpaleDraweeView);

        initListener();

    }

    private void initListener() {
        tv_red_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_red_skip.stop();
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tv_red_skip.setTimeMillis(5000);
        tv_red_skip.setProgressColor(Color.BLACK);
        tv_red_skip.setCountdownProgressListener(5, new CircleProgressbar.OnCountdownProgressListener() {
            @Override
            public void onProgress(int what, int progress) {
                if (progress==5){
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tv_red_skip.start();

        initData();
    }

    private void initData() {
        Uri imgurl=Uri.parse(BASEURL+"/app/img/hy.jpg");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        imagePipeline.evictFromCache(imgurl);
        simpleDraweeView.setImageURI(imgurl);
    }
}
