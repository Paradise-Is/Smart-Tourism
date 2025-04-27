package com.example.smarttourism.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.Comment;
import com.example.smarttourism.util.DBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> list = new ArrayList<>();
    private DBHelper dbHelper;
    private Activity mActivity;
    private String currentUsername;
    private OnCommentDeletedListener deleteListener;

    public CommentAdapter(Activity activity, String currentUsername, OnCommentDeletedListener deleteListener) {
        this.mActivity = activity;
        this.currentUsername = currentUsername;
        this.deleteListener = deleteListener;
        this.dbHelper = new DBHelper(activity);
        dbHelper.open();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.comment_item, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Comment comment = list.get(i);
        //在子项上显示内容
        String username = comment.getComment_username();
        //将相关信息显示到界面上
        String selection = "username=?";
        String[] selectionArgs = {username};
        //根据用户查询到对应项
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbHeadshot = cursor.getString(cursor.getColumnIndex("headshot"));
            //将头像数据显示到界面中
            if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                File imgFile = new File(dbHeadshot);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    viewHolder.userHeadshot.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    viewHolder.userHeadshot.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时显示默认头像
                    viewHolder.userHeadshot.setImageResource(R.mipmap.mine_headshot);
                }
            } else {
                viewHolder.userHeadshot.setImageResource(R.mipmap.mine_headshot);
            }
            @SuppressLint("Range")
            String dbNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            viewHolder.userNickname.setText(dbNickname);
        }
        viewHolder.commentDate.setText(comment.getComment_date());
        viewHolder.commentText.setText(comment.getComment_text());
    }

    //设置数据集
    public void setData(List<Comment> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //回调接口，通知宿主刷新
    public interface OnCommentDeletedListener {
        void onCommentDeleted();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userHeadshot;
        private final TextView userNickname, commentDate, commentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userHeadshot = itemView.findViewById(R.id.userHeadshot);
            userNickname = itemView.findViewById(R.id.userNickname);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentText = itemView.findViewById(R.id.commentText);
            //设置评论点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos < 0 || pos >= list.size()) return;
                    Comment comment = list.get(pos);
                    //仅允许删除用户自己的评论
                    if (currentUsername.equals(comment.getComment_username())) {
                        //弹出弹窗，确认是否注销
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setMessage("确认删除这条评论？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.getDatabase().delete("SightComment", "comment_id=?", new String[]{String.valueOf(comment.getComment_id())});
                                //从列表删并刷新这一行动画
                                list.remove(pos);
                                notifyItemRemoved(pos);
                                //通知宿主界面
                                if (deleteListener != null) {
                                    deleteListener.onCommentDeleted();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}

