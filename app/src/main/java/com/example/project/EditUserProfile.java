package com.example.project;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class EditUserProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

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
        //upload a new image from galler
    }
//    public void setUsername(View v){
//
//        EditText uname = v.findViewById(R.id.setUsername);
//        //update the username of the user and the following id as well username_editPage
//
//    }
    public void deleteAccount(View v){

        //fingerprint verification before deleting from firebase

    }
    public void onSave(View v){

       EditText uname = v.findViewById(R.id.setUsername);

       EditText currPass = v.findViewById(R.id.currentPassword);
       //check if currPass == curr pass from firebase

       EditText newPass = v.findViewById(R.id.NewPassword);
       EditText rePass = v.findViewById(R.id.retypePassword);

       Spinner themeSpinner= v.findViewById(R.id.themesMenu);

       String selectedTheme = themeSpinner.getSelectedItem().toString();
       //yahan i will update themes


        //fingerprint authentication before saving

    }

}
