package com.letsride.passenger.home.submenu.setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.UserService;
import com.letsride.passenger.config.General;
import com.letsride.passenger.model.FirebaseToken;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.user.ChangePasswordRequestJson;
import com.letsride.passenger.model.json.user.ChangePasswordResponseJson;
import com.letsride.passenger.model.json.user.LoginRequestJson;
import com.letsride.passenger.model.json.user.LoginResponseJson;
import com.letsride.passenger.utils.DialogActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends DialogActivity {
    @BindView(R.id.current_password)
    EditText currentpassword;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_new_password)
    EditText confirmNewPassword;
    @BindView(R.id.change_password)
    Button changePassword;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        user = GoTaxiApplication.getInstance(this).getLoginUser();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog("Loading...");
                ChangePasswordRequestJson request = new ChangePasswordRequestJson();
                request.id = user.getId();
                request.email = user.getEmail();
                request.current_password = currentpassword.getText().toString();
                request.new_password = newPassword.getText().toString();

                UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
                service.changePassword(request).enqueue(new Callback<ChangePasswordResponseJson>() {
                    @Override
                    public void onResponse(Call<ChangePasswordResponseJson> call, Response<ChangePasswordResponseJson> response) {
                        if (response.isSuccessful()) {
                            if (response.body().message.equals("success")) {
                                hideProgressDialog();
                                update_data();
                                Toast.makeText(ChangePasswordActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                            } else {
                                String message = response.body().message;
                                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChangePasswordResponseJson> call, Throwable t) {
                        t.printStackTrace();
                        hideProgressDialog();
                        Toast.makeText(ChangePasswordActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void update_data() {
        showProgressDialog(R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(user.getEmail());
        request.setPassword(newPassword.getText().toString());

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

                        Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(ChangePasswordActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        GoTaxiApplication.getInstance(ChangePasswordActivity.this).setLoginUser(user);
    }

}
