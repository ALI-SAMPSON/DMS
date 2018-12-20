package io.zentechgh.dms.mobile.app.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.zentechgh.dms.mobile.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignDocumentFragment extends Fragment {

    View view;

    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_assign;

    public AssignDocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assign_document, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
        fab_assign = view.findViewById(R.id.fab_assign);

        // method call to begin the search engine
        searchUsers();

        // method call to assign document to user
        assignDocument();

        return view;
    }

    // search users in the system
    private void searchUsers(){

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // assign document to user
    private void assignDocument(){

        fab_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
