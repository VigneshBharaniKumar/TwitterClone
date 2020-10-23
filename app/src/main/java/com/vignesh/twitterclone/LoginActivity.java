package com.vignesh.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout edtUserName, edtPassword;
    private Button btnLogIn, btnSignUp;

    private SweetAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserName = findViewById(R.id.edtUserName_logIn);
        edtPassword = findViewById(R.id.edtPassword_logIn);
        btnLogIn = findViewById(R.id.btnLogin_logIn);
        btnSignUp = findViewById(R.id.btnSignUp_logIn);

        btnSignUp.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, TwitterUsersActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLogin_logIn:

                if (!edtUserName.getEditText().getText().toString().isEmpty()) {
                    edtUserName.setError(null);
                    if (!edtPassword.getEditText().getText().toString().isEmpty()) {
                        edtPassword.setError(null);
                        login();
                    } else {
                        edtPassword.setError("Please Enter Your Password");
                    }
                } else {
                    edtUserName.setError("Please Enter the User Name / Email");
                }

                break;

            case R.id.btnSignUp_logIn:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
                break;

        }

    }

    private void login() {

        alertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setContentText("Please Wait...");
        alertDialog.show();

        ParseUser.logInInBackground(edtUserName.getEditText().getText().toString(), edtPassword.getEditText().getText().toString(), new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException e) {

                if (e == null) {

                    if (user != null) {

                        alertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Login Successful!")
                                .setContentText("Welcome , " + user.getUsername())
                                .setConfirmButton("Home", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent intent = new Intent(LoginActivity.this, TwitterUsersActivity.class);
                                        startActivity(intent);
                                        alertDialog.dismissWithAnimation();
                                        finish();
                                    }
                                });
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                    }

                } else {

                    alertDialog.dismissWithAnimation();

                    alertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Login Failed!")
                            .setContentText("Error: " + e);
                    alertDialog.show();

                }

            }

        });

    }

    @Override
    public void onBackPressed() {

        alertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
                .setContentText("Ok...Bye-bye then")
                .setConfirmButton("Bye", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        alertDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();

    }

}
