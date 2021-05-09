package edu.fordham.snapchatclone;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CameraFragment extends Fragment{

    Camera camera;
    ImageView selectedImage;
    Button mLogout, cameraBtn, galleryBtn, storyBtn, findUsers;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference;

    String fileName;
    Uri fileUrl;

    final int CAMERA_PERM_CODE = 1;
    Uri content_uri;
    final int CAMERA_REQUEST_CODE = 2;
    final int GALLERY_REQUEST_CODE = 3;
    public static  CameraFragment getInstance(){
        CameraFragment cameraFragment = new CameraFragment();
        return cameraFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);

        storageReference = FirebaseStorage.getInstance().getReference();
        selectedImage = view.findViewById(R.id.selectedImage);
        cameraBtn = view.findViewById(R.id.camera);
        findUsers = view.findViewById(R.id.findUsers);
        galleryBtn = view.findViewById(R.id.gallery);
        storyBtn = view.findViewById(R.id.story);

        findUsers.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                findUsers();
            }
        });
        storyBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getActivity().getApplicationContext(),ChooseReceiverActivity.class);
               intent.putExtra("filname",fileName);
               intent.putExtra("fileUri",fileUrl.toString());
                startActivity(intent);
                return;
                //uploadImageToFirebase(fileName, fileUrl);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Camera Button Clicked",Toast.LENGTH_LONG).show();
                askCameraPermissions();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Gallery Button Clicked",Toast.LENGTH_LONG).show();
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        mLogout = view.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Logout();
            }
        });

        return view;

    }



    //check if permission is already given by app
    private void askCameraPermissions() {
        //check if authenticated by app if not then request permissions
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            //openCamera();
            dispatchTakePictureIntent();
        }
    }

    //use media store class to open
    private void openCamera() {
        Toast.makeText(getContext(),"Camera open request",Toast.LENGTH_SHORT).show();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //openCamera();
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getContext(),"Camera Permission Required to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //called after Camera Intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            //sets image to imageview
            /*
            Bitmap image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);*/
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.i("image","Absolute URL of Image is " + Uri.fromFile(f));

                //invoking system scanner to add photo to Media providers database
                //to make to available in Android Gallery application
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.getActivity().sendBroadcast(mediaScanIntent);
                fileName = f.getName();
                fileUrl = content_uri;

               // uploadImageToFirebase(f.getName(),contentUri);


            }
        }
        if(requestCode == GALLERY_REQUEST_CODE){
            //sets image to imageview
            /*
            Bitmap image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);*/
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                Log.i("image","onActibityResult: Gallery Image Uri: " + imageFileName);
                selectedImage.setImageURI(contentUri);
               // uploadImageToFirebase(imageFileName,contentUri);
                fileName = imageFileName;
                fileUrl = contentUri;
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri content_uri) {
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("story");
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
                            Picasso.get().load(uri).into(selectedImage);
                            Long currentTimestamp = System.currentTimeMillis();
                            Long endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000);

                            Map<String, Object> mapToUpload = new HashMap<>();
                            mapToUpload.put("imageUrl", uri.toString());
                            mapToUpload.put("timestampBeg", currentTimestamp);
                            mapToUpload.put("timestampEnd", endTimestamp);
                            userStoryDb.child(key).setValue(mapToUpload);

                        }
                    });
                    Toast.makeText(getContext(), "Image is Uplaoded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExt(Uri contentUri) {
        //get extension of image
        ContentResolver c = getActivity().getContentResolver();
        //use map
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    //Open camera and save image
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //create image file
        String imageFileName = "JPEG_" + timeStamp + "_";

    // File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

      //Unlike previous one updates into gallery
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "edu.fordham.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void findUsers() {
        Intent intent = new Intent(getContext(),FindUsersActivity.class);
        startActivity(intent);
        return;
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(),ChooseLoginRegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }
}
