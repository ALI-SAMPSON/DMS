package io.zentechgh.dms.mobile.app.ui.user;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import io.zentechgh.dms.mobile.app.adapter.user.RecyclerViewAdapterSent;
import io.zentechgh.dms.mobile.app.model.SentDocuments;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentDocumentActivity extends AppCompatActivity{

    // global variables
    RelativeLayout relativeLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    TextView tv_no_document;

    TextView tv_no_search_result;

    MaterialSearchView searchView;

    List<SentDocuments> documentsList;

    RecyclerViewAdapterSent adapterSent;

    RecyclerView recyclerView;

    FirebaseUser currentUser;

    DatabaseReference dBRef;

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_sent_document);

        relativeLayout = findViewById(R.id.relativeLayout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_no_document = findViewById(R.id.tv_no_document);

        tv_no_search_result = findViewById(R.id.tv_no_search_result);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentsList = new ArrayList<>();

        adapterSent = new RecyclerViewAdapterSent(this, documentsList);

        dBRef = FirebaseDatabase.getInstance().getReference("SentDocuments")
                .child(currentUser.getUid());

        recyclerView.setAdapter(adapterSent);

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

                    // displays the textView
                    tv_no_document.setVisibility(View.VISIBLE);

                    // hides the recyclerView
                    recyclerView.setVisibility(View.GONE);

                }
                else{
                    // clear list
                    documentsList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        SentDocuments sent_documents = snapshot.getValue(SentDocuments.class);

                        // hides the textView
                        tv_no_document.setVisibility(View.GONE);

                        // displays the recycler view
                        recyclerView.setVisibility(View.VISIBLE);

                        // getting snapshot of each element
                        sent_documents.setKey(snapshot.getKey());

                        // adds documents to list
                        documentsList.add(sent_documents);

                    }
                }

                // notify adapter of changes
                adapterSent.notifyDataSetChanged();

                // hides progressbar
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // hides progressbar
                progressBar.setVisibility(View.GONE);

                // display Error message
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_other,menu);
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
            public boolean onQueryTextChange(String title) {
                if(!title.isEmpty()){
                    // method search to search for document by title
                    searchDocument(title.toLowerCase());
                }
                else{
                    searchDocument("");
                }
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

                if(!dataSnapshot.exists()){
                    // displays this textView to tell user that no search results found
                    tv_no_search_result.setVisibility(View.VISIBLE);

                    // hides the recycler view
                    recyclerView.setVisibility(GONE);
                }
                else{
                    // clear list
                    documentsList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        SentDocuments sent_documents = snapshot.getValue(SentDocuments.class);

                        // displays the recycler view
                        recyclerView.setVisibility(View.VISIBLE);

                        // hides this textView to tell user that no search results found
                        tv_no_search_result.setVisibility(View.GONE);

                        documentsList.add(sent_documents);
                    }
                }


                // notify adapter of changes
                adapterSent.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display Error message
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
