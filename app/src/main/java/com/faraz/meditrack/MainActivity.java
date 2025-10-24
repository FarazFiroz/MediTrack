package com.faraz.meditrack;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.faraz.meditrack.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final int REQUEST_IMAGE = 100;
    private Uri cameraImageUri;
    private SharedPreferences tinyDb;
    private SharedPreferences.Editor editor;
    private ActivityResultLauncher<Intent> imageChooserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        tinyDb = getSharedPreferences("reports", MODE_PRIVATE);
        editor = tinyDb.edit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();

        imageChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                Uri imageUri;

                if(data != null && data.getData() != null){
                    imageUri = data.getData();
                } else {
                    imageUri = cameraImageUri;
                }

                if(imageUri != null) showAddReportDialog(imageUri);
            }
        });


        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                int id = menuItem.getItemId();
                if(id == R.id.nav_home){
                    selectedFragment = new HomeFragment();
                } else if(id == R.id.nav_add) {
                    openImageChooser();
                }

                if(selectedFragment!=null){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }

    private Uri createImageFileUri() throws IOException {
        String name = "report_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(name, ".jpg", storageDir);

        return FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", imageFile);
    }

    private void storeData(Uri image, String title){
        ReportDatabase database = Room.databaseBuilder(this, ReportDatabase.class, "my_reports").build();
        ReportsDao dao = database.reportsDao();

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Report report = new Report(title, image.toString(), date);
        new Thread(() -> {
            dao.insert(report);
        }).start();
        int reportNo = Integer.valueOf(tinyDb.getString("reportNo", "1")) + 1;
        editor.putString("reportNo", String.valueOf(reportNo));
        Toast.makeText(this, "Report added", Toast.LENGTH_SHORT).show();
    }

    private void openImageChooser() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraImageUri = createImageFileUri();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        Intent selectPhoto = new Intent(Intent.ACTION_PICK);
        selectPhoto.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(selectPhoto, "Capture or Select an Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePhoto});
        imageChooserLauncher.launch(chooser);
    }

    private void showAddReportDialog(Uri imageUri) {
        View view = getLayoutInflater().inflate(R.layout.add_report_dialog, null);
        EditText reportTitleBox = view.findViewById(R.id.addReportTitleBox);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add Report")
                .setMessage("Enter the title for this report")
                .setCancelable(false)
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String reportTitle = reportTitleBox.getText().toString();
                    if(reportTitle.isEmpty()) reportTitle = "com.faraz.meditrack.Report " + tinyDb.getString("reportNo", "1");

                    storeData(imageUri, reportTitle);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if(cameraImageUri != null){
            outState.putString("cameraImageUri", cameraImageUri.toString());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("cameraImageUri")){
            cameraImageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}