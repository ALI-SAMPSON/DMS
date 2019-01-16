package io.zentechgh.dms.mobile.app.fragment;

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
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterManageUser;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterUser;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.AdminHomeActivity;

public class ManageUsersFragment extends Fragment {

    View view;

    // for snackbar
   ConstraintLayout constraintLayout;

    EditText editTextSearch;

    RecyclerView recyclerView;

    RecyclerViewAdapterManageUser adapterManageUser;

    ValueEventListener dBListener;

    DatabaseReference usersRef;

    List<Users> usersList;

    ProgressBar progressBar;

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

        progressBar = view.findViewById(R.id.progressBar);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

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

                //clears list
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users = snapshot.getValue(Users.class);

                    assert users != null;

                        // add users
                        usersList.add(users);

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

                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);

                    assert users != null;

                    // add found user
                    usersList.add(users);

                }

                // notify data change in adapter
                adapterUsers.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
