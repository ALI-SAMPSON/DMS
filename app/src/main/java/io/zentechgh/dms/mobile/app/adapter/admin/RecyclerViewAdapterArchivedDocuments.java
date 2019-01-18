package io.zentechgh.dms.mobile.app.adapter.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Admin;
import io.zentechgh.dms.mobile.app.model.ArchivedDocuments;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.admin.ViewDocumentAdminActivity;

public class RecyclerViewAdapterArchivedDocuments extends RecyclerView.Adapter<RecyclerViewAdapterArchivedDocuments.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<ArchivedDocuments> archivedDocumentsList;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // field to stored decrypted password
    String decryptedPassword;

    // static fields for db root ref
    private final static String documents_ref = "Documents";

    private final static String archived_documents_ref = "ArchivedDocuments";

    // default constructor
    public RecyclerViewAdapterArchivedDocuments(){}

    // defaultless constructor
    public RecyclerViewAdapterArchivedDocuments(Context mCtx, List<ArchivedDocuments> archivedDocumentsList){
        this.mCtx = mCtx;
        this.archivedDocumentsList = archivedDocumentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_archived_documents,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        // getting the position of each document
        final ArchivedDocuments archivedDocuments = archivedDocumentsList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.document_title.setText(archivedDocuments.getTitle());
        viewHolder.document_tag.setText(archivedDocuments.getTag());
        viewHolder.document_comment.setText(archivedDocuments.getComment());
        viewHolder.document_distributee.setText(archivedDocuments.getDistributee());

        // checking if the document is not equal to null
        if(archivedDocuments.getDocumentUrl() == null){
            //viewHolder.userImage.setImageResource(R.drawable.profile_icon);
            Glide.with(mCtx).load(R.drawable.profile_icon).into(viewHolder.document_image);
        }
        else{
            Glide.with(mCtx).load(archivedDocuments.getDocumentUrl()).into(viewHolder.document_image);
        }

        // onclick listener for button to view document
        viewHolder.button_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // passes image url of document to activity
                Intent intent = new Intent(mCtx, ViewDocumentAdminActivity.class);
                intent.putExtra("document_url",archivedDocuments.getDocumentUrl());
                intent.putExtra("document_title",archivedDocuments.getTitle());
                intent.putExtra("document_tag",archivedDocuments.getTag());
                intent.putExtra("document_comment",archivedDocuments.getComment());
                intent.putExtra("document_distributee",archivedDocuments.getDistributee());
                mCtx.startActivity(intent);


            }
        });


        // onclick listener for button to unarchive file
        viewHolder.button_unarchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_delete)
                        .setMessage("Unarchiving this document will add it back to the list of documents in the system ");

                alertDialog.setPositiveButton(R.string.text_archive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(mCtx);
                        LayoutInflater inflater = LayoutInflater.from(mCtx);
                        final View dialogView  = inflater.inflate(R.layout.custom_dialog_confirm_password,null);
                        dialogBuilder.setView(dialogView);

                        // reference to the EditText in the layout file (custom_dialog)
                        final EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

                        dialogBuilder.setTitle("Unarchive Document?");
                        dialogBuilder.setMessage("Please enter your password");
                        dialogBuilder.setPositiveButton("UNARCHIVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // getting text from EditText
                                final String password = editTextPassword.getText().toString();

                                DatabaseReference adminRef  = FirebaseDatabase.getInstance().getReference("Admin");

                                adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                            Admin admin = snapshot.getValue(Admin.class);

                                            assert admin != null;
                                            // getting admin email and password and storing them in variables
                                            String adminUid = admin.getUid();
                                            String adminEmail = admin.getEmail();
                                            String encryptedPassword = admin.getPassword();

                                            // getting string email from sharePreference
                                            SharedPreferences preferences =
                                                    PreferenceManager.getDefaultSharedPreferences(mCtx);
                                            String email = preferences.getString("email","");

                                            // decrypt password
                                            try {
                                                decryptedPassword = decryptPassword(encryptedPassword,email);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if(password.equals(decryptedPassword) && adminEmail.equals(email)){

                                                /**
                                                 * Code to delete selected document and move it to archive document table
                                                 */


                                                // displays the progressBar
                                                viewHolder.progressBar.setVisibility(View.VISIBLE);

                                                viewHolder.documents.setDocumentUrl(archivedDocuments.getDocumentUrl());
                                                viewHolder.documents.setTitle(archivedDocuments.getTitle());
                                                viewHolder.documents.setTag(archivedDocuments.getTag());
                                                viewHolder.documents.setComment(archivedDocuments.getComment());
                                                viewHolder.documents.setDistributee(archivedDocuments.getDistributee());
                                                viewHolder.documents.setKey(archivedDocuments.getKey());
                                                viewHolder.documents.setSearch(archivedDocuments.getSearch());

                                                // adding document to the ArchivedDocument table
                                                viewHolder.documentRef.push().setValue(viewHolder.documents)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    ArchivedDocuments selectedDocument = archivedDocumentsList.get(position);

                                                                    String selectedKey = selectedDocument.getKey();

                                                                    // deleting document from the Document table
                                                                    viewHolder.archiveDocumentRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){

                                                                                // display message if added successful
                                                                                Toast.makeText(mCtx,"document unarchived successfully",Toast.LENGTH_LONG).show();

                                                                            }
                                                                            else {
                                                                                // display message if error occurs
                                                                                Toast.makeText(mCtx,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                                else{
                                                                    // display message if error occurs
                                                                    Toast.makeText(mCtx,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                }

                                                                // displays the progressBar
                                                                viewHolder.progressBar.setVisibility(View.GONE);
                                                            }
                                                        });


                                            }
                                            else{
                                                // display a message if there is an error
                                                Toast.makeText(mCtx,"Incorrect password. Please Try Again!",Toast.LENGTH_LONG).show();
                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // display error message
                                        Toast.makeText(mCtx, databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });


                            }

                        });


                        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // dismiss the DialogInterface
                                dialogInterface.dismiss();
                            }
                        });

                        android.support.v7.app.AlertDialog alert = dialogBuilder.create();
                        alert.show();


                    }
                });

                alertDialog.setNegativeButton(R.string.text_dismiss_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

                // creating and showing alert dialog
                AlertDialog alert = alertDialog.create();
                alert.show();

            }

        });

    }


    // method to decrypt password
    private String decryptPassword(String password, String email) throws Exception {
        SecretKeySpec key  = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(password,Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedValue = new String(decVal);
        return decryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        return keySpec;
    }

    @Override
    public int getItemCount() {
        // return list size
        return archivedDocumentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        CircleImageView document_image;
        TextView document_title;
        TextView document_tag;
        TextView document_comment;
        TextView document_distributee;

        Button button_unarchive;

        Button button_view;

        ProgressBar progressBar;

        Documents documents;

        DatabaseReference documentRef;

        DatabaseReference archiveDocumentRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            document_image = itemView.findViewById(R.id.document_image);
            document_title= itemView.findViewById(R.id.tv_document_title);
            document_tag = itemView.findViewById(R.id.tv_document_tag);
            document_comment = itemView.findViewById(R.id.tv_document_comment);
            document_distributee = itemView.findViewById(R.id.tv_distributee);

            progressBar = itemView.findViewById(R.id.progressBar);

            button_unarchive = itemView.findViewById(R.id.button_unarchive);

            button_view = itemView.findViewById(R.id.button_view);

            documents = new Documents();

            documentRef  = FirebaseDatabase.getInstance().getReference(documents_ref);

            archiveDocumentRef  = FirebaseDatabase.getInstance().getReference(archived_documents_ref);

        }
    }

}
