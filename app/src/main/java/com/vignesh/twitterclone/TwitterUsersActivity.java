package com.vignesh.twitterclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TwitterUsersActivity extends AppCompatActivity implements TwitterUsersRecyclerAdapter.onClickUserInterface{

    private MaterialToolbar toolbar;

    private ActionMenuItemView btnLogout_menu;

    private RecyclerView recyclerView;

    private SwipeRefreshLayout pullToRefreshLayout;

    private SweetAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        toolbar = findViewById(R.id.toolbar);
        btnLogout_menu = findViewById(R.id.btnLogout_home_menu);
        recyclerView = findViewById(R.id.recyclerView_twitterUsers);
        pullToRefreshLayout = findViewById(R.id.pullToRefreshLayout);

        setSupportActionBar(toolbar);

        setTitle("Welcome, " + ParseUser.getCurrentUser().getUsername());

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.btnLogout_home_menu:
                        logOut();
                        break;

                }

                return true;
            }
        });

        pullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

    }

    private void refreshList() {

        pullToRefreshLayout.setRefreshing(true);

        final ArrayList<String> users = new ArrayList<>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    pullToRefreshLayout.setRefreshing(false);

                    for (ParseUser user : objects) {
                        users.add(user.getUsername());
                    }

                    recyclerView.setAdapter(new TwitterUsersRecyclerAdapter(users, TwitterUsersActivity.this));
                    recyclerView.setLayoutManager(new LinearLayoutManager(TwitterUsersActivity.this));

                } else {

                    pullToRefreshLayout.setRefreshing(false);

                    alertDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error!")
                            .setContentText("Error: " + e);
                    alertDialog.show();

                }

            }
        });

    }

    private void logOut() {

        alertDialog = new SweetAlertDialog(TwitterUsersActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
                .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ParseUser.logOut();
                        Intent intent = new Intent(TwitterUsersActivity.this, LoginActivity.class);
                        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        logOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onClickUser(String selectedUser) {

    }
}
