package io.zentechgh.dms.mobile.app.ui.admin;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import io.zentechgh.dms.mobile.app.adapter.admin.RecyclerViewAdapterArchivedDocuments;
import io.zentechgh.dms.mobile.app.adapter.user.RecyclerViewAdapterSent;
import io.zentechgh.dms.mobile.app.model.ArchivedDocuments;
import io.zentechgh.dms.mobile.app.model.SentDocuments;

public class ArchivedDocumentsActivity extends AppCompatActivity {

    // global variables
    ConstraintLayout constraintLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    TextView tv_no_archived_document;

    MaterialSearchView searchView;

    List<ArchivedDocuments> archivedDocumentsList;

    RecyclerViewAdapterArchivedDocuments adapterArchivedDocuments;

    RecyclerView recyclerView;

    DatabaseReference dBRef;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_documents);

        constraintLayout = findViewById(R.id.constraintLayout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_no_archived_document = findViewById(R.id.tv_no_document);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        archivedDocumentsList = new ArrayList<>();

        // getting string email from sharePreference
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // getting admin uid
        String uid = preferences.getString("uid","");

        adapterArchivedDocuments = new RecyclerViewAdapterArchivedDocuments(ArchivedDocumentsActivity.this, archivedDocumentsList);

        dBRef = FirebaseDatabase.getInstance().getReference("ArchivedDocuments").child(uid);

        // method call
        displayDocuments();

    }

    public void displayDocuments(){

        // display progressbar
        progressBar.setVisibility(View.VISIBLE);

        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    // hides the recyclerView and displays the textView
                    recyclerView.setVisibility(View.GONE);

                    // sets visibility to visible
                    tv_no_archived_document.setVisibility(View.VISIBLE);
                }
                else{

                    // clear list
                    archivedDocumentsList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        ArchivedDocuments archivedDocuments = snapshot.getValue(ArchivedDocuments.class);

                        // hides the textView and displays the recyclerView
                        tv_no_archived_document.setVisibility(View.GONE);

                        // sets visibility to visible
                        recyclerView.setVisibility(View.VISIBLE);

                        // adds to list
                        archivedDocumentsList.add(archivedDocuments);

                    }

                }

                // notify adapter of changes
                adapterArchivedDocuments.notifyDataSetChanged();

                // hides progressbar
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // hides progressbar
                progressBar.setVisibility(View.GONE);

                // display Error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        // Material SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        searchView.setEllipsize(true);
        searchView.setSubmitOnClick(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String title) {
                if(!title.isEmpty()){
                    // method search to search for document by title
                    searchDocument(title.toLowerCase());
                }
                else{
                    searchDocument("");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });


        return true;

    }

    private void searchDocument(String title) {

        Query query = dBRef.orderByChild("search")
                .startAt(title)
                .endAt(title + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear list
                archivedDocumentsList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    ArchivedDocuments archivedDocuments = snapshot.getValue(ArchivedDocuments.class);

                    archivedDocumentsList.add(archivedDocuments);
                }

                // notify adapter of changes
                adapterArchivedDocuments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display Error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
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