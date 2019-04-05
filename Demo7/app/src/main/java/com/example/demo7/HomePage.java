package com.example.demo7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class HomePage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        getSupportActionBar().hide();//隐藏标题栏
        setContentView(R.layout.home);
    }

    public void recognitionClick(View v){
//动态申请相机权限
        if(ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this, Manifest.permission.CAMERA))
            { //此时我们都弹出提示
                ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            else
            {
                //这里是用户各种拒绝后我们也弹出提示
                ActivityCompat.requestPermissions(
                        HomePage.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }
        else
        {
            //正常情况，表示权限是已经被授予的

        }

        Intent it=new Intent(HomePage.this,Yolo.class);
        startActivity(it);

//        setContentView(R.layout.activity_camera);
//        int flag = getIntent().getIntExtra("Flag", 1);
//        Fragment fragment = null;
//        fragment = Capture2.newInstance();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container, fragment).commit();
    }

    public void navigationClick(View v){
        //动态申请相机权限
        if(ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this, Manifest.permission.CAMERA))
            { //此时我们都弹出提示
                ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            else
            {
                //这里是用户各种拒绝后我们也弹出提示
                ActivityCompat.requestPermissions(
                        HomePage.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }
        else
        {
            //正常情况，表示权限是已经被授予的

        }

        Intent it=new Intent(HomePage.this,Capture.class);
        startActivity(it);
    }
}


