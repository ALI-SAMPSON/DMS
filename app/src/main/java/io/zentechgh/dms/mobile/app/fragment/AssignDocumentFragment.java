package io.zentechgh.dms.mobile.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RelativeLayout;

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
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterManage;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterUsers;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignDocumentFragment extends Fragment {

    View view;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    // for snackbar
    RelativeLayout relativeLayout;

    EditText editTextSearch;

    RecyclerView recyclerView;

    FloatingActionButton fab_assign;

    RecyclerViewAdapterManage adapterManage;

    ValueEventListener dBListener;

    DatabaseReference documentsRef;

    List<Documents> documentsList;

    ProgressBar progressBar;

    HomeActivity applicationContext;


    public AssignDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (HomeActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assign_document, container, false);

        relativeLayout = view.findViewById(R.id.relativeLayout);

        // getting instance of the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // getting instance of the FirebaseUser
        currentUser = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.progressBar);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

        recyclerView = view.findViewById(R.id.recyclerView);

        fab_assign = view.findViewById(R.id.fab_assign);

        documentsRef = FirebaseDatabase.getInstance().getReference("Documents");

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        //adapterManage = new RecyclerViewAdapterManage(applicationContext,documentsList);

        recyclerView.setAdapter(adapterManage);

        documentsList = new ArrayList<>();

        // method to display users in recyclerView
        //displayDocuments();

        // method call to begin the search engine
        //searchForUsers();

        // method call to assign document to user
        assignDocument();

        return view;
    }

    // method that displays the documents in a list view
    private void displayDocuments(){

        // display progressbar when loading documents
        progressBar.setVisibility(View.VISIBLE);

        dBListener = documentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                documentsList.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Documents documents = snapshot.getValue(Documents.class);

                    // add rooms in db to the list in respect to their positions
                    documentsList.add(documents);

                }

                // notify adapter if there is data change
                adapterManage.notifyDataSetChanged();

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);

                // display error message
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // search for document in system
    private void searchForDocument(){

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // method search to search for document by title
                searchDocFile(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // method to perform the search
    private void searchDocFile(String title){

        DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference("Documents")
                .child(currentUser.getUid());

        Query query = searchRef.orderByChild("search")
                .startAt(title)
                .endAt(title + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // clears list
                documentsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Documents documents = snapshot.getValue(Documents.class);

                    documentsList.add(documents);
                }

                // notify adapter if there is data change
                adapterManage.notifyDataSetChanged();

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //documentRef.removeEventListener(dBListener);
    }


    // message to read the admin from the database
    /*public  void displayUsers(){

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

                    usersList.add(users);

                }

                // notifies any data change
                adapterUsers.notifyDataSetChanged();

                // }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }
    */

    // assign document to user
    private void assignDocument(){

        fab_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
