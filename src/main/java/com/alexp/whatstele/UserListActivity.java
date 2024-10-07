package com.alexp.whatstele;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserListActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("message");

    private UserListViewModel userListViewModel;
    private RecyclerView recyclerViewUserList;
    private UserAdapter userAdapter;
    private String currentUserId;
    private static final String EXTRA_CURRENT_USER_ID = "current_id";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.item_logout)
            userListViewModel.logout();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userListViewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        initViews();
        observeViewModel();
        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        userListViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {

                userAdapter.setUserList(users);
            }
        });

        userAdapter.setOnUserClickLitener(new UserAdapter.OnUserClickLitener() {
            @Override
            public void onUserClick(User user) {
                Intent intent = ChatActivity.newIntent(UserListActivity.this,currentUserId,user.getId());
                startActivity(intent);
            }
        });
    }

    private void observeViewModel()
    {
        userListViewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser == null)
                {

                    Intent intent = AuthenticationActivity.newIntent(UserListActivity.this);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void initViews(){
        recyclerViewUserList = findViewById(R.id.recyclerViewUsers);
        userAdapter = new UserAdapter();
        recyclerViewUserList.setAdapter(userAdapter);

    }
    public static Intent newIntent(Context context,String currentUserId)
    {

        Intent intent =  new Intent(context,UserListActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        return intent;
    }


    @Override
    protected void onPause() {
        super.onPause();
        userListViewModel.setUserOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userListViewModel.setUserOnlineStatus(true);
    }
}