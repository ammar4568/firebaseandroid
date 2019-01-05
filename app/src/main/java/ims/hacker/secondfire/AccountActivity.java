package ims.hacker.secondfire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {
    Button signout,imagebtn;
    TextView welcome, email;
    int GALLERY_INTENT = 2;
    EditText emailedt;
    FirebaseStorage storage;
    StorageReference mStorage;
    FirebaseAuth firebaseAuth;
    private static String TAG = "cyb";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button mAllImagesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Firebase References
        storage = FirebaseStorage.getInstance();
        mStorage = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        signout= (Button) findViewById(R.id.signout);
        email=(TextView) findViewById(R.id.email);
        emailedt=(EditText)findViewById(R.id.emailedt);
        imagebtn = (Button) findViewById(R.id.imagebtn);
        mAllImagesButton = findViewById(R.id.viewAllImages);
//        Log.i("cyb", "onCreate: " + firebaseAuth.getCurrentUser().getEmail());
        email.setText(firebaseAuth.getCurrentUser().getEmail());
        signout.setOnClickListener(this);
        imagebtn.setOnClickListener(this);
        mAllImagesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AccountActivity.this, MainActivity.class));
        }
        if (view.getId()==R.id.imagebtn)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String pictureDirectoryPath = pictureDirectory.getPath();
            Uri data = Uri.parse(pictureDirectoryPath);
            intent.setDataAndType(data, "image/*");

            startActivityForResult(intent, GALLERY_INTENT);

            StorageReference filepath = mStorage.child("Photos").child(data.getLastPathSegment());
            filepath.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AccountActivity.this, "Upload Done ", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (view.getId() == R.id.viewAllImages) {
            Intent imageActivityIntent = new Intent(AccountActivity.this, ImagesActivity.class);
            startActivity(imageActivityIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GALLERY_INTENT) {
            //TODO: action
            Uri uri = data.getData();
            File f = new File("" + uri);
            Log.i(TAG, "onActivityResult: " + f.getName());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                final StorageReference uploadRef = mStorage.child("images/" + firebaseAuth.getCurrentUser().getEmail() + "/" + f.getName()+ ".jpg");


                UploadTask uploadTask = uploadRef.putBytes(imageData);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return uploadRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.i(TAG, "onComplete: " + downloadUri);
                            Map<String, String> data = new HashMap<>();
                            data.put("path",downloadUri.toString());

                            db.collection("users")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .collection("root")
                                    .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AccountActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
