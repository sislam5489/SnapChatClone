package edu.fordham.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayImage extends AppCompatActivity {

    String userId;
    private ImageView mImage;
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String chatOrStory;

    //flag if found first image
    private boolean started = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        Log.d("ChatOrStory","Activity Started");
       // Toast.makeText(this,"Activity Started",Toast.LENGTH_SHORT).show();
        //get bundle passed through StoryViewHolder Onclick
        Intent intent = getIntent();
        //Bundle b =  getIntent().getExtras();
        userId = intent.getStringExtra("userId");
       // userId = b.getString("userId");
        Log.d("ChatOrStory",userId);
        Log.d("ChatOrStory","Hello");
       Toast.makeText(this,"UserId: " + userId,Toast.LENGTH_SHORT).show();

       // chatOrStory = b.getString("chatOrStory");
        chatOrStory = intent.getStringExtra("chatOrStory");
      Log.i("ChatOrStory",chatOrStory);
        Toast.makeText(this,"Chat or Story: " + chatOrStory,Toast.LENGTH_SHORT).show();

        mImage = findViewById(R.id.image);

        switch(chatOrStory){
            case "chat":
                listenForChat();
            case "story":
                listenForStory();
        }
        //Log.d("ChatOrStory",userId);
        listenForStory();
    }

    private void listenForChat() {
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser).child("received").child(userId);
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = "";
                long timestampBeg = 0;
                long timestampEnd = 0;
                //loop through stories of followers
                for(DataSnapshot chatSnapShot: snapshot.child("story").getChildren()){
                    if(chatSnapShot.child("imageUrl").getValue() != null){
                        imageUrl = chatSnapShot.child("imageUrl").getValue().toString();
                        Log.i("Userinfo","ImageUrl " + imageUrl);
                    }

                        imageUrlList.add(imageUrl);
                        if(!started){
                            started = true;
                            initializeDisplay();
                            //Picasso.get().load(imageUrl).into(mImage);
                        }
                        //remove snap
                        chatDb.child(chatSnapShot.getKey()).removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ArrayList<String> imageUrlList = new ArrayList<>();
    private void listenForStory() {
            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            followingStoryDb.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String imageUrl = "";
                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    //loop through stories of followers
                    for(DataSnapshot storySnapshot: snapshot.child("story").getChildren()){
                        if(storySnapshot.child("timestampBeg").getValue() != null){
                            timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                            Log.i("Userinfo","TimestampBeg " + String.valueOf(timestampBeg));
                        }
                        if(storySnapshot.child("timestampEnd").getValue() != null){
                            timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                            Log.i("Userinfo","TimestampEnd " + String.valueOf(timestampEnd));
                        }
                        if(storySnapshot.child("imageUrl").getValue() != null){
                            imageUrl = storySnapshot.child("imageUrl").getValue().toString();
                            Log.i("Userinfo","ImageUrl " + imageUrl);
                        }
                        long timestampCurrent = System.currentTimeMillis();
                        if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                            imageUrlList.add(imageUrl);
                            if(!started){
                                started = true;
                                initializeDisplay();

                                //Picasso.get().load(imageUrl).into(mImage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    //iterate through stories of users
    private int imageIterator = 0;
    private void initializeDisplay() {
        Glide.with(getApplication()).load((imageUrlList).get(imageIterator)).placeholder(R.drawable.background_splash).into(mImage);

        Log.i("loadimage",imageUrlList.get(imageIterator));
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay = 5000; //10 seconds

        //automatically calls changeImage every 5 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this,delay);
            }
        },delay);
    }

    //when you click on image it goes on to next story in list
    private void changeImage() {

        if(imageIterator == imageUrlList.size()-1){
            finish();
            return;
        }
        imageIterator ++;
        Glide.with(getApplication()).load((imageUrlList).get(imageIterator)).placeholder(R.drawable.background_splash).into(mImage);
    }
}