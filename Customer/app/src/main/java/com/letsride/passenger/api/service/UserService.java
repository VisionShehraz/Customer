package com.letsride.passenger.api.service;

import com.letsride.passenger.model.json.book.RateDriverRequestJson;
import com.letsride.passenger.model.json.book.RateDriverResponseJson;
import com.letsride.passenger.model.json.fcm.CancelBookRequestJson;
import com.letsride.passenger.model.json.fcm.CancelBookResponseJson;
import com.letsride.passenger.model.json.menu.HelpRequestJson;
import com.letsride.passenger.model.json.menu.HelpResponseJson;
import com.letsride.passenger.model.json.menu.HistoryRequestJson;
import com.letsride.passenger.model.json.menu.HistoryResponseJson;
import com.letsride.passenger.model.json.menu.VersionRequestJson;
import com.letsride.passenger.model.json.menu.VersionResponseJson;
import com.letsride.passenger.model.json.user.ChangePasswordRequestJson;
import com.letsride.passenger.model.json.user.ChangePasswordResponseJson;
import com.letsride.passenger.model.json.user.GetBannerResponseJson;
import com.letsride.passenger.model.json.user.GetFiturResponseJson;
import com.letsride.passenger.model.json.user.GetSaldoRequestJson;
import com.letsride.passenger.model.json.user.GetSaldoResponseJson;
import com.letsride.passenger.model.json.user.LoginRequestJson;
import com.letsride.passenger.model.json.user.LoginResponseJson;
import com.letsride.passenger.model.json.user.PulsaRequestJson;
import com.letsride.passenger.model.json.user.PulsaResponseJson;
import com.letsride.passenger.model.json.user.RegisterRequestJson;
import com.letsride.passenger.model.json.user.RegisterResponseJson;
import com.letsride.passenger.model.json.user.TopupRequestJson;
import com.letsride.passenger.model.json.user.TopupResponseJson;
import com.letsride.passenger.model.json.user.UpdateProfileRequestJson;
import com.letsride.passenger.model.json.user.UpdateProfileResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Androgo on 10/13/2018.
 */

public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("pelanggan/get_saldo")
    Call<GetSaldoResponseJson> getSaldo(@Body GetSaldoRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/user_send_help")
    Call<HelpResponseJson> sendHelp(@Body HelpRequestJson param);

    @POST("pelanggan/update_profile")
    Call<UpdateProfileResponseJson> updateProfile(@Body UpdateProfileRequestJson param);

    @POST("pelanggan/change_password")
    Call<ChangePasswordResponseJson> changePassword(@Body ChangePasswordRequestJson param);

    @POST("book/user_cancel_transaction")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/check_version")
    Call<VersionResponseJson> checkVersion(@Body VersionRequestJson param);

    @POST("book/user_rate_driver")
    Call<RateDriverResponseJson> rateDriver(@Body RateDriverRequestJson param);

    @POST("pelanggan/verifikasi_topup")
    Call<TopupResponseJson> topUp(@Body TopupRequestJson param);

    @POST("pelanggan/verifikasi_isipulsa")
    Call<PulsaResponseJson> isipulsa(@Body PulsaRequestJson param);

    @POST("pelanggan/complete_transaksi")
    Call<HistoryResponseJson> getCompleteHistory(@Body HistoryRequestJson param);

    @POST("pelanggan/inprogress_transaksi")
    Call<HistoryResponseJson> getOnProgressHistory(@Body HistoryRequestJson param);

    @GET("pelanggan/banner_promosi")
    Call<GetBannerResponseJson> getBanner();

}
