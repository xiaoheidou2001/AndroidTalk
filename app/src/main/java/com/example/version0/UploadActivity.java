package com.example.version0;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.version0.Message;
import com.example.version0.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.version0.PathUtils;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_COVER_VIDEO = 102;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private Uri coverVideoUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;
    private String mp4Path = "";
    private ImageView mImageView;
    private FullScreenVideoView mVideoView;

//    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        mVideoView = findViewById(R.id.videoView);

        Intent intent =getIntent();
        mp4Path=intent.getStringExtra("data");
        try {
            mVideoView.setVideoPath(mp4Path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.start();
        } catch (Exception e){
            e.printStackTrace();
        }
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        findViewById(R.id.choose_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_COVER_VIDEO);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.setVideoURI(coverVideoUri);
                mVideoView.start();
                }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
        else if (REQUEST_CODE_COVER_VIDEO == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverVideoUri = data.getData();
                ContentResolver cr = this.getContentResolver();
                Cursor cursor = cr.query(coverVideoUri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        mp4Path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        Log.d("AAAAA",mp4Path);
                    }
                }
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        final Retrofit retrofit  = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri mp4Uri = coverVideoUri;
        try {
            mp4Uri = PathUtils.getUriForFile(UploadActivity.this, mp4Path);
        } catch (Exception e){
            e.printStackTrace();
        }
        byte[] coverMp4Data = readDataFromUri(mp4Uri);


        //TODO 5
        // 使用api.submitMessage()方法提交留言
        MultipartBody.Part coverPart = MultipartBody.Part.createFormData("cover_image",
                "cover.png",
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        coverImageData));
        MultipartBody.Part contentPart = MultipartBody.Part.createFormData("video",
                "AA.mp4",
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        coverMp4Data));

        try {
            api.submitMessage(Constants.STUDENT_ID, "",Constants.USER_NAME, coverPart, contentPart,Constants.token).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    Log.d(TAG, "success");
                    Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "success");

                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(UploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "Failure");
                }
            });
        }catch (final Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UploadActivity.this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "网络异常" + e.toString());
                }
            });
        }
        // 如果提交成功则关闭activity，否则弹出toast
    }



    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
