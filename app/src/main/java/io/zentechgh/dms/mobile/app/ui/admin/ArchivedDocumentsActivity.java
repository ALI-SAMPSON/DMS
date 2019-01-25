package io.zentechgh.dms.mobile.app.ui.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import io.zentechgh.dms.mobile.app.adapter.admin.RecyclerViewAdapterArchivedDocuments;
import io.zentechgh.dms.mobile.app.adapter.user.RecyclerViewAdapterSent;
import io.zentechgh.dms.mobile.app.model.ArchivedDocuments;
import io.zentechgh.dms.mobile.app.model.SentDocuments;

public class ArchivedDocumentsActivity extends AppCompatActivity {

    // global variables
    RelativeLayout relativeLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    TextView tv_no_archived_document;

    TextView tv_no_search_result;

    TextView tv_no_internet;

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

        relativeLayout = findViewById(R.id.relativeLayout);

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

        tv_no_search_result = findViewById(R.id.tv_no_search_result);

        tv_no_internet = findViewById(R.id.tv_no_internet);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        archivedDocumentsList = new ArrayList<>();

        // getting string uid from sharePreference
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // getting admin uid
        String uid = preferences.getString("uid","");

        adapterArchivedDocuments = new RecyclerViewAdapterArchivedDocuments(this, archivedDocumentsList);

        dBRef = FirebaseDatabase.getInstance().getReference("ArchivedDocuments").child(uid);

        // setting adapter
        recyclerView.setAdapter(adapterArchivedDocuments);

        // method to check if internet connection is available on device
        checkInternetConnection();

    }

    // checks if device is connected to an internet
    protected boolean isOnline(){

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // get all active networks be it WIFI or MOBILE DATA
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }

    }

    // method to check if internet is made
    public void checkInternetConnection(){
        if(isOnline()){
            // method call to display the document in a recycler View
            displayDocuments();
        }
        else{
            // display a text view with a message to user if there is no internet connection
            tv_no_internet.setVisibility(View.VISIBLE);
        }
    }

    // method to display achieved documents
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

                        // gets the unique for each item
                        archivedDocuments.setKey(snapshot.getKey());

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
                Snackbar.make(relativeLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
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

    // method searches for a particular archived documents on the system
    private void searchDocument(String title) {

        Query query = dBRef.orderByChild("search")
                .startAt(title)
                .endAt(title + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // checks if there are any archived documents

                if(!dataSnapshot.exists()){

                    // displays "No search result found" if there is no such search data
                    tv_no_search_result.setVisibility(View.VISIBLE);

                    // hides recyclerView
                    recyclerView.setVisibility(View.GONE);

                }
                else {
                    // clear list
                    archivedDocumentsList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        ArchivedDocuments archivedDocuments = snapshot.getValue(ArchivedDocuments.class);

                        // displays the data if search data found
                        tv_no_search_result.setVisibility(View.GONE);

                        // displays recyclerView
                        recyclerView.setVisibility(View.VISIBLE);

                        // add list of data(document)
                        archivedDocumentsList.add(archivedDocuments);
                    }
                }

                // notify adapter of changes
                adapterArchivedDocuments.notifyDataSetChanged();
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
