package com.example.version0;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.UiThread;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment implements FeedAdapter.IOnItemClickListener {

    private View view;
    private ImageSwitcher imageSwitcher;
    private LinearLayout point_layout;//声明导航圆点的布局
    private ImageView img_back;
    //实例化存储导航圆点的集合
    ArrayList<ImageView> points = new ArrayList<>();
    private int[]arrayPictures =new int[]{R.drawable.img02,R.drawable.img06,R.drawable.img05};//声明并初始化一个保存要显示图像ID的数组
    private int pictutureIndex;//要显示的图片在图片数组中的Index
    private float touchDownX;  //左右滑动时手指按下的X坐标
    private float touchUpX;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FeedAdapter adapter = new FeedAdapter();

    private Timer timer = new Timer();
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //////////滑动图片部分//////////
        imageSwitcher = view.findViewById(R.id.imgsw1);
        img_back = view.findViewById(R.id.img_back);
        initpoint();
        //为ImageSwicher设置Factory，用来为ImageSwicher制造ImageView
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView=new ImageView(getActivity());// 实例化一个ImageView类的对象
                imageView.setImageResource(arrayPictures[pictutureIndex]);//根据id加载默认显示图片
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }
        });
        imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    touchDownX = event.getX();  //取得左右滑动时手指按下的X坐标
                    return true;
            }else if(event.getAction()==MotionEvent.ACTION_UP){
                touchUpX= event.getX(); //取得左右滑动时手指松开的X坐标
//                    //手指从左往右滑动，看下一张
                    if(touchUpX-touchDownX>100){
                        pictutureIndex=pictutureIndex==0?arrayPictures.length-1:pictutureIndex-1;//如果图片是第一章图片，从左往右滑动就是最后一张图片，如果不是，索引往前减1
                        //设置图片切换的动画
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right));
                        //设置当前要看的图片
                        imageSwitcher.setImageResource(arrayPictures[pictutureIndex]);
                        //调用方法设置圆点对应状态
                        setImageBackground(pictutureIndex);
                        //从右往左，看上一张
                    }else
                        if (touchDownX - touchUpX > 100) {
                        //取得当前要看的图片index
                         pictutureIndex = pictutureIndex == arrayPictures.length - 1 ? 0 : pictutureIndex + 1;//如果图片是最后一张，从右往左滑动就是最后一张图片，如果不是最后一张，右往左滑就是索引往后+1
                        //设置切换动画
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left));
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
                        //设置要看的图片
                        imageSwitcher.setImageResource(arrayPictures[pictutureIndex]);
                        //调用方法设置圆点对应状态
                         setImageBackground(pictutureIndex);
                    }
                    return true;
                }
                return false;
            }
        });

        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                pictutureIndex = pictutureIndex == arrayPictures.length - 1 ? 0 : pictutureIndex + 1;
                try {
                    imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left));
                    imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置要看的图片
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        imageSwitcher.setImageResource(arrayPictures[pictutureIndex]);
                        //调用方法设置圆点对应状态
                        setImageBackground(pictutureIndex);
                    }
                });
            }
        };
        timer.schedule(task,5000,5000);





        //////////视频列表部分//////////
        final RecyclerView recyclerView = view.findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void hide() {
                imageSwitcher.animate().translationY(-imageSwitcher.getHeight()-img_back.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator());
                point_layout.animate().translationY(-imageSwitcher.getHeight()-point_layout.getHeight()-img_back.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator());
                recyclerView.animate().translationY(-imageSwitcher.getHeight()-point_layout.getHeight()-img_back.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator());
            }

            @Override
            public void show() {
                imageSwitcher.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());
                point_layout.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());
                recyclerView.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });
        getData(null);
        //设置Adapter每个item的点击事件
        adapter.setOnItemClickListener(this);
        return view;
    }

    //初始化导航圆点的方法
    private void initpoint(){
        point_layout= (LinearLayout) view.findViewById(R.id.point_layout);
        int count = point_layout.getChildCount();//获取布局中圆点数量
        for(int i =0;i<count;i++){
            //将布局中的圆点加入到圆点集合中
            points.add((ImageView) point_layout.getChildAt(i));
        }
        //设置第一张图片（也就是图片数组的0下标）的圆点状态为触摸实心状态
        points.get(0).setImageResource(R.drawable.ic_radio_button_checked_black_10dp);
    }
    //设选中图片对应的导航原点的状态
    public void setImageBackground(int selectImage) {
        for(int i=0;i<points.size();i++){
            //如果选中图片的下标等于圆点集合中下标的id，则改变圆点状态
            if(i==selectImage){
                points.get(i).setImageResource(R.drawable.ic_radio_button_checked_black_10dp);
            }else{
                points.get(i).setImageResource(R.drawable.ic_radio_button_unchecked_black_10dp);
            }
        }
    }

    private void getData(final String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Message> messages = baseGetMessagesFromRemote(studentId);
                if (messages != null && !messages.isEmpty()) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(messages);
                        }
                    });
                }
            }
        }).start();
    }

    public List<Message> baseGetMessagesFromRemote(String userName) {
        String urlStr;
        if(userName == null) {
            urlStr =
                    String.format("https://api-android-camp.bytedance.com/zju/invoke/video");
        }
        else {
            urlStr =
                    String.format("https://api-android-camp.bytedance.com/zju/invoke/video?student_id=%s",userName);
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
//        Toast.makeText(getActivity(), "发布者：" + data.getUser_name(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),VideoActivity.class);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private static final int SCROLL_DISTANCE = 50;
        private int totalScrollDistance;
        private boolean isShow = true;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstVisableItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            //当第一个item存在界面上时就不触发隐藏、显示操作
            if(firstVisableItem==0){
                return;
            }
            if ((dy > 0 && isShow) || (dy < 0 && !isShow)) {
                totalScrollDistance += dy;
            }
            if (totalScrollDistance > SCROLL_DISTANCE && isShow) {
                hide();
                isShow = false;
                totalScrollDistance = 0;
            } else if (totalScrollDistance < -SCROLL_DISTANCE && !isShow) {
                show();
                isShow = true;
                totalScrollDistance = 0;
            }
        }

        public abstract void hide();

        public abstract void show();
    }

}
