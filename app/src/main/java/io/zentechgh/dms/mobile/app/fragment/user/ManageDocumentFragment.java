package io.zentechgh.dms.mobile.app.fragment.user;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.user.RecyclerViewAdapterManage;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.user.HomeActivity;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageDocumentFragment extends Fragment {

    View view;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    // for snackbar
    RelativeLayout relativeLayout;

    TextView tv_no_document;

    TextView tv_no_search_result;

    LinearLayout search_layout;

    EditText editTextSearch;

    RecyclerView recyclerView;

    RecyclerViewAdapterManage adapterManage;

    ValueEventListener dBListener;

    DatabaseReference documentRef;

    List<Documents> documentsList;

    ProgressBar progressBar;

    HomeActivity applicationContext;

    public ManageDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_document, container, false);

        relativeLayout = view.findViewById(R.id.relativeLayout);

        search_layout = view.findViewById(R.id.search_layout);

        // getting instance of the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // getting instance of the FirebaseUser
        currentUser = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.progressBar);

        tv_no_search_result = view.findViewById(R.id.tv_no_search_result);

        tv_no_document = view.findViewById(R.id.tv_no_document);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

        documentRef = FirebaseDatabase.getInstance().getReference("Documents");

        documentsList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        adapterManage = new RecyclerViewAdapterManage(applicationContext,documentsList);

        recyclerView.setAdapter(adapterManage);

        displayDocuments();

        searchForDocument();

        return view;

    }

    // method that displays the documents in a list view
    private void displayDocuments(){

        // display progressbar when loading documents
        progressBar.setVisibility(View.VISIBLE);

        dBListener = documentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // checks if no document exist in database

                if(!dataSnapshot.exists()){

                    // sets visibility to gone on view
                    search_layout.setVisibility(View.GONE);

                    // sets visibility to gone on view
                    recyclerView.setVisibility(View.GONE);

                    // sets visibility to visible on view
                    tv_no_document.setVisibility(View.VISIBLE);


                }
                else {

                    // adds document to list
                    documentsList.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                        Documents documents = snapshot.getValue(Documents.class);

                        // sets visibility to visible on view
                        search_layout.setVisibility(View.VISIBLE);

                        // sets visibility to visible on view
                        recyclerView.setVisibility(View.VISIBLE);

                        // sets visibility to gone on view
                        tv_no_document.setVisibility(View.GONE);

                        // add rooms in db to the list in respect to their positions
                        documentsList.add(documents);

                    }

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

        Query query = documentRef.orderByChild("search")
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

                    // clears list
                    documentsList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Documents documents = snapshot.getValue(Documents.class);

                        // displays the recycler view
                        recyclerView.setVisibility(View.VISIBLE);

                        // hides this textView to tell user that no search results found
                        tv_no_search_result.setVisibility(View.GONE);

                        documentsList.add(documents);
                    }

                }


                // notify adapter if there is data change
                adapterManage.notifyDataSetChanged();


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
}
