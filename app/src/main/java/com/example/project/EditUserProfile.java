package com.example.project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static io.fabric.sdk.android.Fabric.TAG;

public class EditUserProfile extends Activity {

    Uri selectedImage;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        selectedImage = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uname = findViewById(R.id.setUsername);
        uname.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        CircleImageView img = findViewById(R.id.UserImg);
        if(selectedImage!=null) {
            Picasso.get().load(selectedImage).into(img);
        }

        Spinner spinner = (Spinner) findViewById(R.id.themesMenu);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.themes_names, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }

    public void changeProfileImg(View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
        }
    }
//    public void setUsername(View v){
//
//        EditText uname = v.findViewById(R.id.setUsername);
//        //update the username of the user and the following id as well username_editPage
//
//    }
    public void deleteAccount(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure You want to delete this account?");
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finishAffinity();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), input.getText().toString());

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                                                    FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                        });
                            }});

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    EditText uname;
    EditText newPass;
    public void onSave(View v){
       EditText currPass = findViewById(R.id.currentPassword);
       newPass = findViewById(R.id.NewPassword);
       EditText rePass = findViewById(R.id.retypePassword);
        Button b = findViewById(R.id.saveSettings);
        b.setEnabled(false);
        b.setClickable(false);
        mAuth = FirebaseAuth.getInstance();
        Context context = this;
        user = mAuth.getCurrentUser();
        if(user==null)
            return;
        if(selectedImage!=null) {
            mStorageRef = FirebaseStorage.getInstance().getReference("profilePics/" + user.getUid() + ".jpg");
            FirebaseStorage.getInstance().getReference("profilePics/" + user.getUid() + ".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mStorageRef.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        String imageuri = uri.getResult().toString();
                        UserProfileChangeRequest user_update =new UserProfileChangeRequest.Builder().setDisplayName(uname.getText().toString()).setPhotoUri(Uri.parse(imageuri)).build();
                        user.updateProfile(user_update);
                        user.updatePassword(newPass.getText().toString());
                        Toast.makeText(context, "image saved", Toast.LENGTH_LONG).show();
                        finish();



                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "image saved "+e.toString(), Toast.LENGTH_LONG).show();
                        b.setEnabled(true);
                        b.setClickable(true);
                    });
                }
            });

        }
        else{
            UserProfileChangeRequest user_update =new UserProfileChangeRequest.Builder().setDisplayName(uname.getText().toString()).build();
            user.updateProfile(user_update);
            user.updatePassword(newPass.getText().toString());
        }
        //fingerprint authentication before saving

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data ==  null)
            return;
        if (requestCode == 1) {
            //TODO: action
            final Bundle extras = data.getExtras();
            if (extras != null) {
                selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                ImageView img = findViewById(R.id.UserImg);
                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

}
