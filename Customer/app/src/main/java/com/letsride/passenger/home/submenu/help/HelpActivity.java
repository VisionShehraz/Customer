package com.letsride.passenger.home.submenu.help;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.UserService;
import com.letsride.passenger.config.General;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.menu.HelpRequestJson;
import com.letsride.passenger.model.json.menu.HelpResponseJson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.help_title)
    TextView helpTitle;
    @BindView(R.id.help_subject)
    TextView helpSubject;
    @BindView(R.id.help_description)
    TextView helpDescription;
    @BindView(R.id.send_help_request)
    Button sendHelpRequest;

    Context context;
    private int titleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHelpTitle(getIntent().getIntExtra("id", -1));

        sendHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpSubject.getText().toString().length() > 0 && helpDescription.getText().toString().length() > 0) {
                    Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show();
                    User loginUser = GoTaxiApplication.getInstance(HelpActivity.this).getLoginUser();
                    HelpRequestJson request = new HelpRequestJson();
                    request.id_pelanggan = loginUser.getId();
                    request.subject = helpSubject.getText().toString();
                    request.description = helpDescription.getText().toString();
                    request.type = titleId + "";

                    UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
                    service.sendHelp(request).enqueue(new Callback<HelpResponseJson>() {
                        @Override
                        public void onResponse(Call<HelpResponseJson> call, Response<HelpResponseJson> response) {
                            if (response.isSuccessful()) {
                                if (response.body().mesage.equals("success")) {
                                    helpSubject.setText("");
                                    helpDescription.setText("");
                                    View view = HelpActivity.this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(HelpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<HelpResponseJson> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(HelpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }


    private void setHelpTitle(int id) {
        String titile = "";
        titleId = id;
        switch (id) {
            case 0:
                titile = "Go-Car";
                break;
            case 1:
                titile = "Go-Ride";
                break;
            case 2:
                titile = "Go-Send";
                break;
            case 3:
                titile = "Go-Box";
                break;
            case 4:
                titile = "Go-Massage";
                break;
            case 5:
                titile = "Go-Food";
                break;
            case 6:
                titile = "Go-Service";
                break;
            default:
                titile = "Go-Taxi";
                break;

        }
        helpTitle.setText(titile);
    }




}
