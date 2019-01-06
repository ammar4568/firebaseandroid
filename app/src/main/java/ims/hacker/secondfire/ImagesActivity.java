package ims.hacker.secondfire;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ims.hacker.secondfire.Adapters.ImageAdapter;

public class ImagesActivity extends AppCompatActivity {

    private static final String TAG = "cyb";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<String> imageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private final static int NUM_OF_COLUMNS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ImageAdapter(imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_OF_COLUMNS));
        recyclerView.setAdapter(adapter);

        db.collection("users")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("root")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("path"));
                                imageList.add(document.getData().get("path").toString());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}
