package com.gotaxiride.driver.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

//import net.gumcode.drivermangjek.preference.UserPreference;


public class WithdrawFragment extends Fragment {
    private static final String TAG = WithdrawFragment.class.getSimpleName();
    MainActivity activity;
    Driver driver;
    int maxRetry = 4;
    private View rootView;
    public TextView saldo;

    public WithdrawFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_withdraw, container, false);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.My_withdraw);
        Queries que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        que.closeDatabase();
        initView();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void initView() {
        TextView butSubmit;
        final EditText nominalWithdraw;


        butSubmit = (TextView) rootView.findViewById(R.id.butSubmitW);
        nominalWithdraw = (EditText) rootView.findViewById(R.id.nominalWithdraw);
        saldo = (TextView) rootView.findViewById(R.id.saldoDriver);

        saldo.setText(amountAdapter(driver.deposit));

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (driver.no_rek.equals("")) {
                    Toast.makeText(activity, R.string.update_bank, Toast.LENGTH_LONG).show();
                } else {
                    if (nominalWithdraw.getText().toString().equals("")) {
                        Toast.makeText(activity, R.string.Nominal_withdraw, Toast.LENGTH_SHORT).show();
                    } else {
                        if (Integer.parseInt(nominalWithdraw.getText().toString()) > (driver.deposit - 100)) {
                            Toast.makeText(activity, R.string.Balance_withdraw, Toast.LENGTH_LONG).show();
                        } else {
                            withdrawal(nominalWithdraw.getText().toString());
                        }
                    }
                }
            }
        });
    }
    private String amountAdapter(int amo) {
        return "Available Balance amount : $ " + NumberFormat.getNumberInstance(Locale.US).format(amo);
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    private void withdrawal(final String nominal) {
        final ProgressDialog md1 = showLoading();
        JSONObject jWith = new JSONObject();
        try {
            jWith.put("jumlah", nominal);
            jWith.put("id_driver", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("Update_withdrawal", jWith.toString());
        HTTPHelper.getInstance(activity).withdrawal(jWith, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("success")) {
                        Toast.makeText(activity, R.string.Process_approve, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Withdraw failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                md1.dismiss();
                maxRetry = 4;
            }


            @Override
            public void onFailure(String message) {
                md1.dismiss();
            }

            @Override
            public void onError(String message) {
                if (maxRetry == 0) {
                    md1.dismiss();
                    Toast.makeText(activity, R.string.Connection_problem, Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                } else {
                    withdrawal(nominal);
                    maxRetry--;
                    Log.d("Try_ke_withdraw", String.valueOf(maxRetry));
                    md1.dismiss();
                }
            }
        });
    }
}