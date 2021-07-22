package com.example.version0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private ImageButton btn_cam;
    private ImageButton btn_upload;
    private ImageView bubble_window;
    private BottomNavigationView navView;
    private NoScrollViewPager pager;

    private final static int PERMISSION_REQUEST_CODE = 1001;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    pager.setCurrentItem(0);
                    return true;
                case R.id.action_record:
                    View view1 = View.inflate(MainActivity.this, R.layout.layout_popwindow, null);
                    popupWindow = new PopupWindow(view1);
                    //设置充满父窗体
                    popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    //popupWindow.setAnimationStyle(R.style.St);
                    //点击外部关闭弹框
                    popupWindow.setBackgroundDrawable(new ColorDrawable());
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setTouchable(true);
                    //从中间弹出
                    popupWindow.showAtLocation(view1,Gravity.CENTER|Gravity.START,0,800);
                    //找弹框控件
                    bubble_window = view1.findViewById(R.id.imageView);
                    btn_cam = view1.findViewById(R.id.imageButton);
                    btn_cam.getBackground().setAlpha(0);
                    //跳转录制
                    btn_cam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermission();
                            finish();
                        }
                    });
                    btn_upload = view1.findViewById(R.id.imageButton2);
                    btn_upload.getBackground().setAlpha(0);
                    //跳转上传
                    btn_upload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                            startActivity(intent);
                        }
                    });
                    //点击按钮关闭弹框
                    // popupWindow.dismiss();

                    return true;
                case R.id.action_mine:
                    pager.setCurrentItem(1);
                    return true;
                default: return false;
            }
        }
    };

    //
    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            CustomCameraActivity.startUI(this);
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
            CustomCameraActivity.startUI(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            CustomCameraActivity.startUI(this);
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.bottomNavigationView);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        pager = findViewById(R.id.view_pager);
        setupViewPager(pager);


    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new MineFragment());
        viewPager.setAdapter(adapter);
    }

}
