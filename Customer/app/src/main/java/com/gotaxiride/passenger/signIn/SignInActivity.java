package com.gotaxiride.passenger.signIn;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.signUp.VerificationActivity;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.home.ForgotPasswordActivity;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.model.FirebaseToken;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.user.LoginRequestJson;
import com.gotaxiride.passenger.model.json.user.LoginResponseJson;
import com.gotaxiride.passenger.signUp.SignUpActivity;
import com.gotaxiride.passenger.utils.DialogActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 10/12/2018.
 */

public class SignInActivity extends DialogActivity implements Validator.ValidationListener {

    private static final String TAG = "SignInActivity";

    @NotEmpty
    @Email
    @BindView(R.id.signIn_email)
    EditText textEmail;

    @NotEmpty
    @BindView(R.id.signIn_password)
    EditText textPassword;

    @BindView(R.id.signIn_signInButton)
    Button buttonSignIn;

   // @BindView(R.id.signIn_signUpButton)
   // LinearLayout buttonSignUp;

    @BindView(R.id.sign_up)
    TextView buttonSignUp;

    @BindView(R.id.forgot_password)
    TextView forgot;

    @BindView(R.id.imageView_Day)
    ImageView imageDay;
    @BindView(R.id.textView_Day)
    TextView textDay;

    Validator validator;
    private Locale locale;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_day);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, VerificationActivity.class);
                startActivityForResult(intent, VerificationActivity.SIGNUP_ID);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity (intent);
            }
        });

// cange lauange
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = settings.getString("LANG", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        findViewById(R.id.changeLang).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showChangeLangDialog();
            }
        });

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay>=0 && timeOfDay<11){
            textDay.setText("Morning");
            imageDay.setImageResource(R.drawable.good_morning_img);
        } else if (timeOfDay>=11 && timeOfDay<18){
            textDay.setText("Afternoon");
            imageDay.setImageResource(R.drawable.good_morning_img);
        } else if (timeOfDay>=18 && timeOfDay<24){
            textDay.setText("Night");
            imageDay.setImageResource(R.drawable.good_night_img);
        }

    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(getResources().getString(R.string.lang_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.lang_dialog_message));
        dialogBuilder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                changeLang(langpos);
            }
        });
        dialogBuilder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    void changeLang(int langpos){
        switch (langpos) {
            case 0: //Arabic
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ar").commit();
                setLangRecreate("ar");
                return;
            case 1: //Spanish
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                setLangRecreate("es");
                return;
            case 2: //French
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "fr").commit();
                setLangRecreate("fr");
                return;
            case 3: //Portuguese
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "pt").commit();
                setLangRecreate("pt");
                return;

            default: //By default set to english
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
                return;
        }
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }



    private void onSignInClick() {
        showProgressDialog(R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(textEmail.getText().toString());
        request.setPassword(textPassword.getText().toString());

        Realm realm = Realm.getDefaultInstance();
        FirebaseToken token = realm.where(FirebaseToken.class).findFirst();
        if (token.getTokenId() != null) {
            request.setRegId(token.getTokenId());
        } else {
            Toast.makeText(this, R.string.waiting_pleaseWait, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return;
        }

        UserService service = ServiceGenerator.createService(UserService.class, request.getEmail(), request.getPassword());
        service.login(request).enqueue(new Callback<LoginResponseJson>() {
            @Override
            public void onResponse(Call<LoginResponseJson> call, Response<LoginResponseJson> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equalsIgnoreCase("found")) {
                        User user = response.body().getData().get(0);

                        saveUser(user);

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, R.string.incorrect, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(SignInActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.SIGNUP_ID) {
            if (resultCode == Activity.RESULT_OK) {
                User user = (User) data.getSerializableExtra(SignUpActivity.USER_KEY);

                saveUser(user);

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        onSignInClick();
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

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        GoTaxiApplication.getInstance(SignInActivity.this).setLoginUser(user);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirebaseToken response) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FirebaseToken.class);
        realm.copyToRealm(response);
        realm.commitTransaction();
    }
}
