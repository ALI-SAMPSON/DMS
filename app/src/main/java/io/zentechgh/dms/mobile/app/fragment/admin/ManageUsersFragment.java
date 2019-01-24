package io.zentechgh.dms.mobile.app.fragment.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.admin.RecyclerViewAdapterManageUser;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.admin.AdminHomeActivity;

public class ManageUsersFragment extends Fragment {

    View view;

    // for snack bar
    ConstraintLayout constraintLayout;

    LinearLayout search_layout;

    EditText editTextSearch;

    TextView tv_no_users;

    TextView tv_heading;

    DatabaseReference usersRef;

    List<Users> usersList;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    RecyclerViewAdapterManageUser adapterManageUser;

    ValueEventListener dBListener;

    AdminHomeActivity applicationContext;

    public ManageUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (AdminHomeActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_manage_users, container,false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        search_layout = view.findViewById(R.id.search_layout);

        progressBar = view.findViewById(R.id.progressBar);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

        tv_no_users = view.findViewById(R.id.tv_no_users);

        tv_heading = view.findViewById(R.id.tv_heading);

        progressBar = view.findViewById(R.id.progressBar);

        usersList = new ArrayList<>();

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        adapterManageUser = new RecyclerViewAdapterManageUser(applicationContext,usersList);

        recyclerView.setAdapter(adapterManageUser);

        // method call to display users in recyclerView
        displayUsers();

        // method to search for user in the system
        searchForUser();

        return view;

    }


    // message to read the admin from the database
    public  void displayUsers(){

        progressBar.setVisibility(View.VISIBLE);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // if no user exist in db
                if (!dataSnapshot.exists()) {

                    // sets visibility to gone on textView
                    tv_heading.setVisibility(View.GONE);

                    // sets visibility to gone on search layout
                    search_layout.setVisibility(View.GONE);

                    // sets visibility to gone on recyclerView
                    recyclerView.setVisibility(View.GONE);

                    // sets visibility to gone on textView
                    tv_no_users.setVisibility(View.VISIBLE);
                }

                else {

                //clears list
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users = snapshot.getValue(Users.class);

                    assert users != null;

                    // sets visibility to visible on view
                    tv_heading.setVisibility(View.VISIBLE);

                    // sets visibility to visible on view
                    search_layout.setVisibility(View.VISIBLE);

                    // sets visibility to visible on view
                    recyclerView.setVisibility(View.VISIBLE);

                    // sets visibility to visible on textView
                    tv_no_users.setVisibility(View.GONE);

                    // gets the unique key of users
                    users.setKey(snapshot.getKey());

                    // add users
                    usersList.add(users);

                }

            }


                // notifies any data change
                adapterManageUser.notifyDataSetChanged();

                // dismiss progressBar
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // dismiss progressBar
                progressBar.setVisibility(View.GONE);

                // display message if exception occurs
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // search for document in system
    private void searchForUser(){

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // method search to search for document by title
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    // method to search for user in the system
    private void searchUser(String username) {

        Query query = usersRef.orderByChild("search")
                .startAt(username)
                .endAt(username + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // clear list
                usersList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Users users = snapshot.getValue(Users.class);

                    assert users != null;

                    // sets visibility to visible on view
                    tv_heading.setVisibility(View.VISIBLE);

                    // sets visibility to visible on view
                    search_layout.setVisibility(View.VISIBLE);

                    // sets visibility to visible on view
                    recyclerView.setVisibility(View.VISIBLE);

                    // sets visibility to visible on textView
                    tv_no_users.setVisibility(View.GONE);

                    // add found user
                    usersList.add(users);

                }

                // notify data change in adapter
                adapterManageUser.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // display message if exception occurs
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });

    }

}
