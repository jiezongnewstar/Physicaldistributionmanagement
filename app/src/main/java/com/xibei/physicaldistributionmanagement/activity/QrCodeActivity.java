package com.xibei.physicaldistributionmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xibei.physicaldistributionmanagement.R;
import com.xibei.physicaldistributionmanagement.util.ToastUtil;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static com.xibei.physicaldistributionmanagement.Constant.BASEURL;

public class QrCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {
    ZXingView zxingview;
    ImageView iv_scan, iv_home, iv_refresh, iv_back,iv_flash;
    TextView tv_title;
    boolean flash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        zxingview = findViewById(R.id.zxingview);
        iv_scan = findViewById(R.id.iv_scan);
        iv_flash = findViewById(R.id.iv_flash);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("扫一扫");
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_scan.setVisibility(View.GONE);
        iv_home = findViewById(R.id.iv_home);
        iv_home.setVisibility(View.GONE);
        iv_refresh = findViewById(R.id.iv_refresh);
        iv_refresh.setVisibility(View.GONE);
        zxingview.setType(BarcodeType.ONLY_QR_CODE, null);
        zxingview.setDelegate(this);
        iv_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flash){
                    flash = false;
                    iv_flash.setBackgroundResource(R.mipmap.open);
                    zxingview.closeFlashlight();
                }else {
                    flash = true;
                    iv_flash.setBackgroundResource(R.mipmap.close);
                    zxingview.openFlashlight();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        zxingview.startCamera();
//         mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        zxingview.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        zxingview.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zxingview.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();

        if (result.length()>=18){
        if (result.substring(0,18).equals(BASEURL)){
            Intent data = new Intent();
            data.putExtra("result",result);
            setResult(1001,data);
            finish();
        }else {
            ToastUtil.showText("请扫描正确二维码！");
            zxingview.startSpotAndShowRect();
            return;
        }
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        if (isDark&&!flash)
            ToastUtil.showText("光线太暗，请到光亮的地方或者打开闪光灯");

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ToastUtil.showText("打开摄像头出错");

    }
}
