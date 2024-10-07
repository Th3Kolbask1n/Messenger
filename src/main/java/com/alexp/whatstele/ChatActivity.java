package com.alexp.whatstele;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String EXTRA_CURRENT_USER_ID = "current_id";

    private static final String EXTRA_OTHER_USER_ID = "other_id";
    private TextView textViewTitle;
    private View viewOnlineStatus;
    private RecyclerView recyclerViewMessage;
    private EditText editTextMessage;
    private ImageView imageViewSendMessage;
    private ChatAdapter chatAdapter;
    private String currentUserId;
    private String otherUserId;
    private ChatViewModel chatViewModel;
    private ChatViewModelFactory chatViewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);
        chatAdapter = new ChatAdapter(currentUserId);
        recyclerViewMessage.setAdapter(chatAdapter);
        chatViewModelFactory = new ChatViewModelFactory(currentUserId,otherUserId);

        chatViewModel = new ViewModelProvider(this,chatViewModelFactory).get(ChatViewModel.class);

        observeViewModel();
        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextMessage.getText().toString().trim().isEmpty()) {
                    Message message = new Message(currentUserId, otherUserId, editTextMessage.getText().toString().trim());
                    chatViewModel.sendMessage(message);
                    recyclerViewMessage.scrollToPosition(chatAdapter.getItemCount() - 1);

                }
            }
        });

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if(chatAdapter.getItemCount()>0) {
                android.graphics.Rect r = new android.graphics.Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    recyclerViewMessage.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });

    }
    private void observeViewModel()
    {
        chatViewModel.getMessageList().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {

                chatAdapter.setMessageList(messages);
                recyclerViewMessage.scrollToPosition(messages.size() - 1);

            }
        });
        chatViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(ChatActivity.this, error,Toast.LENGTH_SHORT).show();
            }
        });
        chatViewModel.getMessageType().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                editTextMessage.setText("");
            }
        });
        chatViewModel.getOtherUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                String userInfo = String.format("%s %s", user.getName(), user.getSurname());
                textViewTitle.setText(userInfo);
                int backgroundResId;

                if (user.isOnline())
                    backgroundResId = R.drawable.circle_green;
                else
                    backgroundResId = R.drawable.circle_red;
                Drawable background = ContextCompat.getDrawable(ChatActivity.this,backgroundResId);
                viewOnlineStatus.setBackground(background);
            }
        });
    }
    private void initViews()
    {
        textViewTitle = findViewById(R.id.TextViewTitle1);
        viewOnlineStatus = findViewById(R.id.viewOnlineStatus);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        editTextMessage = findViewById(R.id.editTextMessage1);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);

    }

    public static Intent newIntent(Context context, String currentUserId, String otherUserId)
    {
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID,currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID,otherUserId);


        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatViewModel.setUserOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatViewModel.setUserOnlineStatus(true);
    }
}