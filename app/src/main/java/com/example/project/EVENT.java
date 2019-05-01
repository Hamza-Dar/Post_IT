package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.widget.ShareButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EVENT extends Activity  implements OnMapReadyCallback {

    RecyclerView rv;
    TextView name, desc, adr;
    Double latitude, longitude;
    GoogleMap mMap;
    ImageView img;
    FirebaseAuth mAuth;
    MapView mapView;
    String address;
    FirebaseRecyclerAdapter<post, post_viewholder> firebaseadapter;
    boolean like = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        latitude = 31.4812;
        longitude =74.3031;
        address = "FAST NU Lahore";
        rv = findViewById(R.id.rv_event_v);
        name = findViewById(R.id.name_event_v);
        desc = findViewById(R.id.desc_event_v);
        img = findViewById(R.id.img_Event_v);
        adr = findViewById(R.id.Event_address_v);
        mAuth = FirebaseAuth.getInstance();
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);

        String post_key = getIntent().getStringExtra("event_key");
        DatabaseReference DB = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/Events").child(post_key);

        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String UN =(String) dataSnapshot.child("event_name").getValue();
                String Desc =(String) dataSnapshot.child("event_desc").getValue();
                String Image =(String) dataSnapshot.child("image_url").getValue();
                address =(String) dataSnapshot.child("address").getValue();
                latitude = (double) dataSnapshot.child("latitude").getValue();
                longitude = (double) dataSnapshot.child("longitude").getValue();
                if (mMap != null) {
                    mMap.clear();
                    Geocoder cd = new Geocoder(getApplicationContext());

                    List<Address> adr;
                    TextView path = findViewById(R.id.Event_location);
                    LatLng sydney = new LatLng(latitude, longitude);

                    try {
                        adr = cd.getFromLocationName(path.getText().toString(), 2);
                        if (adr != null && adr.size() > 0) {
                            Address a = adr.get(0);
                            latitude = a.getLatitude();
                            longitude = a.getLongitude();
                            sydney = new LatLng(a.getLatitude(), a.getLongitude());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title("Event Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
                }
                name.setText(UN);
                desc.setText(Desc);
                adr.setText("Location: "+address);
                if(Image!=null && !Image.equals("no_imag")) {
                    Picasso.get().load(Image).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        rv.setLayoutManager(layout);
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference likeref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/likes");
        DatabaseReference dref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/Event_posts");
        dref = dref.child(getIntent().getStringExtra("event_name"));
        firebaseadapter = new FirebaseRecyclerAdapter<post, post_viewholder>( post.class,
                R.layout.post_view,
                post_viewholder.class,
                dref
        ) {
            @Override
            protected void populateViewHolder(post_viewholder viewHolder, post model, int position) {

                viewHolder.set_post(model, getRef(position).getKey(), getApplicationContext());
                likeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(getRef(position).getKey()).hasChild(mAuth.getCurrentUser().getUid())) {
                            viewHolder.like.setChecked(true);
                        } else {
                            viewHolder.like.setChecked(false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.like.setOnClickListener(v -> {
                    like = true;
                    likeref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (like) {
                                if (dataSnapshot.child(getRef(position).getKey()).hasChild(mAuth.getCurrentUser().getUid())) {
                                    UserRef.child(model.getUID()).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+getRef(position).getKey()).removeValue();
                                    likeref.child(getRef(position).getKey()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    like=false;
                                } else {
                                    UserRef.child(model.getUID()).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+getRef(position).getKey()).setValue("liked");
                                    likeref.child(getRef(position).getKey()).child(mAuth.getCurrentUser().getUid()).setValue("Random");
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
        };
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(firebaseadapter);


    }

    public void mapclick(View v){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", latitude, longitude, address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        Geocoder cd =  new Geocoder(this);

        List<Address> adr;
        TextView path = findViewById(R.id.Event_location);
        LatLng sydney = new LatLng(latitude, longitude);

        try {
            adr = cd.getFromLocationName(path.getText().toString(), 2);
            if(adr!=null){
                Address a = adr.get(0);
                latitude = a.getLatitude();
                longitude=a.getLongitude();
                sydney = new LatLng(a.getLatitude(), a.getLongitude());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(sydney).title("Event")).setDraggable(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
