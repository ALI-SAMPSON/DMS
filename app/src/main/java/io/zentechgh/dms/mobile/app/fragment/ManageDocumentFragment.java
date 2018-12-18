package io.zentechgh.dms.mobile.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.RecyclerViewAdapterManage;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageDocumentFragment extends Fragment {

    View view;

    // for snackbar
    RelativeLayout relativeLayout;

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

        progressBar = view.findViewById(R.id.progressBar);

        // initializing variables
        editTextSearch = view.findViewById(R.id.editTextSearch);

        documentRef = FirebaseDatabase.getInstance().getReference("Documents");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        //adapterManage = new RecyclerViewAdapterManage(applicationContext,documentsList);

        recyclerView.setAdapter(adapterManage);

        documentsList = new ArrayList<>();

        //displayDocument();

        return view;

    }

    // method that displays the documents in a list view
    private void displayDocument(){

        // display progressbar when loading documents
        progressBar.setVisibility(View.VISIBLE);

        dBListener = documentRef.addValueEventListener(new ValueEventListener() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        //documentRef.removeEventListener(dBListener);
    }
}
