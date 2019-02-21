//package net.gumcode.drivermangjek.network;
//
//import android.content.Context;
//import net.gumcode.drivermangjek.network.Log;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.kurawal.bankdki.model.CollectionLoan;
//import com.kurawal.bankdki.model.CollectionLoanID;
//import com.kurawal.bankdki.model.DataNasabah;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by GagahIB on 25/08/2016.
// */
//public class RestVolley {
//
//    private static RestVolley instance;
//    private Context context;
//    private static String TAG_response = "RestVolley_response";
//    private static String TAG_error = "RestVolley_error";
//
//    String URL_Setor = "http://robert-001-site12.btempurl.com/TellerTransaction/androidsavesetorantunai";
//    String URL_get_data = "http://robert-001-site11.btempurl.com/VwGridAccount/select";
//    String URL_get_col_by_date = "http://robert-001-site10.btempurl.com/LoanCollectionGroupPay/select";
//    String URL_get_col_by_idLoan = "http://robert-001-site10.btempurl.com/VwGridCollection/select";
//
//    public static RestVolley getInstance(Context context){
//        if(instance==null){
//            instance = new RestVolley();
//        }
//        instance.setContext(context);
//        return instance;
//    }
//
//    private void setContext(Context context){
//        this.context = context;
//    }
//
//
//    public void setorTunaiToServer(JSONObject setoranTunaiData, final NetworkActionResult actionResult){
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, URL_Setor, setoranTunaiData,  new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG_response, response.toString());
//                        actionResult.onSuccess(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG_error, error.toString());
//                        actionResult.onError(error.toString());
//                    }
//                }){
//        };
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        mRequestQueue.add(jsObjRequest);
//    }
//
//    public void getPelangganData(JSONObject dataPribadi, final NetworkActionResult actionResult){
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, URL_get_data, dataPribadi,  new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG_response, response.toString());
//                        actionResult.onSuccess(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG_error, error.toString());
//                        actionResult.onError(error.toString());
//                    }
//                }){
//        };
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        mRequestQueue.add(jsObjRequest);
//    }
//
//    public void tesString() throws JSONException {
//        String url = "http://vault.kurawal.com/api/trx/vault/1H4PvhTgWc5k18ZEvL22dmi68fu3JPU4mFdr8s";
////        String url = "http://httpbin.org/post";
//        final JSONObject jsonBody = new JSONObject("{\"amount\":\"0.1\"}");
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>(){
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d("Response", response);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", error.toString());
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
////                headers.put("Content-Type", "application/json; charset=UTF-8");
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("amount", "0.01");
//                return params;
//            }
//        };
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        mRequestQueue.add(postRequest);
//    }
//
//    public DataNasabah responseDataNasabah(JSONObject response) throws JSONException {
//        JSONObject Data = response.getJSONObject("Data");
//        DataNasabah dataNasabah = new DataNasabah();
//
//
//        if(Data.getInt("ItemCount") == 0){
//            dataNasabah.isSuccess = false;
//        }else{
//            JSONArray Items = Data.getJSONArray("Items");
//            JSONObject akun = Items.getJSONObject(0);
//
//            dataNasabah.Data = Data;
//            dataNasabah.Items = Items;
//            dataNasabah.akun = akun;
//            dataNasabah.isSuccess = true;
//        }
//        return dataNasabah;
//    }
//
//    public void getLoanDataByDate(JSONObject loanDate, final NetworkActionResult actionResult){
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, URL_get_col_by_date, loanDate,  new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG_response, response.toString());
//                        actionResult.onSuccess(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG_error, error.toString());
//                        actionResult.onError(error.toString());
//                    }
//                }){
//        };
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        mRequestQueue.add(jsObjRequest);
//    }
//
//    public void getLoanDataByIDLoan(JSONObject loanID, final NetworkActionResult actionResult){
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, URL_get_col_by_idLoan, loanID,  new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG_response, response.toString());
//                        actionResult.onSuccess(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG_error, error.toString());
//                        actionResult.onError(error.toString());
//                    }
//                }){
//        };
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        mRequestQueue.add(jsObjRequest);
//    }
//
//
//    public ArrayList<CollectionLoan> responseLoadByDate(JSONObject response) throws JSONException {
//        ArrayList<CollectionLoan> arrCLoan = new ArrayList<>();
//
//        JSONArray Items = response.getJSONObject("Data").getJSONArray("Items");
//
//        for(int i=0; i<Items.length(); i++){
//            JSONObject loan = Items.getJSONObject(i);
//            CollectionLoan cLoan = new CollectionLoan();
//            cLoan.LoanID = loan.getString("LoanID");
//            cLoan.LoanNo = loan.getString("LoanNo");
//            cLoan.LoanAmount = loan.getInt("LoanAmount");
//            cLoan.CollectionDate = loan.getString("CollectionDate");
//            arrCLoan.add(cLoan);
//        }
//        return arrCLoan;
//    }
//
//    public ArrayList<CollectionLoanID> responseLoadByIDLoan(JSONObject response) throws JSONException {
//        ArrayList<CollectionLoanID> arrCLoan = new ArrayList<>();
//
//        JSONArray Items = response.getJSONObject("Data").getJSONArray("Items");
//
//        for(int i=0; i<Items.length(); i++){
//            JSONObject loan = Items.getJSONObject(i);
//            CollectionLoanID cLoan = new CollectionLoanID();
//            cLoan.LoanAccountCollectionID = loan.getString("LoanAccountCollectionID");
//            cLoan.LoanID = loan.getString("LoanID");
//            cLoan.LoanNo = loan.getString("LoanNo");
//            cLoan.GroupName = loan.getString("GroupName");
//            cLoan.StartDate = loan.getString("StartDate");
//            cLoan.TenorMonth = loan.getInt("TenorMonth");
//            cLoan.EndDate = loan.getString("EndDate");
//            cLoan.CollectionAmount = loan.getInt("CollectionAmount");
//            cLoan.CustomerName = loan.getString("CustomerName");
//            cLoan.AccountName = loan.getString("AccountName");
//            cLoan.IsPaid = loan.getBoolean("IsPaid");
//            arrCLoan.add(cLoan);
//        }
//
//        return arrCLoan;
//    }
//}
