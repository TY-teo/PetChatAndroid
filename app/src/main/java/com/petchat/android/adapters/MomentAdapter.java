package com.petchat.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petchat.android.R;
import com.petchat.android.models.Moment;

import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {

    private Context context;
    private List<Moment> moments;

    public MomentAdapter(Context context, List<Moment> moments) {
        this.context = context;
        this.moments = moments;
    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_moment, parent, false);
        return new MomentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, int position) {
        Moment moment = moments.get(position);
        
        holder.userName.setText(moment.getUserName());
        holder.time.setText(moment.getTime());
        holder.content.setText(moment.getContent());
        holder.likeCount.setText(String.valueOf(moment.getLikeCount()));
        holder.commentCount.setText(String.valueOf(moment.getCommentCount()));
        
        // 设置点赞状态
        if (moment.isLiked()) {
            holder.likeButton.setImageResource(R.drawable.ic_heart_filled);
            holder.likeButton.setColorFilter(context.getColor(R.color.pink));
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_heart_outline);
            holder.likeButton.setColorFilter(context.getColor(R.color.gray));
        }
        
        // 点赞按钮点击事件
        holder.likeButton.setOnClickListener(v -> {
            if (moment.isLiked()) {
                moment.setLiked(false);
                moment.setLikeCount(moment.getLikeCount() - 1);
                holder.likeButton.setImageResource(R.drawable.ic_heart_outline);
                holder.likeButton.setColorFilter(context.getColor(R.color.gray));
            } else {
                moment.setLiked(true);
                moment.setLikeCount(moment.getLikeCount() + 1);
                holder.likeButton.setImageResource(R.drawable.ic_heart_filled);
                holder.likeButton.setColorFilter(context.getColor(R.color.pink));
                
                // 添加动画效果
                holder.likeButton.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> holder.likeButton.animate().scaleX(1f).scaleY(1f).setDuration(100));
            }
            holder.likeCount.setText(String.valueOf(moment.getLikeCount()));
        });
        
        // 评论按钮点击事件
        holder.commentButton.setOnClickListener(v -> {
            Toast.makeText(context, "评论功能开发中...", Toast.LENGTH_SHORT).show();
        });
        
        // 分享按钮点击事件
        holder.shareButton.setOnClickListener(v -> {
            Toast.makeText(context, "分享功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    static class MomentViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView time;
        TextView content;
        TextView likeCount;
        TextView commentCount;
        ImageButton likeButton;
        ImageButton commentButton;
        ImageButton shareButton;

        MomentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            shareButton = itemView.findViewById(R.id.share_button);
        }
    }
}