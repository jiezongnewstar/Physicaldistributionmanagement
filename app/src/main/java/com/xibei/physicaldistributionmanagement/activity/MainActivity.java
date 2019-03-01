package com.xibei.physicaldistributionmanagement.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.xibei.physicaldistributionmanagement.R;
import com.xibei.physicaldistributionmanagement.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.xibei.physicaldistributionmanagement.Constant.BASEURL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, SwipeRefreshLayout.OnRefreshListener {

    ImageView iv_scan, iv_home, iv_refresh, iv_back;

    WebView webView;

    ProgressBar pg1;

    LinearLayout ll_faild;

//    SwipeRefreshLayout swiperefresh;

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    WebSettings webSettings;

    private String path = "/app/index.asp";

    long firstTime = 0;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public ValueCallback<Uri> mUploadMessage;
    public final static int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 2;
    private final static int FILE_CHOOSER_RESULT_CODE = 3;
    private String fromWhere = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fromWhere = "";
        requestCodeQRCodePermissions();
    }

    private void initView() {
        iv_scan = findViewById(R.id.iv_scan);
        iv_home = findViewById(R.id.iv_home);
        iv_refresh = findViewById(R.id.iv_refresh);
        iv_back = findViewById(R.id.iv_back);
        ll_faild = findViewById(R.id.ll_faild);
        iv_back.setVisibility(View.GONE);
        webView = findViewById(R.id.webview);
        pg1 = findViewById(R.id.progressBar1);
//        swiperefresh = findViewById(R.id.swiperefresh);

        initListener();
    }

    private void initListener() {
        iv_scan.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        iv_back.setOnClickListener(this);
//        swiperefresh.setOnRefreshListener(this);
        webSettings = webView.getSettings();

        initWebview();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                webView.goBack();
                break;
            case R.id.iv_home:
                path = "/app/index.asp";
                webView.loadUrl(BASEURL+path);
                break;
            case R.id.iv_refresh:
                fromWhere = "qrcode";
                requestCodeQRCodePermissions();
                break;
            case R.id.iv_scan:
                webView.reload();
                break;
            default:
        }

    }

    private void initWebview() {
        webView.clearCache(true);
        webView.clearHistory();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                webView.setVisibility(View.VISIBLE);
                ll_faild.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return super.shouldOverrideUrlLoading(view, BASEURL+path);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //6.0以下执行
                //网络未连接
                webView.setVisibility(View.GONE);
                ll_faild.setVisibility(View.VISIBLE);
            }

            //处理网页加载失败时
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //6.0以上执行
                webView.setVisibility(View.GONE);
                ll_faild.setVisibility(View.VISIBLE);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (title != null) {
                    if (title.contains("404") || title.contains("System Error")||title.contains("无法找到")) {
                        //加载错误显示的页面
                        webView.setVisibility(View.GONE);
                        ll_faild.setVisibility(View.VISIBLE);
                    } else {
                        webView.setVisibility(View.VISIBLE);
                        ll_faild.setVisibility(View.GONE);

                    }
                }

                super.onReceivedTitle(view, title);
            }


            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {

            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {

            }



            // For Android < 5.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                openFileChooserImpl(uploadMsg);
                select(null,uploadMsg);
//                uploadMsg.onReceiveValue(null);
            }

            // For Android => 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg,
                                              WebChromeClient.FileChooserParams fileChooserParams) {
//                onenFileChooseImpleForAndroid(uploadMsg);
                select(uploadMsg,null);
//                uploadMsg.onReceiveValue(null);
                return true;
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    pg1.setVisibility(View.GONE);
//                    swiperefresh.setRefreshing(false);
                    if (webView.canGoBack()){
                        iv_back.setVisibility(View.VISIBLE);
                    }else {
                        iv_back.setVisibility(View.GONE);
                    }
                }
                else{
                    pg1.setVisibility(View.VISIBLE);
                    pg1.setProgress(newProgress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("提醒：");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("确定：");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }

            //设置响应js 的Prompt()函数
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("请输入：");
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }
        });

        webView.loadUrl(BASEURL+path);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }else {

            if (secondTime - firstTime > 2000) {
                ToastUtil.showText("再按一次退出程序");
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
                finish();
            }
            return true;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            if (mUploadMessage!=null){
                mUploadMessage.onReceiveValue(null);
            }

            if (mUploadMessageForAndroid5!=null){
                mUploadMessageForAndroid5.onReceiveValue(null);
            }
        }        if (requestCode==1001&&resultCode==1001) {
            if (data != null)
                webView.loadUrl(data.getStringExtra("result"));
        }

        List<Uri> resultlist = (data == null) ? null:  Matisse.obtainResult(data);
        switch (requestCode){
            case FILE_CHOOSER_RESULT_CODE:  //android 5.0以下 选择图片回调

                if (null == mUploadMessage)
                    return;
                mUploadMessage.onReceiveValue(resultlist.get(0));
                mUploadMessage = null;
                if (resultCode ==Activity.RESULT_CANCELED){
                    mUploadMessage.onReceiveValue(null);

                }
                break;

            case FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5:

                if (null == mUploadMessageForAndroid5 ||resultlist==null)
                    return;
                if (resultlist.get(0) != null) {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{resultlist.get(0)});
                    mUploadMessageForAndroid5 = null;
                } else {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                    mUploadMessageForAndroid5 = null;
                }
                mUploadMessageForAndroid5 = null;
                if (resultCode ==Activity.RESULT_CANCELED){
                    mUploadMessageForAndroid5.onReceiveValue(null);

                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (fromWhere.equals("qrcode")){
            fromWhere="";
            startActivityForResult(new Intent(MainActivity.this, QrCodeActivity.class),1001);
        }


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        requestCodeQRCodePermissions();
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }else {
            if (fromWhere.equals("qrcode")){
                fromWhere="";
                startActivityForResult(new Intent(MainActivity.this, QrCodeActivity.class),1001);
            }

        }
    }

    @Override
    public void onRefresh() {
        webView.reload();
    }


    /**
     * android 5.0 以下开启图片选择（原生）
     *
     * 可以自己改图片选择框架。
     */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);

    }

    /**
     * android 5.0(含) 以上开启图片选择（原生）
     *
     * 可以自己改图片选择框架。
     */
    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5);
    }


    private void select(ValueCallback<Uri[]> filePathCallback,ValueCallback<Uri> uploadMsg ){
        mUploadMessageForAndroid5 = filePathCallback;
        mUploadMessage = uploadMsg;
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofAll()) // 选择 mime 的类型
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.xibei.physicaldistributionmanagement.fileprovider"))
                .maxSelectable(1) // 图片选择的最多数量
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f) // 缩略图的比例
                .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                .autoHideToolbarOnSingleTap(true)
                .forResult(FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5); // 设置作为标记的请求码

    }

}
