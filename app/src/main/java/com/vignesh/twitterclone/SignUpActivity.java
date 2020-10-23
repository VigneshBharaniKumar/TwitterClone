package com.vignesh.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout edtEmail, edtUserName, edtPassword;
    private Button btnSignUp;

    private SweetAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = findViewById(R.id.edtEmail_signUp);
        edtUserName = findViewById(R.id.edtUserName_signUp);
        edtPassword = findViewById(R.id.edtPassword_signUp);
        btnSignUp = findViewById(R.id.btnSignUp_signUp);

        btnSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSignUp_signUp:
                if (!edtEmail.getEditText().getText().toString().isEmpty()) {
                    edtEmail.setError(null);
                    if (!edtUserName.getEditText().getText().toString().isEmpty()) {
                        edtUserName.setError(null);
                        if (!edtPassword.getEditText().getText().toString().isEmpty()) {
                            edtPassword.setError(null);
                            signUp();
                        } else {
                            edtPassword.setError("Please Enter the Password");
                        }
                    } else {
                        edtUserName.setError("Please Enter the User Name");
                    }
                } else {
                    edtEmail.setError("Please Enter Your Email Id");
                }
                break;
        }

    }

    private void signUp() {

        alertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setContentText("Please Wait...");
        alertDialog.show();

        ParseUser user = new ParseUser();
        user.setEmail(edtEmail.getEditText().getText().toString());
        user.setUsername(edtUserName.getEditText().getText().toString());
        user.setPassword(edtPassword.getEditText().getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    alertDialog.dismissWithAnimation();

                    alertDialog = new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Signed Up...")
                            .setContentText("Account Created Successfully!")
                            .setConfirmButton("Log In", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    alertDialog.dismissWithAnimation();
                                }
                            });
                    alertDialog.show();

                } else {

                    alertDialog.dismissWithAnimation();

                    alertDialog = new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("SignUp Failed!")
                            .setContentText("Error Account Creation failed, Account could not be created" + " :"  + e);
                    alertDialog.show();

                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
