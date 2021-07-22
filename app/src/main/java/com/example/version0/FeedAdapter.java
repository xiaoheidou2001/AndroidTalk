package com.example.version0;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VideoViewHolder>{
    private List<Message> data;
    private IOnItemClickListener mItemClickListener;

    public void setData(List<Message> messageList){
        data = new ArrayList<Message>();
        data = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false);
        return new VideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, final int position) {
        holder.bind(data.get(position));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, data.get(position));
                }
            }
        });
    }

    public interface IOnItemClickListener {

        void onItemCLick(int position, Message data);

//        void onItemLongCLick(int position, Message data);
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
        private TextView fromTV;
        private TextView toTV;
        private View contentView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView;
            fromTV = itemView.findViewById(R.id.tv_from);
            toTV = itemView.findViewById(R.id.tv_to);
            coverSD = itemView.findViewById(R.id.sd_cover);
        }
        public void bind(Message message){
            coverSD.setImageURI(message.getImage_url());
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            fromTV.setText("发布时间: "+ simpleDateFormat.format(message.getCreated_at()));
            toTV.setText("发布者: "+ message.getUser_name());
        }
        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }
        public void setOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                contentView.setOnLongClickListener(listener);
            }
        }

    }


}
