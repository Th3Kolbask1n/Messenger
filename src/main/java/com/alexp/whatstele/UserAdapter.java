package com.alexp.whatstele;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<User> userList = new ArrayList<>();


    private OnUserClickLitener onUserClickLitener;
    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String userInfo = String.format("%s %s %s",user.getName(), user.getSurname(), user.getAge());

        holder.textViewUserInfo.setText(userInfo);
        int backgroundResId;
        if (user.isOnline())
            backgroundResId = R.drawable.circle_green;
        else
            backgroundResId = R.drawable.circle_red;
        Drawable background = ContextCompat.getDrawable(holder.itemView.getContext(),backgroundResId);
        holder.onlineStatus.setBackground(background);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onUserClickLitener!=null)
                {
                    onUserClickLitener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewUserInfo;
        private View onlineStatus;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserInfo = itemView.findViewById(R.id.textViewUserInfo);
            onlineStatus = itemView.findViewById(R.id.viewOnlineStatusOnList);
        }
    }

    interface  OnUserClickLitener{
        void onUserClick(User user);
    }

    public void setOnUserClickLitener(OnUserClickLitener onUserClickLitener) {
        this.onUserClickLitener = onUserClickLitener;
    }

    public OnUserClickLitener getOnUserClickLitener() {
        return onUserClickLitener;

    }

}
