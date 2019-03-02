package com.gotaxiride.passenger.api;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.gotaxiride.passenger.utils.BooleanSerializerDeserializer;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gotaxiride.passenger.utils.Log.LOG;

/**
 * Created by Androgo on 10/13/2018.
 */

public class ServiceGenerator {

    public static String URL_WEB = "http://salesgotaxi.com/";  // please web server

    public static final String Privacy = URL_WEB + "index.php/c_utama/privacy";
    public static final String FAQ = URL_WEB + "index.php/c_utama/faq";
    public static final String TOS = URL_WEB + "index.php/c_utama/terms_conditions";
    public static final String FORGOT = URL_WEB + "index.php/c_utama/forgot_password_customer";

    public static final String API_BASE_URL = URL_WEB + "api/";
    private static BooleanSerializerDeserializer booleanSerializerDeserializer = new BooleanSerializerDeserializer();
    public static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .registerTypeAdapter(Boolean.class, booleanSerializerDeserializer)
            .registerTypeAdapter(boolean.class, booleanSerializerDeserializer)
            .create();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        if (!httpClient.interceptors().isEmpty()) {
            httpClient.interceptors().clear();
        }
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }


        // Logging HTTP Request & Response
        if (LOG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

}
