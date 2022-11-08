package com.example.art_blooddonation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.art_blooddonation.Adapter.UserAdapter;
import com.example.art_blooddonation.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;

    private CircleImageView nav_profile_image;
    private TextView nav_fullname, nav_email, nav_bloodgroup, nav_type;


    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    private DatabaseReference userRef;

    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Donation App");

        drawerLayout = findViewById(R.id.drawerLayout);
        nav_view = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        nav_view.setNavigationItemSelectedListener(this);

        progressBar= findViewById(R.id.progressbar);

        recyclerView=findViewById(R.id.RecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        userList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this,userList);

        recyclerView.setAdapter(userAdapter);

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    readRecipients();
                }else{
                    readDonors();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        nav_profile_image = nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodgroup = nav_view.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type = nav_view.getHeaderView(0).findViewById(R.id.nav_user_type);



        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        userRef.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                                              if(snapshot.exists()){
                                                  String name = snapshot.child("name").getValue().toString();
                                                  nav_fullname.setText(name);

                                                  String email = snapshot.child("email").getValue().toString();
                                                  nav_email.setText(email);


                                                  String bloodgroup =snapshot.child("bloodgroup").getValue().toString();
                                                  nav_bloodgroup.setText(bloodgroup);
                                                  String type =snapshot.child("type").getValue().toString();
                                                  nav_type.setText(type);


                                                  if(snapshot.hasChild("profilepictureurl")){
                                                      String imageUrl = snapshot.child("profilepictureurl").getValue().toString();
                                                      Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);

                                                  }else{
                                                      nav_profile_image.setImageResource(R.drawable.profile_image);
                                                  }
                                                  Menu nav_menu =  nav_view.getMenu();
                                                  if(type.equals("donor")){
                                                      nav_menu.findItem(R.id.sentEmail).setTitle("Received Emails");
                                                      nav_menu.findItem(R.id.notifications).setVisible(true);

                                                  }

                                              }

                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError error) {

                                          }
                                      }

        );
    }

    private void readDonors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query=reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);

                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this, "No donor", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query=reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this, "No recipients", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compatible:
                Intent compatible = new Intent(MainActivity.this, CategorySelectedActivity.class);
                compatible.putExtra("group","compatible whit me ");
                startActivity(compatible);
                break;
            case R.id.notifications:
                Intent notifications = new Intent(MainActivity.this, CategorySelectedActivity.class);
                startActivity(notifications);
                break;
            case R.id.sentEmail:
                Intent sentEmail = new Intent(MainActivity.this, SentEmailActivity.class);
                startActivity(sentEmail);
                break;
            case R.id.aplus:
                Intent aplus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                aplus.putExtra("group","A+");
                startActivity(aplus);
                break;
            case R.id.aminus:
                Intent aminus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                aminus.putExtra("group","A-");
                startActivity(aminus);
                break;
            case R.id.bplus:
                Intent bplus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                bplus.putExtra("group","B+");
                startActivity(bplus);
                break;
            case R.id.bminus:
                Intent bminus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                bminus.putExtra("group","B-");
                startActivity(bminus);
                break;
            case R.id.abplus:
                Intent abplus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                abplus.putExtra("group","AB+");
                startActivity(abplus);
                break;
            case R.id.abminus:
                Intent abminus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                abminus.putExtra("group","AB-");
                startActivity(abminus);
                break;
            case R.id.oplus:
                Intent oplus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                oplus.putExtra("group","O+");
                startActivity(oplus);
                break;
            case R.id.ominus:
                Intent ominus = new Intent(MainActivity.this, CategorySelectedActivity.class);
                ominus.putExtra("group","O-");
                startActivity(ominus);
                break;
            case R.id.profile:
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2=new Intent(MainActivity.this,logoutActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}