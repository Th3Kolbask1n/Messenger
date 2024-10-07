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

public class ResetPasswordActivity extends AppCompatActivity {
    public static final String EXTRA_EMAIL = "email";
    private Button buttonResetPassword;
    private EditText editTextEmail;
    private ResetPasswordViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        editTextEmail.setText(email);
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.toString().trim();
                viewModel.resetPassword(email);
            }
        });
        observeViewModel();
    }
    private void observeViewModel()
    {
        viewModel.getSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(success)
                {
                    Toast.makeText(ResetPasswordActivity.this, R.string.reset_link_password,Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(ResetPasswordActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void initView()
    {
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        editTextEmail = findViewById(R.id.editTextTextForgotEmailAddress);
    }

    public static Intent newIntent(Context context, String email)

    {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra(EXTRA_EMAIL,email);
        return intent;
    }

    public void getTT()
    {

    }
}