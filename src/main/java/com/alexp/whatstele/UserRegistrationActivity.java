package com.alexp.whatstele;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class UserRegistrationActivity extends AppCompatActivity {
    private EditText editTextLogin;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextAge;
    private Button buttonResgister;
    private UserRegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        initView();
        viewModel = new ViewModelProvider(this).get(UserRegistrationViewModel.class);
        observerViewModel();
        buttonResgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getTrimmedText(editTextLogin);
                String password = getTrimmedText(editTextPassword);
                String name = getTrimmedText(editTextName);
                String surname = getTrimmedText(editTextSurname);
                int  age = Integer.parseInt(getTrimmedText(editTextAge));

                viewModel.signUp(email,password,name,surname,age);
            }
        });
    }

    private void observerViewModel()
    {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if(errorMessage!=null)
                {
                    Toast.makeText(UserRegistrationActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

                }
            }
        });
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!=null)
                {
                    Intent intent = UserListActivity.newIntent(UserRegistrationActivity.this,firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void initView()
    {
        editTextLogin = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextAge = findViewById(R.id.editTextAge);
        buttonResgister = findViewById(R.id.buttonRegister);

    }

    private String getTrimmedText(EditText editText)
    {
        return editText.getText().toString().trim();
    }


    public static Intent newIntent(Context context)
    {
        return new Intent(context, UserRegistrationActivity.class);
    }
}