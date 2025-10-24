package com.faraz.meditrack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.faraz.meditrack.databinding.FragmentHomeBinding;
import com.faraz.meditrack.databinding.FragmentReportsBinding;

import java.util.List;

public class ReportsFragment extends Fragment {
    private FragmentReportsBinding binding;
    public ReportsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);

        binding.reportsRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new Thread(() -> {
            ReportDatabase database = Room.databaseBuilder(getContext(), ReportDatabase.class, "reports").build();
            ReportsDao dao = database.reportsDao();
            List<Report> reports = dao.getAllReports();

            if(isAdded() && binding!=null) {
                requireActivity().runOnUiThread(() -> {
                    adapter = new ReportsAdapter(getContext(), reports);
                    binding.reportsRecylerView.setAdapter(adapter);
                });
            }
        }).start();

        binding.searchReportBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.filter(charSequence.toString());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}