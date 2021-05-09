package edu.fordham.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseReceiverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String fileName;
    Uri fileUrl;
    String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        mRecyclerView = findViewById(R.id.recyclerViewSendSnaps);
        mRecyclerView.getRecycledViewPool().clear();
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReceiverAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("filname");
        fileUrl = Uri.parse(intent.getStringExtra("fileUri"));
        FloatingActionButton mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase(fileName,fileUrl);
            }
        });
    }

    private ArrayList<ReceiveObject> results = new ArrayList<>();
    private ArrayList<ReceiveObject> getDataSet() {
        listenForData();
        return results;
    }

    private void uploadImageToFirebase(String name, Uri content_uri) {
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(current_user).child("story");
        final String key = userStoryDb.push().getKey();
        final StorageReference image = storageReference.child("captures/" + name).child(key);
        if(content_uri!=null) {
            image.putFile(content_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("image", "OnSuccess: Uploaded Image URL is " + uri.toString());
                          //  Picasso.get().load(uri).into(selectedImage);
                            Long currentTimestamp = System.currentTimeMillis();
                            Long endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000);

                            CheckBox mStory = findViewById(R.id.story);
                            if(mStory.isChecked()){
                                Map<String, Object> mapToUpload = new HashMap<>();
                                mapToUpload.put("imageUrl", uri.toString());
                                mapToUpload.put("timestampBeg", currentTimestamp);
                                mapToUpload.put("timestampEnd", endTimestamp);
                                userStoryDb.child(key).setValue(mapToUpload);
                            }

                            for(int i = 0; i < results.size();i++){
                                if(results.get(i).getReceive()){
                                    DatabaseReference userDb = FirebaseDatabase.getInstance()
                                            .getReference().child("users").child(results.get(i).getUid()).child("received").child(current_user);
                                    Map<String, Object> mapToUpload = new HashMap<>();
                                    mapToUpload.put("imageUrl", uri.toString());
                                    mapToUpload.put("timestampBeg", currentTimestamp);
                                    mapToUpload.put("timestampEnd", endTimestamp);
                                    userDb.child(key).setValue(mapToUpload);
                                }
                            }

                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);



                        }
                    });
                    Toast.makeText(getApplicationContext(), "Image is Uplaoded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void listenForData() {
        for(int i = 0; i < UserInformation.listFollowing.size();i++){
            DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            usersDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = "";
                    String uid = snapshot.getRef().getKey();
                    if(snapshot.child("email").getValue() != null){
                        email = snapshot.child("email").getValue().toString();
                    }
                    ReceiveObject obj = new ReceiveObject(email,uid,false);
                    if(!results.contains(obj)){
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}