package com.faraz.meditrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.faraz.meditrack.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;

import java.util.HashSet;

public class HomeFragment extends Fragment {

    private HashSet<String> allergies;
    private FragmentHomeBinding binding;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private String[] genders = {"Male", "Female"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initViews();

        return view;
    }

    private void initViews(){
        data = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        editor = data.edit();
        allergies = new HashSet<>(data.getStringSet("allergies", new HashSet<>()));
        loadUserData();

        binding.genderDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.gender_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.male){
                        editor.putString("gender", "Male");
                        editor.apply();
                        loadUserData();
                        return true;
                    } else if (item.getItemId() == R.id.female) {
                        editor.putString("gender", "Female");
                        editor.apply();
                        loadUserData();
                        return true;
                    } else return true;
                });
                popupMenu.show();
            }
        });

        binding.bloodGrpDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.bloodgroup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.aPos) binding.bloodGrpTxt.setText("A+");
                        else if (id == R.id.aNeg) binding.bloodGrpTxt.setText("A-");
                        else if (id == R.id.bPos) binding.bloodGrpTxt.setText("B+");
                        else if (id == R.id.bNeg) binding.bloodGrpTxt.setText("B-");
                        else if (id == R.id.abPos) binding.bloodGrpTxt.setText("AB+");
                        else if (id == R.id.abNeg) binding.bloodGrpTxt.setText("AB-");
                        else if (id == R.id.oPos) binding.bloodGrpTxt.setText("O+");
                        else if (id == R.id.oNeg) binding.bloodGrpTxt.setText("O-");
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to update your data")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            String name = binding.nameBox.getText().toString();
                            editor.putString("name", !name.isEmpty() ? name:binding.nameBox.getHint().toString());

                            String age = binding.ageBox.getText().toString();
                            editor.putString("age", !age.isEmpty() ? age:binding.ageBox.getHint().toString());

                            String height = binding.heightBox.getText().toString();
                            editor.putString("height", !height.isEmpty() ? height:binding.heightBox.getHint().toString());

                            String weight = binding.weightBox.getText().toString();
                            editor.putString("weight", !weight.isEmpty() ? weight:binding.weightBox.getHint().toString());

                            String email = binding.emailBox.getText().toString();
                            editor.putString("email", !email.isEmpty() ? email:binding.emailBox.getHint().toString());

                            String phoneNumber = binding.phoneBox.getText().toString();
                            if(!phoneNumber.isEmpty() && phoneNumber.length() != 10) {
                                Toast.makeText(getContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            editor.putString("phoneNumber", !phoneNumber.isEmpty() ? phoneNumber:binding.phoneBox.getHint().toString());

                            editor.putString("bloodGroup", binding.bloodGrpTxt.getText().toString());

                            editor.apply();
                            loadUserData();
                            clearEditTextFocus();

                            Toast.makeText(getContext(), "Data updated succesfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialog, which) -> {})
                        .setCancelable(true)
                        .show();
            }
        });

        binding.addAllergyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.edttxt_enter_allergy, null);
                EditText input = view.findViewById(R.id.allergyBox);
                new AlertDialog.Builder(getContext())
                        .setTitle("Add allergy")
                        .setView(view)
                        .setPositiveButton("Add", (dialog, which) -> {
                            String text = input.getText().toString();
                            if(!text.isEmpty()){
                                allergies.add(text);
                                editor.putStringSet("allergies", allergies);
                                editor.apply();
                                loadUserData();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {})
                        .setCancelable(false)
                        .show();
            }
        });
    }

    public String getBmiCategory(double bmi){
        if(bmi < 18) return "Underweight";
        else if(bmi < 25) return "Normal";
        else if(bmi < 30) return "Overweight";
        else return "Obese";
    }

    public void loadUserData(){
        String userName = data.getString("name", "Faraz Firoz");
        binding.name.setText(userName);
        binding.nameBox.setText("");
        binding.nameBox.setHint(userName);

        String gender = data.getString("gender", "Male");
        binding.genderTxt.setText(gender);

        String userAge = data.getString("age", "17");
        binding.age.setText(userAge + " Years");
        binding.ageBox.setText("");
        binding.ageBox.setHint(userAge);

        String height = data.getString("height", "170");
        binding.heightBox.setText("");
        binding.heightBox.setHint(height);

        String weight = data.getString("weight", "55");
        binding.weightBox.setText("");
        binding.weightBox.setHint(weight);

        String email = data.getString("email", "farazfiroz2472@gmail.com");
        binding.emailBox.setText("");
        binding.emailBox.setHint(email);

        String phoneNumber = data.getString("phoneNumber", "8271758484");
        binding.phoneBox.setText("");
        binding.phoneBox.setHint(phoneNumber);

        String bloodGroup = data.getString("bloodGroup", "O+");
        binding.bloodGrpTxt.setText(bloodGroup);

        double heightMeter = Integer.valueOf(height) / 100.0;
        double bmi = Integer.valueOf(weight) / (heightMeter * heightMeter);
        binding.bmi.setText(String.format("%.2f", bmi));
        binding.bmiCategory.setText(getBmiCategory(bmi));

        binding.allergyChipgroup.removeAllViews();
        for(String s:allergies){
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setChipStrokeWidth(0f); chip.setChipBackgroundColorResource(R.color.background);
            chip.setTextColor(ContextCompat.getColor(getContext(), R.color.subheading_color));
            int radius = (int) (20 * getResources().getDisplayMetrics().density);
            chip.setChipCornerRadius(radius);
            chip.setCloseIconResource(R.drawable.close);
            chip.setCloseIconVisible(true);
            chip.setCloseIconTintResource(R.color.subheading_color);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Chip chip = (Chip) view;
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Confirmation")
                            .setMessage("Are you sure you want to delete this allergy?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                allergies.remove(chip.getText());
                                editor.putStringSet("allergies", allergies);
                                editor.apply();
                                loadUserData();

                                Toast.makeText(getContext(), "Allergy deleted succesfully", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", (dialog, which) -> {})
                            .setCancelable(true)
                            .show();
                }
            });
            binding.allergyChipgroup.addView(chip);
        }
    }

    public void clearEditTextFocus(){
        binding.nameBox.clearFocus();
        binding.ageBox.clearFocus();
        binding.heightBox.clearFocus();
        binding.weightBox.clearFocus();
        binding.emailBox.clearFocus();
        binding.phoneBox.clearFocus();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}