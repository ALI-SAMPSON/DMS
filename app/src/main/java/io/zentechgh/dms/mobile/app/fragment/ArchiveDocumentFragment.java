package io.zentechgh.dms.mobile.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.zentechgh.dms.mobile.app.R;

public class ArchiveDocumentFragment extends Fragment {

    View view;

    public ArchiveDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_archive_document, container, false);

        return view;
    }


}