package com.faraz.meditrack;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.faraz.meditrack.databinding.ActivityViewReportsBinding;

public class ViewReports extends AppCompatActivity {
    private ActivityViewReportsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.reportImage.setImageURI(Uri.parse(getIntent().getStringExtra("reportUri")));
    }
}