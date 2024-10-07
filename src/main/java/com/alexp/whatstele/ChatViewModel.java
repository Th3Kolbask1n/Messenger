package com.alexp.whatstele;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference usersReference = firebaseDatabase.getReference("users");
    private DatabaseReference messageReference = firebaseDatabase.getReference("messages");

    private final String TAG = "ChatViewModel";

    MutableLiveData<List<Message>> messageList = new MutableLiveData<>();
    MutableLiveData<User> otherUser = new MutableLiveData<>();
    MutableLiveData<Boolean> messageSend = new MutableLiveData<>();
    MutableLiveData<String> error = new MutableLiveData<>();

    private String currentUserId;
    private String otherUserId;

    public ChatViewModel(String currentUserId, String otherUserId) {
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
        usersReference.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                otherUser.setValue(user1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageReference.child(currentUserId).child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messageListFromBase = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageListFromBase.add(message);
                }
                messageList.setValue(messageListFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<List<Message>> getMessageList() {
        return messageList;
    }

    public LiveData<User> getOtherUser() {
        return otherUser;
    }

    public LiveData<Boolean> getMessageType() {
        return messageSend;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public void sendMessage(Message message)

    {

        Log.d("SendBD", message.getIdSender() + " " + message.getIdReceiver());
        messageReference
                .child(message.getIdSender())
                .child(message.getIdReceiver())
                .push()
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        messageReference
                                .child(message.getIdReceiver())
                                .child(message.getIdSender())
                                .push()
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        messageSend.setValue(true);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        error.setValue(e.getMessage());

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }

    public void setUserOnlineStatus(boolean online)
    {
        usersReference.child(currentUserId).child("online").setValue(online);
    }
}
