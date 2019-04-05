package com.example.demo7;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Yolo extends AppCompatActivity {

    private static final int COPY_FALSE = -1;
    private static final int DETECT_FINISH = 1;
    private static final int WRITE_EXTERNAL_STORAGE = 2;
    private static final String TAG = "Yolo";
    private static final int RESULT_LOAD_IMAGE = 3;

    ImageView view_srcimg;
    ImageView view_dstimg;
    TextView view_status;
    Bitmap dstimg;
    Bitmap srcimg;

    String srcimgpath;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DETECT_FINISH) {
                dstimg = BitmapFactory.decodeFile(FileUtils.getSDCardPath()+"yolo/out.jpg");
                view_dstimg.setImageBitmap(dstimg);
                view_status.setText("run time = " + (double)msg.obj + "s");
            }
            else
            if (msg.what == COPY_FALSE) {

            }
        }
    };

    public Yolo() {
        srcimgpath = FileUtils.getSDCardPath()+"yolo/data/bicycle.jpg";
    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
            mHandler.sendEmptyMessage(COPY_FALSE);
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void yoloDetect(){

        final String sdPath=FileUtils.getSDCardPath();
        new Thread(new Runnable() {
            public void run() {
                double runtime = testyolo(srcimgpath,sdPath);
                Log.i(TAG, "yolo run time " + runtime);
                Message msg = new Message();
                msg.what = DETECT_FINISH;
                msg.obj = runtime;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    public void exactresClick(View v){
        view_status.setText("exact model, please wait");
        copyFilesFassets(this, "cfg", FileUtils.getSDCardPath()+"yolo/cfg");
        copyFilesFassets(this, "data", FileUtils.getSDCardPath()+"yolo/data");
        copyFilesFassets(this, "weights", FileUtils.getSDCardPath()+"yolo/weights");
        view_status.setText("exact model finish");

    }

    public void analyseClick(View v){

        view_dstimg.setImageResource(R.drawable.yologo_1);
        view_status.setText("Analysing ...");
        yoloDetect();
    }

    public void captureClick(View v){
        //动态申请相机权限
        if(ActivityCompat.checkSelfPermission(Yolo.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Yolo.this, Manifest.permission.CAMERA))
            { //此时我们都弹出提示
                ActivityCompat.requestPermissions(Yolo.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            else
            {
                //这里是用户各种拒绝后我们也弹出提示
                ActivityCompat.requestPermissions(
                        Yolo.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }
        else
        {
            //正常情况，表示权限是已经被授予的

        }

        Intent intent=new Intent(Yolo.this,Capture.class);
        startActivity(intent);
    }

    public void selectimgClick(View v){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

//    public void aboutClick(View v){
//        /* @setIcon 设置对话框图标
//         * @setTitle 设置对话框标题
//         * @setMessage 设置对话框消息提示
//         * setXXX方法返回Dialog对象，因此可以链式设置属性
//         */
//        final AlertDialog.Builder normalDialog =
//                new AlertDialog.Builder(Yolo.this);
//        normalDialog.setIcon(R.drawable.yologo_1);
//        normalDialog.setTitle("About");
//        normalDialog.setMessage("This is a Demo use Darknet Yolo to detect a img \n");
//        normalDialog.setPositiveButton("Back",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//        // 显示
//        normalDialog.show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        getSupportActionBar().hide();//隐藏标题栏

        Log.d("Yolo","onCreate");

        //检查权限
        if(ActivityCompat.checkSelfPermission(Yolo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        { //调用这个方法只会在API>=23的时候才会起作用，否则一律返回false
            // 第一次请求权限时，用户拒绝了，调用后返回true
            // 第二次请求权限时，用户拒绝且选择了“不在提醒”，调用后返回false。
            // 设备的策略禁止当前应用获取这个权限的授权时调用后返回false 。
            if(ActivityCompat.shouldShowRequestPermissionRationale(Yolo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            { //此时我们都弹出提示
                ActivityCompat.requestPermissions(Yolo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            else
            {
                //这里是用户各种拒绝后我们也弹出提示
                ActivityCompat.requestPermissions(
                        Yolo.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
        else
        {
            //正常情况，表示权限是已经被授予的

        }

        // Example of a call to a native method
        view_srcimg = (ImageView) findViewById(R.id.srcimg);
        view_dstimg = (ImageView) findViewById(R.id.dstimg);
        view_status = (TextView) findViewById(R.id.status);

        dstimg = BitmapFactory.decodeFile(FileUtils.getSDCardPath()+"yolo/out.jpg");
        view_dstimg.setImageBitmap(dstimg);
        view_dstimg.setScaleType(ImageView.ScaleType.FIT_XY);

        view_srcimg.setScaleType(ImageView.ScaleType.FIT_XY);
        //yoloDetect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            // 如果不是document类型的Uri，则使用普通方式处理

            srcimgpath = getImagePath(uri, null);
            view_status.setText("selectfile = " + srcimgpath);
            srcimg = BitmapFactory.decodeFile(srcimgpath);
            view_srcimg.setImageBitmap(srcimg);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    public native void inityolo(String cfgfile, String weightfile);
    public native double testyolo(String imgfile,final String sdpath);
    public native boolean detectimg(Bitmap dst, Bitmap src);

}
