package com.example.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static GsonConverterFactory converterFactory = GsonConverterFactory.create(gson);

    private static OkHttpClient client = new OkHttpClient();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(UrlLinks.HEADER)
            .addConverterFactory(converterFactory)
            .client(client)
            .build();
    private NetworkService() {

    }
    public synchronized static <T> T createService(Class<T> tClass,@Nullable Context context){
        NetworkInterceptor networkInterceptor = new NetworkInterceptor(getNetworkInfo(context));
        if (!client.interceptors().contains(networkInterceptor)) {
            client = client.newBuilder().addInterceptor(networkInterceptor).build();
        }
        return retrofit.create(tClass);
    }

    private static class  NetworkInterceptor implements Interceptor{
        private NetworkInfo networkInfo;
        NetworkInterceptor(NetworkInfo networkInfo) {
            this.networkInfo = networkInfo;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            if ((networkInfo == null) || !networkInfo.isConnected()) {
                throw new NoNetworkException("No active Network connection");
            }
            Request request = chain.request();
            return chain.proceed(request);

        }
    }
    public static class NoNetworkException extends IOException{
         NoNetworkException(String message) {
            super(message);
        }

    }

    @Nullable
    private static NetworkInfo getNetworkInfo(@Nullable Context context) {
        NetworkInfo networkInfo = null;
        if (context != null) {
            ConnectivityManager manager =(ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo =manager.getActiveNetworkInfo();
        }
        return networkInfo;
    }
}
