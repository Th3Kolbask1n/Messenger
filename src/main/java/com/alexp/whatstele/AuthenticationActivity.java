package com.alexp.whatstele;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;
    private TextView textViewRegister;
    private AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initViews();
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        observeViewModel();
        setupClickListeners();

    }

    private void observeViewModel(){

        authenticationViewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {

                if(firebaseUser!= null)
                {
                    Intent intent = UserListActivity.newIntent(AuthenticationActivity.this,
                            firebaseUser.getUid());

                    Toast.makeText(AuthenticationActivity.this,"Successful",Toast.LENGTH_SHORT).show();

                    startActivity(intent);

                    finish();
                }
                else
                {

                }

            }
        });
        authenticationViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if(error!=null)
                    Toast.makeText(AuthenticationActivity.this,error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners()
    {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editTextLogin.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if(!login.isEmpty() && !password.isEmpty())
                    authenticationViewModel.logIn(login,password);
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ResetPasswordActivity.newIntent(AuthenticationActivity.this,
                        editTextLogin.getText().toString().trim());
                startActivity(intent);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserRegistrationActivity.newIntent(AuthenticationActivity.this);
                startActivity(intent);
            }
        });
    }

    private void initViews()
    {
        editTextLogin = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
    }


    public static Intent newIntent(Context context)
    {
        return new Intent(context,AuthenticationActivity.class);
    }
}