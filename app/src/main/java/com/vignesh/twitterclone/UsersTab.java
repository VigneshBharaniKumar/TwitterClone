package com.vignesh.twitterclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.parse.Parse.getApplicationContext;

public class UsersTab extends Fragment implements TwitterUsersRecyclerAdapter.onClickUserInterface{

    private RecyclerView recyclerView;

    private SwipeRefreshLayout pullToRefreshLayout;

    private SweetAlertDialog alertDialog;

    public UsersTab() {
        // Required empty public constructor
    }

    public static UsersTab newInstance() {
        UsersTab fragment = new UsersTab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users_tab, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_twitterUsers);
        pullToRefreshLayout = view.findViewById(R.id.pullToRefreshLayout);

        refreshList();

        pullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        return view;

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

                    recyclerView.setAdapter(new TwitterUsersRecyclerAdapter(users, UsersTab.this));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

    @Override
    public void onClickUser(String selectedUser) {

    }
}
