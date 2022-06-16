package com.group2.minidog.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.group2.minidog.databinding.ActivitySignUpBinding;
import com.group2.minidog.ui.signin.SignInActivity;

public class SignUpActivity extends AppCompatActivity implements SignUpActivityI {

    private ActivitySignUpBinding binding;
    private SignUpPresenterI signUpPresenterI;
    private EditText etUsername, etEmail, etPassword, etRePassword;
    private ImageButton ibSignUp;
    private TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

        signUpPresenterI = new SignUpPresenter(this, this);

        ibSignUp.setOnClickListener(view -> signUpPresenterI.createUser(etEmail.getText().toString().trim(), etPassword.getText().toString().trim(), etRePassword.getText().toString().trim()));
        txtSignIn.setOnClickListener(view -> goToSignInActivity());
    }

    @Override
    public void initView() {
        etUsername = binding.etUsernameSignup;
        etEmail = binding.etEmailSignup;
        etPassword = binding.etPasswordSignup;
        etRePassword = binding.etRePasswordSignup;
        ibSignUp = binding.ibSignUp;
        txtSignIn = binding.txtSignIn;
    }

    @Override
    public void etEmailSetError(String error) {
        etEmail.setError(error);
    }

    @Override
    public void etPasswordSetError(String error) {
        etPassword.setError(error);
    }

    @Override
    public void etRePasswordSetError(String error) {
        etRePassword.setError(error);
    }

    @Override
    public void etEmailRequestFocus() {
        etEmail.requestFocus();
    }

    @Override
    public void etPasswordRequestFocus() {
        etPassword.requestFocus();
    }

    @Override
    public void etRePasswordRequestFocus() {
        etRePassword.requestFocus();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToSignInActivity(){
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}