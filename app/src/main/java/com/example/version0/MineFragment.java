package com.example.version0;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;

public class MineFragment extends Fragment implements FeedAdapter.IOnItemClickListener {

    private RecyclerView recyclerView;
    private FeedAdapter adapter = new FeedAdapter();
    private ImageView imgHead;
    private TextView tvYourName;
    private TextView tvID;
    private TextView tvMyVideo;

    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private Uri coverImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        recyclerView = view.findViewById(R.id.rv2);
        imgHead = view.findViewById(R.id.img_head);
        tvYourName = view.findViewById(R.id.tv_yourname);
        tvYourName.setText("用户名: " + Constants.USER_NAME);
        tvID = view.findViewById(R.id.tv_id);
        tvID.setText("用户ID: " + Constants.STUDENT_ID);
        tvMyVideo = view.findViewById(R.id.tv_myvideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        getData(Constants.STUDENT_ID);
        //设置Adapter每个item的点击事件
        adapter.setOnItemClickListener(this);
        //头像点击事件
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });
        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getData(final String studentID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Message> messages = baseGetMessagesFromRemote(studentID);
                if (messages != null && !messages.isEmpty()) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(messages);
                        }
                    });
                }
                tvMyVideo.setText("我的视频 | " + (messages.size()) + " 个");
            }
        }).start();
    }
    public List<Message> baseGetMessagesFromRemote(String userName) {
        Log.d("name", "name = " + userName);
        String urlStr;
        if(userName == null) {
            urlStr =
                    String.format("https://api-android-camp.bytedance.com/zju/invoke/video");
            Log.d("name", "1");
        }
        else {
            urlStr =
                    String.format("https://api-android-camp.bytedance.com/zju/invoke/video?student_id=%s",userName);
            Log.d("name", "2");
        }
        List<Message> result = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);

            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
                MessageListResponse mlr = new Gson().fromJson(reader, MessageListResponse.class);
                result = mlr.feeds;

                reader.close();
                in.close();

            } else {
                // 错误处理
            }
            conn.disconnect();

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    @Override
    public void onItemCLick(int position, Message data) {
        Intent intent = new Intent(getActivity(),VideoActivity.class);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                imgHead.setImageURI(coverImageUri);
                imgHead.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }
}
