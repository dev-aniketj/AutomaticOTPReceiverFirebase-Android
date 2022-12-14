package com.aniketjain.automaticotpfirebase.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aniketjain.automaticotpfirebase.receiver.OTP_Receiver;
import com.aniketjain.automaticotpfirebase.databinding.ActivityManageOtpBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ManageOtpActivity extends AppCompatActivity {

    private ActivityManageOtpBinding binding;

    private String mobileNumber;
    private String otpID;           // using this variable when SIM is not in the Device.
    //private OTP_Receiver otp_receiver;

    private FirebaseAuth mAuth;

    EditText et_otpNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get data from mobile number activity using Intent
        mobileNumber = getIntent().getStringExtra("mobile");

        // firebase get instance
        mAuth = FirebaseAuth.getInstance();

        // binding
        binding = ActivityManageOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initiateOtp();      // sending request to firebase for the otp

        setOnClickListeners();

        requestSMSPermission();

        et_otpNumber = binding.otpEt;
        new OTP_Receiver().setEditText(et_otpNumber);

//        autoOtpReceiver();
    }

    private void initiateOtp() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobileNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                otpID = s;
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ManageOtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(ManageOtpActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        binding.otpEt.setError("Invalid Otp");
                    }
                });
    }

    private void requestSMSPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void setOnClickListeners() {

        // when user click on otp button
        binding.verifyOtpBtn.setOnClickListener(v -> {
            // when user is using different SIM, which is not insert in their phone.
            if (binding.otpEt.getText().toString().isEmpty()) {
                binding.otpEt.setError("Blank Field can not be processed");
            } else if (binding.otpEt.getText().toString().length() != 6) {
                binding.otpEt.setError("Invalid Otp");
            } else {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otpID, binding.otpEt.getText().toString());
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        });

    }
}