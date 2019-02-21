package com.gotaxiride.driver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.fragment.DashboardFragment;
import com.gotaxiride.driver.fragment.DepositFragment;
import com.gotaxiride.driver.fragment.FeedbackFragment;
import com.gotaxiride.driver.fragment.OrderFragment;
import com.gotaxiride.driver.fragment.TransactionHistoryFragment;
import com.gotaxiride.driver.fragment.SettingFragment;
import com.gotaxiride.driver.fragment.WithdrawFragment;
import com.gotaxiride.driver.model.Content;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.model.Kendaraan;
import com.gotaxiride.driver.model.TransactionGoCab;
import com.gotaxiride.driver.network.AsyncTaskHelper;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.preference.VehiclePreference;
import com.gotaxiride.driver.preference.SettingPreference;
import com.gotaxiride.driver.preference.UserPreference;
import com.gotaxiride.driver.service.LocationService;
import com.gotaxiride.driver.service.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 991;
    public boolean statusFragment, ordering = false;
    public TextView saldo, textRating;
    Toolbar toolbar;
    MainActivity activity;
    DrawerLayout drawer;
    //    UserPreference up;
    Driver driver;
    CircularImageView imageDriver;
    Intent service;
    ProgressDialog pd;
    int maxRetry1 = 4;
    int status = -1;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle recv = getIntent().getExtras();
        activity = MainActivity.this;
//        up = new UserPreference(activity);
        Queries que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        que.closeDatabase();

        drawerLayout();

        if (savedInstanceState == null && recv != null) {
            if (recv.getString("SOURCE").equals(MyConfig.orderFragment)) {
                changeFragment(new OrderFragment(), false);
            } else if (recv.getString("SOURCE").equals(MyConfig.dashFragment)) {
                if (recv.getInt("response") == 2) {
                    Toast.makeText(activity, "Transaction Canceled", Toast.LENGTH_LONG).show();
                }
                changeFragment(new DashboardFragment(), false);
            }
        } else {
            if (driver.status == 2 || driver.status == 3) {
                changeFragment(new OrderFragment(), false);
            } else {
                changeFragment(new DashboardFragment(), false);
            }
        }
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        service = new Intent(this, LocationService.class);
        startService(service);

        // change language
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = settings.getString("LANG", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

      //  findViewById(R.id.languange).setOnClickListener(new View.OnClickListener() {
      //      @Override public void onClick(View view) {
     //           showChangeLangDialog();
     //       }
    //    });


    }


    private void drawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navViewLeft = (NavigationView) findViewById(R.id.nav_view);
        navViewLeft.setNavigationItemSelectedListener(this);

        NavigationView navViewRight = (NavigationView) findViewById(R.id.nav_view2);
        navViewRight.setNavigationItemSelectedListener(this);

        int width = getResources().getDisplayMetrics().widthPixels * 9 / 10;
        DrawerLayout.LayoutParams drawRightParam = (DrawerLayout.LayoutParams) navViewRight.getLayoutParams();
        drawRightParam.width = width;
        navViewRight.setLayoutParams(drawRightParam);

        View headerL = navViewLeft.getHeaderView(0);
        View headerR = navViewRight.getHeaderView(0);
        initMenuDrawer(headerL, headerR);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent service = new Intent(this, LocationService.class);
                startService(service);
            } else {
                Toast.makeText(activity, "Failed to use location service..", Toast.LENGTH_SHORT).show();
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImageFromStorage(imageDriver);
    }

    private void initMenuDrawer(View headerL, View headerR) {
        CardView menuRW, menuDeposit, menuWithdraw, menuPerforma,
                menuRating, menuBooking, menuInbox, menuFeedback, menuAccount;

        TextView namaDriver, namaKendaraan, platNomor;
        ImageView mobilAktif;

        final Button butAutoBid;
        ImageView fitMcar, fitMride, fitMsend, fitMbox, fitMmart, fitMmassage, fitMfood, fitMService;
        final Spinner uangBelanja;

        namaDriver = (TextView) headerL.findViewById(R.id.namaDriver);
        namaKendaraan = (TextView) headerL.findViewById(R.id.carName);
        platNomor = (TextView) headerL.findViewById(R.id.carPlat);
        saldo = (TextView) headerL.findViewById(R.id.saldoDriver);
        imageDriver = (CircularImageView) headerL.findViewById(R.id.imageDriver);
        mobilAktif = (ImageView) headerL.findViewById(R.id.mobilAktif);
        menuRW = (CardView) headerL.findViewById(R.id.menu_rw);
        menuDeposit = (CardView) headerL.findViewById(R.id.menu_deposit);
        menuWithdraw = (CardView) headerL.findViewById(R.id.menu_withdraw);
    //  menuPerforma = (CardView) headerL.findViewById(R.id.menu_performa);
        menuRating = (CardView) headerL.findViewById(R.id.menu_rating);
        menuBooking = (CardView) headerL.findViewById(R.id.menu_booking);
        menuInbox = (CardView) headerL.findViewById(R.id.menu_inbox);
        menuFeedback = (CardView) headerL.findViewById(R.id.menu_feedback);
        menuAccount = (CardView) headerL.findViewById(R.id.menu_account);
        textRating = (TextView) headerL.findViewById(R.id.textRating);

        butAutoBid = (Button) headerR.findViewById(R.id.butAutoBid);
        fitMcar = (ImageView) headerR.findViewById(R.id.fitMcar);
        fitMride = (ImageView) headerR.findViewById(R.id.fitMride);
        fitMsend = (ImageView) headerR.findViewById(R.id.fitMsend);
        fitMbox = (ImageView) headerR.findViewById(R.id.fitMbox);
        fitMmart = (ImageView) headerR.findViewById(R.id.fitMMart);
        fitMmassage = (ImageView) headerR.findViewById(R.id.fitMMassage);
        fitMfood = (ImageView) headerR.findViewById(R.id.fitMFood);
        fitMService = (ImageView) headerR.findViewById(R.id.fitMService);
        uangBelanja = (Spinner) headerR.findViewById(R.id.spinMaximal);

        initializeRigthDrawer(butAutoBid);
        SettingPreference sp = new SettingPreference(activity);
        uangBelanja.setSelection(Integer.parseInt(sp.getSetting()[1]));

        uangBelanja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateUangBelanja(i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switch (driver.job) {
            case 1:
                mobilAktif.setImageResource(R.mipmap.ic_fitur_mride);
                fitMcar.setImageResource(R.mipmap.taxi_black);
                fitMbox.setImageResource(R.mipmap.box_black);
                fitMService.setImageResource(R.mipmap.ac_black);
                fitMmassage.setImageResource(R.mipmap.massage_black);
                break;
            case 2:
                mobilAktif.setImageResource(R.mipmap.ic_fitur_mcar);
                fitMride.setImageResource(R.mipmap.moto_black);
                fitMsend.setImageResource(R.mipmap.send_black);
                fitMmart.setImageResource(R.mipmap.mart_black);
                fitMbox.setImageResource(R.mipmap.box_black);
                fitMService.setImageResource(R.mipmap.ac_black);
                fitMmassage.setImageResource(R.mipmap.massage_black);
                fitMfood.setImageResource(R.mipmap.food_black);
                break;
            case 3:
                mobilAktif.setImageResource(R.mipmap.ic_fitur_mmassage);
                fitMcar.setImageResource(R.mipmap.taxi_black);
                fitMride.setImageResource(R.mipmap.moto_black);
                fitMsend.setImageResource(R.mipmap.send_black);
                fitMmart.setImageResource(R.mipmap.mart_black);
                fitMbox.setImageResource(R.mipmap.box_black);
                fitMService.setImageResource(R.mipmap.ac_black);
                fitMfood.setImageResource(R.mipmap.food_black);
                break;
            case 4:
                mobilAktif.setImageResource(R.mipmap.ic_fitur_mbox);
                fitMcar.setImageResource(R.mipmap.taxi_black);
                fitMride.setImageResource(R.mipmap.moto_black);
                fitMsend.setImageResource(R.mipmap.send_black);
                fitMmart.setImageResource(R.mipmap.mart_black);
                fitMmassage.setImageResource(R.mipmap.massage_black);
                fitMService.setImageResource(R.mipmap.ac_black);
                fitMfood.setImageResource(R.mipmap.food_black);
                break;
            case 5:
                mobilAktif.setImageResource(R.mipmap.ic_fitur_mservice);
                fitMcar.setImageResource(R.mipmap.taxi_black);
                fitMride.setImageResource(R.mipmap.moto_black);
                fitMsend.setImageResource(R.mipmap.send_black);
                fitMmart.setImageResource(R.mipmap.mart_black);
                fitMmassage.setImageResource(R.mipmap.massage_black);
                fitMbox.setImageResource(R.mipmap.box_black);
                fitMfood.setImageResource(R.mipmap.food_black);
                break;
            default:
                break;
        }

        butAutoBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPreference sp = new SettingPreference(activity);
                if (sp.getSetting()[0].equals("OFF")) {
                    sp.updateAutoBid("ON");
                    butAutoBid.setText("ON");
                } else {
                    sp.updateAutoBid("OFF");
                    butAutoBid.setText("OFF");
                }
            }
        });

        loadImageFromStorage(imageDriver);
        Kendaraan myRide = new VehiclePreference(this).getKendaraan();
        namaDriver.setText(driver.name);
        textRating.setText(convertJarak(Double.parseDouble(driver.rating)) + " / 5");
        saldo.setText(amountAdapter(driver.deposit));
        namaKendaraan.setText(myRide.merek);
        platNomor.setText(myRide.nopol);

        menuRW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ordering) {
                } else {
                    changeFragment(new TransactionHistoryFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });
        menuInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent toNotif = new Intent(activity, ChatActivity.class);
//                startActivity(toNotif);
                if (ordering) {
                } else {
                }
                closeLeftDrawer();
//                statusFragment = true;
            }
        });
        menuDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ordering) {
                } else {
                    changeFragment(new DepositFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });
        menuBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent toRate = new Intent(activity, RatingUserActivity.class);
//                startActivity(toRate);
                if (ordering) {
                } else {
                }
                closeLeftDrawer();
//                statusFragment = true;
            }
        });
        menuRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeFragment(new OrderFragment(), false);
                if (ordering) {
                } else {
//                    Intent toChat = new Intent(activity, ChatActivity.class);
//                    startActivity(toChat);
                }
                closeLeftDrawer();
//                statusFragment = true;
            }
        });
      /* menuPerforma.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                sendResponse(106);
               closeLeftDrawer();
                statusFragment = true;
            }
        }); */

     /* menuPerforma.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent user = new Intent(activity, IsiPulsa.class);
                    startActivity(user);
          }
      });  */




        menuWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ordering) {
                } else {
                    changeFragment(new WithdrawFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
        menuFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ordering) {
                } else {
                    changeFragment(new FeedbackFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
        menuAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ordering) {
                } else {
                    changeFragment(new SettingFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
    }

    private String convertJarak(Double jarak) {
        int range = (int) (jarak * 10);
        jarak = (double) range / 10;
        return String.valueOf(jarak);
    }

    private String amountAdapter(int amo) {
        return "Wallet : $ " + NumberFormat.getNumberInstance(Locale.US).format(amo);
    }

    private void sendResponse(final int acc) {
        final String myCGM = new UserPreference(activity).getDriver().gcm_id;
        AsyncTaskHelper asyncTask = new AsyncTaskHelper(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelper.OnAsyncTaskListener() {
            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask) {
                Map<String, String> dd = new TransactionGoCab().dataDummy();
                dd.put("reg_id_pelanggan", new UserPreference(activity).getDriver().gcm_id);
                Content content = new Content();
                content.addRegId(myCGM);
                content.createDataDummy(dd);
                status = HTTPHelper.sendToGCMServer(content);
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask) {
            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask) {
                if (status == 1) {
                    Toast.makeText(activity, "Message Sent", Toast.LENGTH_SHORT).show();
                } else if (status == 0) {
                    Toast.makeText(activity, "Message sending failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask) {

            }
        });
        asyncTask.execute();
    }

    private void closeLeftDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void closeRightDrawer() {
        drawer.closeDrawer(GravityCompat.END);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            drawer.openDrawer(GravityCompat.END);
            closeLeftDrawer();
            return true;
        }

//        if (id == R.id.action_refresh) {
//            syncronizingAccount();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void loadImageFromStorage(CircularImageView civ) {
        if (!driver.image.equals("")) {
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap circleBitmap = decodeFile(f);
            civ.setImageBitmap(circleBitmap);
        }else {
            imageDriver.setImageResource(R.drawable.ic_account);

        }
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        if (!ordering) {
            exitByBackKey();
        }
    }

    protected void exitByBackKey() {

        if (statusFragment) {
            changeFragment(new DashboardFragment(), false);
            statusFragment = false;
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
                closeLeftDrawer();
                closeRightDrawer();
            } else {
                showWarnExit();
            }
        }
    }

    private MaterialDialog showWarnExit() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(R.string.Warning)
                .content(R.string.Waring_close)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .negativeText(R.string.text_cancel)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                Queries que = new Queries(new DBHandler(activity));
                Driver dr = que.getDriver();
                if (dr.status == 4) {
                    activity.finish();
                    stopService(service);
                } else {
                    pd = showLoading();
                    turningTheJob(false);
                }
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    private void updateUangBelanja(final int uang) {
        JSONObject jUang = new JSONObject();
        try {
            jUang.put("id_driver", driver.id);
            jUang.put("id_uang", uang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pdi = showLoading();
        HTTPHelper.getInstance(activity).settingUangBelanja(jUang, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("success")) {
//                        Toast.makeText(activity, "Update Ok", Toast.LENGTH_SHORT).show();
                        new SettingPreference(activity).updateMaksimalBelanja(String.valueOf(uang - 1));
                    } else {
//                        Toast.makeText(activity, "Update Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pdi.dismiss();
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {
                pdi.dismiss();
//                Toast.makeText(activity, "connection is problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "connected to Server...", true);
        return ad;
    }

    private void initializeRigthDrawer(Button butAutoBid) {
        SettingPreference sp = new SettingPreference(this);
        if (sp.getSetting()[0].equals("OFF")) {
            butAutoBid.setText("OFF");
        } else {
            butAutoBid.setText("ON");
        }
    }

    private void turningTheJob(final boolean action) {
        JSONObject jTurn = new JSONObject();
        try {
            jTurn.put("is_turn", action);
            jTurn.put("id_driver", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON_turning_on", jTurn.toString());
        HTTPHelper.getInstance(activity).turningOn(jTurn, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("banned")) {
                        showMessage(true, "Warning", "Your account is currently being suspended, please contact our office immediately!");
                    } else if (obj.getString("message").equals("success")) {
                        turningActOff();
                    } else {
                        Toast.makeText(activity, "Already Off", Toast.LENGTH_SHORT).show();
                        turningActOff();
                    }
                    pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry1 == 0) {
                    showMessage(false, "Sorry", "A network error has occurred, please try again!");
                    pd.dismiss();
                    maxRetry1 = 4;
                } else {
                    turningTheJob(false);
                    Log.d("try_ke", String.valueOf(maxRetry1));
                    maxRetry1--;
                }
            }
        });
    }

    private void turningActOff() {
        Queries que = new Queries(new DBHandler(activity));
        que.updateStatus(4);
        que.closeDatabase();
        activity.finish();
        stopService(service);
    }

    private MaterialDialog showMessage(final boolean exit, String title, String message) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(title)
                .content(message)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.GREEN)
                        .sizeDp(24))
                .positiveText("Close")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
//        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                if (exit) {
                    turningActOff();
                }
            }
        });

        return md;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
