package io.zentechgh.dms.mobile.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadDocumentFragment extends Fragment {

    View view;

    ImageView scannedImageView;

    EditText editTextName,editTextComment;

    private AppCompatSpinner spinnerTag;
    private ArrayAdapter<CharSequence> arrayAdapter;

    HomeActivity applicationContext;

    public UploadDocumentFragment() {
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
        view = inflater.inflate(R.layout.fragment_upload_document, container, false);

        scannedImageView = view.findViewById(R.id.scannedImageView);

        editTextName = view.findViewById(R.id.editTextName);
        editTextComment = view.findViewById(R.id.editTextComment);

        // initializing the spinnerView and adapter
        spinnerTag = view.findViewById(R.id.spinnerTag);
        arrayAdapter = ArrayAdapter.createFromResource(applicationContext,R.array.tags,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTag.setAdapter(arrayAdapter);

        return view;
    }

}
