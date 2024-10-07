package com.alexp.whatstele;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListViewModel extends ViewModel {
    private FirebaseAuth auth;

    private final String TAG = "UserListViewModel";

    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;


    public MutableLiveData<List<User>> getUserList() {
        return userList;
    }

    private MutableLiveData<List<User>> userList = new MutableLiveData<>();

    public UserListViewModel() {

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user.setValue(firebaseAuth.getCurrentUser());

            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> usersListFromDb = new ArrayList<>();

                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null)
                    return;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userFromDb = snapshot.getValue(User.class);

                    if (userFromDb == null || userFromDb.getId()==null)
                        return;

                    if (!userFromDb.getId().equals(currentUser.getUid()))
                        usersListFromDb.add(userFromDb);
                }
                userList.setValue(usersListFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }
    public void setUserOnlineStatus(boolean online)
    {
        FirebaseUser user = auth.getCurrentUser();
        if(user==null)
            return;
        usersReference.child(user.getUid()).child("online").setValue(online);
    }
    public void logout() {
        setUserOnlineStatus(false);
        auth.signOut();
    }
}
