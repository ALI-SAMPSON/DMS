package io.zentechgh.dms.mobile.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.zentechgh.dms.mobile.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadDocumentFragment extends Fragment {

    View view;

    public UploadDocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_document, container, false);

        return view;
    }

}
