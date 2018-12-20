package io.zentechgh.dms.mobile.app.ui;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterUsers;
import io.zentechgh.dms.mobile.app.model.Users;

import static android.view.View.GONE;

public class AssignDocumentToUserActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    DatabaseReference usersRef;

    RecyclerViewAdapterUsers adapterUsers;

    List<Users> usersList;

    TextView document_title;

    EditText editTextSearch;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_document_to_user);

        constraintLayout =  findViewById(R.id.constraintLayout);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        document_title = findViewById(R.id.document_title);

        editTextSearch =  findViewById(R.id.editTextSearch);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterUsers = new RecyclerViewAdapterUsers(this,usersList);

        recyclerView.setAdapter(adapterUsers);

        usersList = new ArrayList<>();

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // method call to display users in recyclerView
        displayUsers();

        // method call to search for a user in the system
        searchForUser();


    }

    // message to read the admin from the database
    public  void displayUsers(){

        progressBar.setVisibility(View.VISIBLE);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if(search_users.getText().toString().equals("")) {
                //clears list
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Users users = snapshot.getValue(Users.class);

                    assert users != null;

                    assert currentUser != null;

                    if(!users.getUid().equals(currentUser.getUid())){
                        // add users except currently logged in user
                        usersList.add(users);
                    }

                }

                // notifies any data change
                adapterUsers.notifyDataSetChanged();

                // dismiss progressBar
               progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
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

                    if(!users.getUid().equals(currentUser.getUid())){
                        usersList.add(users);
                    }
                }

                // notify data change in adapter
                adapterUsers.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
