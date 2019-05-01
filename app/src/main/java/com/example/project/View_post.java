package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class View_post extends Activity {
    TextToSpeech t1;

    boolean like = false;
    String UID;
    ToggleButton liked;
    FirebaseAuth mAuth;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    SharePhotoContent content;
    String post_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        mAuth = FirebaseAuth.getInstance();
        liked = findViewById(R.id.like_image_v);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        post_key = getIntent().getStringExtra("post_key");
        DatabaseReference DB = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/posts").child(post_key);
        TextView Username = findViewById(R.id.view_username);
        TextView desc = findViewById(R.id.view_desc);
        ImageView img = findViewById(R.id.view_image);
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UN = (String) dataSnapshot.child("userName").getValue();
                String Desc = (String) dataSnapshot.child("desc").getValue();
                String Image = (String) dataSnapshot.child("image_uri").getValue();
                UID = (String) dataSnapshot.child("uid").getValue();
                set_like_button();
                Username.setText(UN);
                desc.setText(Desc);
                if (Image != null && !Image.equals("no_imag")) {
                    Picasso.get().load(Image).into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            set_share_button();
                            ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
                            shareButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {


                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        shareDialog = new ShareDialog(this);

        callbackManager = CallbackManager.Factory.create();
        set_recyclerView();


    }

    void set_like_button(){
        DatabaseReference likeref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/likes");
        likeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                    liked.setChecked(true);
                } else {
                    liked.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference likeref1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/likes").child(post_key);
        likeref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView l = findViewById(R.id.no_likes_v);
                l.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        liked.setOnClickListener(v -> {
            like = true;
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (like) {
                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                            UserRef.child(UID).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+post_key).removeValue();
                            likeref.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                            like=false;
                        } else {
                            UserRef.child(UID).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+post_key).setValue("liked");
                            likeref.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                            like=false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
    }

    void set_share_button() {
        ImageView img = findViewById(R.id.view_image);
        img.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        TextView desc = findViewById(R.id.view_desc);
        content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);
    }

    void set_recyclerView() {
        RecyclerView rv = findViewById(R.id.comment_rv);
        String post_key = getIntent().getStringExtra("post_key");
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_key);
        FirebaseRecyclerAdapter<String, String_viewHolder> firebaseadapter = new FirebaseRecyclerAdapter<String, String_viewHolder>(
                String.class,
                R.layout.comment_row,
                String_viewHolder.class,
                dref
        ) {
            @Override
            protected void populateViewHolder(String_viewHolder viewHolder, String model, int position) {
                viewHolder.setvalue(model);
            }
        };
        rv.setAdapter(firebaseadapter);

    }


    public static class String_viewHolder extends RecyclerView.ViewHolder {
        View v;

        public String_viewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
        }

        void setvalue(String cmnt) {
            TextView cmnt1 = v.findViewById(R.id.comnt_text);
            cmnt1.setText(cmnt);
        }
    }

    public void text_to_speech(View v) {
        TextView Username = findViewById(R.id.view_username);
        TextView desc = findViewById(R.id.view_desc);
        String toSpeak = Username.getText().toString() + " Posted:  " + desc.getText().toString();
        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void comment(View v) {
        TextView cmnt = findViewById(R.id.comment);
        String post_key = getIntent().getStringExtra("post_key");
        DatabaseReference CmntRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_key).push();
        String c = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        c = c + ": " + cmnt.getText().toString();
        CmntRef.setValue(c);

        cmnt.setText("");
    }


    public void share(View v) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {


            ImageView img = findViewById(R.id.view_image);
            img.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            TextView desc = findViewById(R.id.view_desc);
            content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
            shareButton.setShareContent(content);
            shareDialog.show(content);
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
