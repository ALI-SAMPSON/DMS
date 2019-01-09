package io.zentechgh.dms.mobile.app.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterUsers;
import io.zentechgh.dms.mobile.app.model.Users;

public class AssignDocumentToUserActivity extends AppCompatActivity {

    // global variables
    RelativeLayout relativeLayout;

    Toolbar toolbar;

    MaterialSearchView searchView;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    DatabaseReference usersRef;

    RecyclerViewAdapterUsers adapterUsers;

    List<Users> usersList;

    TextView document_title;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_document_to_user);

        relativeLayout =  findViewById(R.id.relativeLayout);

        // getting reference to views
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_assign_document);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // getting string extra
        String documentTitle = getIntent().getStringExtra("documentTitle");
        String documentTag = getIntent().getStringExtra("documentTag");
        String documentComment = getIntent().getStringExtra("documentComment");
        String documentImage = getIntent().getStringExtra("documentImage");

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        document_title = findViewById(R.id.document_title);

        progressBar = findViewById(R.id.progressBar);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterUsers = new RecyclerViewAdapterUsers(this,usersList);

        recyclerView.setAdapter(adapterUsers);

        // method call to display users in recyclerView
        displayUsers();

        // method call to search for a user in the system
        //searchForUser();


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
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assign,menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        // Material SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setEllipsize(true);
        searchView.setSubmitOnClick(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String username) {
                // test if searchView is not empty
                if(!username.isEmpty()){
                    // method search to search for document by title
                    searchUser(username.toLowerCase());
                    searchView.clearFocus();
                }

                //else
                else{
                    searchUser("");
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String username) {
                // test if searchView is not empty
                if(!username.isEmpty()){
                    searchUser(username.toLowerCase());
                }

                //else
                else{
                    searchUser("");
                }

                return true;
            }
        });


        return true;
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
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finishes activity and does not take user to the interface until user goes back again
        finish();
    }
}
