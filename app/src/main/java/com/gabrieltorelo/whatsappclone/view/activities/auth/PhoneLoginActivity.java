package com.gabrieltorelo.whatsappclone.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityPhoneLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener
{

    private ActivityPhoneLoginBinding binding;
    private static String TAG = "PhoneLoginActivity";
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    String[] country = {"Brazil","China","India","Japan","USA","Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);

        Spinner spin = findViewById(R.id.spinner_country);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(aa);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(this, SetUserInfoActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnNext.getText().toString().equals("Próximo")){
                    progressDialog.setMessage("Por favor aguarde...");
                    progressDialog.show();

                    String phone = "+"+binding.editCodeCountry.getText().toString()+binding.editPhone.getText().toString();
                    startPhoneNumberVerification(phone);
                }else {
                    progressDialog.setMessage("Verificando código...");
                    progressDialog.show();
                    verifyPhoneNumberWithCode(mVerificationId, binding.editCode.getText().toString());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Verificação Completa");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed: Verificação Falhou");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;

                binding.editCode.setVisibility(View.VISIBLE);
                binding.btnNext.setText("Confirmar");
                binding.editCodeCountry.setEnabled(false);
                binding.spinnerCountry.setEnabled(false);
                binding.editPhone.setEnabled(false);
                binding.plus.setEnabled(false);
                progressDialog.dismiss();
            }
        };
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential: CODE SUCCESS");

                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
                        }
                        else {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d(TAG, "onComplete: CODE ERROR");
                            }
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String myStr = binding.spinnerCountry.getSelectedItem().toString();

        switch (myStr){
            case "Brazil" :
                binding.editCodeCountry.setText("55");
                binding.editCodeCountry.setFocusable(false);
                break;
            case "China" :
                binding.editCodeCountry.setText("86");
                binding.editCodeCountry.setFocusable(false);
                break;
            case "India" :
                binding.editCodeCountry.setText("91");
                binding.editCodeCountry.setFocusable(false);
                break;
            case "Japan" :
                binding.editCodeCountry.setText("81");
                binding.editCodeCountry.setFocusable(false);
                break;
            case "USA" :
                binding.editCodeCountry.setText("1");
                binding.editCodeCountry.setFocusable(false);
                break;
            default:
                binding.editCodeCountry.setText("");
                binding.editCodeCountry.setFocusableInTouchMode(true);
        };
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}