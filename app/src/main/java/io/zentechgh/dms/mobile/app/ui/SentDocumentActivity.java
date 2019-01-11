package io.zentechgh.dms.mobile.app.ui;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

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
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterSent;
import io.zentechgh.dms.mobile.app.model.Sent_Documents;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentDocumentActivity extends AppCompatActivity{

    ConstraintLayout constraintLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    TextView tv_no_document;

    MaterialSearchView searchView;

    List<Sent_Documents> documentsList;

    RecyclerViewAdapterSent adapterSent;

    RecyclerView recyclerView;

    FirebaseUser currentUser;

    DatabaseReference dBRef;

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_sent_document);

        constraintLayout = findViewById(R.id.constraintLayout);

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_no_document = findViewById(R.id.tv_no_document);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentsList = new ArrayList<>();

        adapterSent = new RecyclerViewAdapterSent(this, documentsList);

        dBRef = FirebaseDatabase.getInstance().getReference("Sent_Documents")
                .child(currentUser.getUid());

        // method call
        displayDocuments();

    }

    public void displayDocuments(){

        // display progressbar
        progressBar.setVisibility(View.VISIBLE);

        dBRef.child("Documents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear list
                documentsList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Sent_Documents sent_documents = snapshot.getValue(Sent_Documents.class);

                    // hides the textView and displays the recyclerView
                    tv_no_document.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    // adds to list
                    documentsList.add(sent_documents);

                }

                // notify adapter of changes
                adapterSent.notifyDataSetChanged();

                if(!dataSnapshot.exists()){
                    // hides the recyclerView and displays the textView
                    recyclerView.setVisibility(View.GONE);
                    tv_no_document.setVisibility(View.VISIBLE);
                }

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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    private void searchDocument(String title) {

        Query query = dBRef.child("Documents").orderByChild("search")
                .startAt(title)
                .endAt(title + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear list
                documentsList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Sent_Documents sent_documents = snapshot.getValue(Sent_Documents.class);

                    documentsList.add(sent_documents);
                }

                // notify adapter of changes
                adapterSent.notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_search:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
