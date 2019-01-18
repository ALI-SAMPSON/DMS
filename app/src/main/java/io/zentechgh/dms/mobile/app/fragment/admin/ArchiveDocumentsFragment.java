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
import io.zentechgh.dms.mobile.app.adapter.admin.RecyclerViewAdapterArchiveDocument;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.admin.AdminHomeActivity;

public class ArchiveDocumentsFragment extends Fragment {

    View view;

    // for snackbar
    ConstraintLayout constraintLayout;

    TextView tv_heading;

    LinearLayout search_layout;

    TextView tv_no_documents;

    EditText editTextSearch;

    RecyclerView recyclerView;

    RecyclerViewAdapterArchiveDocument adapterArchiveDocument;

    ValueEventListener dBListener;

    DatabaseReference documentRef;

    List<Documents> documentsList;

    ProgressBar progressBar;

    AdminHomeActivity applicationContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (AdminHomeActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_archive_documents, container,false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        search_layout = view.findViewById(R.id.search_layout);

        progressBar = view.findViewById(R.id.progressBar);

        tv_heading = view.findViewById(R.id.tv_heading);

        tv_no_documents = view.findViewById(R.id.tv_no_documents);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

        documentRef = FirebaseDatabase.getInstance().getReference("Documents");

        documentsList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        adapterArchiveDocument = new RecyclerViewAdapterArchiveDocument(applicationContext,documentsList);

        recyclerView.setAdapter(adapterArchiveDocument);

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

                // if no user exist in db
                if(!dataSnapshot.exists()){

                    // sets visibility to gone on textView
                    tv_heading.setVisibility(View.GONE);

                    // sets visibility to gone on search layout
                    search_layout.setVisibility(View.GONE);

                    // sets visibility to gone on recyclerView
                    recyclerView.setVisibility(View.GONE);

                    // sets visibility to gone on textView
                    tv_no_documents.setVisibility(View.VISIBLE);
                }
                else {

                    documentsList.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                        Documents documents = snapshot.getValue(Documents.class);

                        // sets visibility to visible on view
                        tv_heading.setVisibility(View.VISIBLE);

                        // sets visibility to visible on view
                        search_layout.setVisibility(View.VISIBLE);

                        // sets visibility to visible on view
                        recyclerView.setVisibility(View.VISIBLE);

                        // sets visibility to visible on textView
                        tv_no_documents.setVisibility(View.GONE);

                        // gets the unique key of users
                        documents.setKey(snapshot.getKey());

                        // add rooms in db to the list in respect to their positions
                        documentsList.add(documents);

                    }

                }



                // notify adapter if there is data change
                adapterArchiveDocument.notifyDataSetChanged();

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);

                // display error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
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

                // clears list
                documentsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Documents documents = snapshot.getValue(Documents.class);

                    documentsList.add(documents);
                }

                // notify adapter if there is data change
                adapterArchiveDocument.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //documentRef.removeEventListener(dBListener);
    }

}
