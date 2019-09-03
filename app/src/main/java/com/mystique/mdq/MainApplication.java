package com.mystique.mdq;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.GsonBuilder;
import com.mystique.mdq.rest.ApiInterface;
import com.mystique.mdq.rest.InternetConnectionListener;
import com.mystique.mdq.rest.NetworkConnectionInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class
            .getSimpleName();

    private static MainApplication mInstance;
    private ApiInterface apiService;
    private InternetConnectionListener mInternetConnectionListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    public void setInternetConnectionListener(InternetConnectionListener listener) {
        mInternetConnectionListener = listener;
    }

    public void removeInternetConnectionListener() {
        mInternetConnectionListener = null;
    }

    public ApiInterface getApiService() {
        if (apiService == null) {
            apiService = provideRetrofit(Constants.BASE_URL).create(ApiInterface.class);
        }
        return apiService;
    }

    public void setApiService(ApiInterface apiService) {
        this.apiService = apiService;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Retrofit provideRetrofit(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                //Custom converters should come before Gson converter
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create((new GsonBuilder()
                        .setLenient()
                        .create())))
                .client(provideOkHttpClient())
                .build();
    }

    public OkHttpClient provideOkHttpClient() {
        //Time out after 10 seconds of inactivity
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);

        //add our internet connection listener
        okhttpClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return MainApplication.this.isInternetAvailable();
            }

            @Override
            public void onInternetUnavailable() {
                if (mInternetConnectionListener != null) {
                    mInternetConnectionListener.onInternetUnavailable();
                }
            }
        });

        okhttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });


        return okhttpClientBuilder.build();
    }
}
