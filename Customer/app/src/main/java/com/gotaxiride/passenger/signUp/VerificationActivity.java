package com.gotaxiride.passenger.signUp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.config.General;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gotaxiride.passenger.signUp.SignUpActivity.NomorHP;


public class VerificationActivity extends AppCompatActivity implements OnClickListener, Validator.ValidationListener{

    public static final int SIGNUP_ID = 110;
    //Variable Untuk Komponen-komponen Yang Diperlukan
    private EditText SetKode;
    private Button Masuk;
    AppCompatButton Verifikasi;
    TextView Resend;
    Validator validator;

    // Button test;

    @NotEmpty
    @BindView(R.id.phone)
    EditText NoTelepon;

    @BindView(R.id.please_input_code)
    TextView pleaseInputCode;

    private TextView PhoneID;

    //Variables Needed for Authentication
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener stateListener;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String VerifikasiID;
    private String No_Telepon;

    private TextView Waktu;
    private  TimerClass timerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        Waktu = (TextView) findViewById(R.id.timer);
        PhoneID = (TextView) findViewById(R.id.no_id);
        SetKode = (EditText) findViewById(R.id.setVertifi);
        Masuk = (Button) findViewById(R.id.login);
        Masuk.setOnClickListener(this);
        Verifikasi = (AppCompatButton) findViewById(R.id.verifi);
        Verifikasi.setOnClickListener(this);
        Resend = (TextView) findViewById(R.id.resend);
        Resend.setOnClickListener(this);
        Resend.setEnabled(false);

        PhoneID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        timerClass = new TimerClass(60000 * 1, 1000);
        hidden();




        //    test = (Button) findViewById(R.id.test);
        //   test.setVisibility(View.GONE);

    /*    test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(VerificationActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });  */

        Masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        //Connecting Projects with Firebase Authentication
        auth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //If There Is, The User Does Not Need to Login Again, and Directly Goes to the Acivity Heading
                    //         startActivity(new Intent(VerificationActivity.this, SignUpActivity.class));
                    //        finish();
                }
            }
        };




    }

    private void send(){
        Intent intent = new Intent(VerificationActivity.this, ListCountryActivity.class);
        startActivityForResult(intent, ListCountryActivity.LIST_COUNTRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ListCountryActivity.LIST_COUNTRY) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getStringExtra("key");
                PhoneID.setText(code);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Attach Listener to FirebaseAuth when Activity Starts
        auth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(stateListener != null){
        }
    }

    private void setupVerificationCallback(){
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                VerifikasiID = verificationId;
                resendToken = token;
                Resend.setEnabled(true);
                Toast.makeText(getApplicationContext(), "The code has been sent to your number" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential Credential) {
                // Callbacks here will be called when the Verification is Successful or Successful
                Toast.makeText(getApplicationContext(), "Phone number verification has succeeded", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(Credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // The callback here will be called when the request is invalid or there is an error
                Toast.makeText(getApplicationContext(), "Verification Fails, Please Try Again", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Sign In Successful
                            Intent intent  = new Intent(VerificationActivity.this, SignUpActivity.class);
                            intent.putExtra(NomorHP, NoTelepon.getText().toString());
                            startActivity(intent);
                            finish();
                        }else{
                            //Sign In Failed
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                // No Valid Entered Code.
                                Toast.makeText(getApplicationContext(), "Invalid code entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.verifi:
                String verifiCode = SetKode.getText().toString();
                if(TextUtils.isEmpty(verifiCode)){
                    Toast.makeText(getApplicationContext(),"Enter the Verification Code", Toast.LENGTH_SHORT).show();
                }else{

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifikasiID, verifiCode);
                    signInWithPhoneAuthCredential(credential);
                    Toast.makeText(getApplicationContext(),"While Doing the Verification Process", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.resend:
                No_Telepon = PhoneID.getText()+NoTelepon.getText().toString();
                setupVerificationCallback();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        No_Telepon, //NO Telepon Untuk Di Vertifikai
                        120, //Durasi Waktu Habis
                        TimeUnit.SECONDS, //Unit Timeout
                        this, //Activity
                        callbacks, // OnVerificationStateChangedCallbacks
                        resendToken); // Digunakan untuk mengirim ulang kembali kode verifikasi
                Toast.makeText(getApplicationContext(), "Resend verification code", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void onSendInClick (){
        String verifiCode = NoTelepon.getText().toString();
        if(TextUtils.isEmpty(verifiCode)) {
            Toast.makeText(getApplicationContext(), "Enter digits of your phone number", Toast.LENGTH_SHORT).show();
        }
        else {
            No_Telepon = PhoneID.getText() + NoTelepon.getText().toString();
            setupVerificationCallback();
            // fungsi timer
            timerClass.start();
            show();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    No_Telepon, //Phone Number to Verify
                    60, //Duration of Time Out
                    TimeUnit.SECONDS, //Unit Timeout
                    this, //Activity
                    callbacks); // OnVerificationStateChangedCallbacks
            Toast.makeText(getApplicationContext(), "Verification Process, Please Wait", Toast.LENGTH_SHORT).show();
            NoTelepon.setText("");
        }
    }


    //Make InnerClass for Countdown Time configuration
    public class TimerClass extends CountDownTimer {

        TimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //This method runs when the time / timer changes
        @Override
        public void onTick(long millisUntilFinished) {
            //Time Format configuration used
            String waktu = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

            //Display it on TexView
            Waktu.setText("Code can be sent after " +waktu);
        }

        @Override
        public void onFinish() {
            Resend.setVisibility(View.VISIBLE);
            Waktu.setVisibility(View.GONE);
            ///Walk when the time has finished or stop
            Toast.makeText(VerificationActivity.this, "You can request a verification code", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onValidationSucceeded() {
        onSendInClick();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void show(){
        Waktu.setVisibility(View.VISIBLE);
        pleaseInputCode.setVisibility(View.VISIBLE);
        SetKode.setVisibility(View.VISIBLE);
        Verifikasi.setVisibility(View.VISIBLE);
        Masuk.setVisibility(View.GONE);
        PhoneID.setVisibility(View.GONE);
        NoTelepon.setVisibility(View.GONE);
    }

    private void hidden(){
        Waktu.setVisibility(View.GONE);
        pleaseInputCode.setVisibility(View.GONE);
        SetKode.setVisibility(View.GONE);
        Verifikasi.setVisibility(View.GONE);
        Masuk.setVisibility(View.VISIBLE);
        PhoneID.setVisibility(View.VISIBLE);
        NoTelepon.setVisibility(View.VISIBLE);

    }
}
