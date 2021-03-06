package com.k3.k3pler.ui;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.k3.k3pler.handler.ServiceController;

/** Entry point for application **/
public class MainActivity extends Activity {
    private ServiceController serviceController;
    private int OVERLAY_REQUEST = 0x63;

    private void init(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
        serviceController = new ServiceController(this, ProxyService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        checkDrawOverlayPermission();
    }
    public void checkDrawOverlayPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_REQUEST);
            }else{
                startProxy();
            }
        } else{
            startProxy();
        }
    }
    private void startProxy(){
        stopProxyService();
        try{
            serviceController.startService();
        }catch (Exception e1){
            e1.printStackTrace();
            stopProxyService();
        }
        finish();
    }
    private void stopProxyService(){
        try{
            serviceController.stopService();
        }catch (Exception e){
           e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == OVERLAY_REQUEST) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startProxy();
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {}
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}


