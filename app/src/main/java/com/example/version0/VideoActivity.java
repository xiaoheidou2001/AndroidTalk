package com.example.version0;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.os.Message;

import java.io.IOException;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.os.Looper.getMainLooper;

public class VideoActivity extends AppCompatActivity {

    private com.example.version0.Message data;

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;

    private TextView tvUserName;
    private SeekBar seekBar;
    private TextView video_now_time;
    private TextView video_total_time;

    private ImageView replayBackground;
    private ImageButton replayButton;
    private TextView replayText;

    private int count = 0;
    public static final int UPDATE_UI = 1;

    private float touchDownX;  //滑动时手指按下的X坐标
    private float touchUpX;
    private float touchDownY;  //滑动时手指按下的Y坐标
    private float touchUpY;
    private AudioManager audioManager;
    private ImageView s_play;

    private View view1;
    private PopupWindow popupWindow;
    private TextView tvVolume;
    private ImageView imgVolume;


    private long downClick = 0;
    private long upClick = 0;
    private final int duration = 1000;

    private ImageView heart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Bundle bundle = getIntent().getExtras();
        data = (com.example.version0.Message)bundle.get("data");

        //视频播放
        surfaceView = findViewById(R.id.video);
        player = new MediaPlayer();
        tvUserName = findViewById(R.id.tv_username);
        tvUserName.setText(" From: " + data.getUser_name());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar = findViewById(R.id.video_seekBar);
        video_now_time = findViewById(R.id.video_now_time);
        video_total_time = findViewById(R.id.video_total_time);
        s_play = findViewById(R.id.play);
        heart = findViewById(R.id.img_heart);
        seekBar.setOnSeekBarChangeListener(change);
        try {
            player.setDataSource(data.getVideo_url());
            holder = surfaceView.getHolder();
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                    UIhandle.sendEmptyMessage(UPDATE_UI);
                    player.setLooping(false);
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    replayBackground = findViewById(R.id.imgbg);
                    replayButton = findViewById(R.id.imageButton4);
                    replayText = findViewById(R.id.tv_replay);
                    replayBackground.setVisibility(View.VISIBLE);
                    replayText.setVisibility(View.VISIBLE);
                    replayButton.setVisibility(View.VISIBLE);
                    replayButton.setEnabled(true);
                    replayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            player.seekTo(0);
                            player.start();
                            replayBackground.setVisibility(View.INVISIBLE);
                            replayText.setVisibility(View.INVISIBLE);
                            replayButton.setVisibility(View.INVISIBLE);
                            replayButton.setEnabled(false);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("player", "failed!QAQ");
        }

        view1 = View.inflate(VideoActivity.this, R.layout.layout_volume, null);
        popupWindow = new PopupWindow(view1);
        //设置充满父窗体
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow.setAnimationStyle(R.style.St);
        //点击外部关闭弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        //
        tvVolume = view1.findViewById(R.id.tv_volume);
        imgVolume = view1.findViewById(R.id.img_volume);


        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    touchDownX = event.getX();  //取得左右滑动时手指按下的X坐标
                    touchDownY = event.getY();
                    downClick = System.currentTimeMillis();
                    return true;
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    touchUpX= event.getX(); //取得左右滑动时手指松开的X坐标
                    touchUpY = event.getY();
                    upClick = System.currentTimeMillis();
                    if(Math.abs(touchUpX - touchDownX) < 10 && Math.abs(touchUpY - touchDownY) < 10 ) {
                        Log.d("click", ": " + (upClick-downClick));
                        if(upClick - downClick > duration) {
                            heart.setVisibility(View.VISIBLE);
                            Log.d("click", "long");
                            ObjectAnimator animator2x = ObjectAnimator.ofFloat(heart,"scaleX",1f,1.5f);
                            animator2x.setInterpolator(new LinearInterpolator());
                            animator2x.setDuration(1500);

                            ObjectAnimator animator2y = ObjectAnimator.ofFloat(heart,"scaleY",1f,1.5f);
                            animator2y.setInterpolator(new LinearInterpolator());
                            animator2y.setDuration(1500);
                            // 另一个 ObjectAnimator，对 target 控件的透明度进行修改，从 1 到 0.5f 循环
                            ObjectAnimator animator3 = ObjectAnimator.ofFloat(heart,"alpha",1f,0.5f);
                            animator3.setRepeatCount(3);
                            animator3.setDuration(500);
                            animator2y.setRepeatMode(ValueAnimator.REVERSE);
                            //
                            ObjectAnimator animator4 = ObjectAnimator.ofFloat(heart,"alpha",0.5f,0f);
                            animator3.setDuration(200);
                            // 将ObjectAnimator 都添加到 AnimatorSet 中
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(animator2x,animator2y,animator3);
                            AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.playSequentially(animatorSet,animator4);
                            animatorSet1.start();
                        } else {
                            Log.d("click", "short");
                            if(count % 2 == 0) {
                                s_play.setVisibility(View.VISIBLE);
                                player.pause();
                            } else {
                                s_play.setVisibility(View.INVISIBLE);
                                player.start();
                            }
                            count++;
                        }
                        downClick = 0;upClick = 0;
                    } else if(touchUpX - touchDownX > 100) {
                        int tmp = (int)(player.getCurrentPosition() + player.getDuration() * 0.2);
                        if(tmp > player.getDuration()) {tmp = player.getDuration();}
                        player.seekTo(tmp);
                    } else if(touchUpX - touchDownX < -100) {
                        int tmp = (int)(player.getCurrentPosition() - player.getDuration() * 0.2);
                        if(tmp < 0) {tmp = 0;}
                        player.seekTo(tmp);
                    } else if(touchUpY - touchDownY < -100) {
                        //调大音量
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 2,FLAG_PLAY_SOUND);
                        tvVolume.setText("音量: " + 100*audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) + " %");
                        popupWindow.showAtLocation(view1,Gravity.CENTER,0,0);
                        timer.start();
                    } else if(touchUpY - touchDownY > 100) {
                        //调小音量
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 2,FLAG_PLAY_SOUND);
                        tvVolume.setText("音量: " + 100*audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) + " %");
                        popupWindow.showAtLocation(view1,Gravity.CENTER,0,0);
                        timer.start();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    CountDownTimer timer = new CountDownTimer(1000,10) {
        @Override
        public void onTick(long l) {        }
        @Override
        public void onFinish() {
            popupWindow.dismiss();
        }

    };

    //更新时间
    private void updateTime(TextView textView,int millisecond){
        int second = millisecond/1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String str = null;
        if(hh!=0){
            str = String.format("%02d:%02d:%02d",hh,mm,ss);
        }else {
            str = String.format("%02d:%02d",mm,ss);
        }
        textView.setText(str);
    }

    private Handler UIhandle = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==UPDATE_UI) {
                int position = player.getCurrentPosition();
                int totalduration = player.getDuration();

                seekBar.setMax(totalduration);
                seekBar.setProgress(position);

                updateTime(video_now_time,position);
                updateTime(video_total_time,totalduration);

                UIhandle.sendEmptyMessageDelayed(UPDATE_UI, 5);
            }
        }
    };

    SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (player != null && player.isPlaying()) {
                // 设置当前播放的位置
                player.seekTo(progress);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }
    };

    public class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            player.pause();
        }
    }


}
