package com.aniketjain.automaticotpfirebase.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aniketjain.automaticotpfirebase.databinding.ActivityMobileNumberBinding;

public class MobileNumberActivity extends AppCompatActivity {

    private ActivityMobileNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binding
        binding = ActivityMobileNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // merge country code picker with mobile number
        binding.ccp.registerCarrierNumberEditText(binding.mobileNumberEt);

        setOnClickListeners();

    }

    private void setOnClickListeners() {
        binding.sendOtpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageOtpActivity.class);
            intent.putExtra("mobile", binding.ccp.getFullNumberWithPlus().replace(" ", ""));
            startActivity(intent);
            finish();
        });
    }
}