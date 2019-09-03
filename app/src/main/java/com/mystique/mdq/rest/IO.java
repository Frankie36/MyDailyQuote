package com.mystique.mdq.rest;

import android.view.View;
import android.widget.TextView;

import com.mystique.mdq.MainApplication;
import com.mystique.mdq.R;
import com.mystique.mdq.model.FortuneResponse;
import com.santalu.emptyview.EmptyView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IO {
    private ApiInterface apiService = MainApplication.getInstance().getApiService();

    public void getFortuneQuote(final EmptyView emptyView, final TextView tvQuote) {
        emptyView.showLoading();
        Call<FortuneResponse> call = apiService.getData();
        call.enqueue(new Callback<FortuneResponse>() {
            @Override
            public void onResponse(Call<FortuneResponse> call, Response<FortuneResponse> response) {
                if (response.isSuccessful()) {
                    int responseSize = response.body().getFortune().size();

                    if (response.body().getFortune() != null && responseSize > 0) {

                        String quote = "";
                        if (responseSize == 1)
                            quote = response.body().getFortune().get(0);
                        else {
                            //concatenate all strings received in response
                            for (String text : response.body().getFortune()) {
                                quote = quote.concat("\n").concat(text);
                            }
                        }

                        tvQuote.setText(quote);
                        emptyView.showContent();
                    } else {
                        emptyView.empty()
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new IO().getFortuneQuote(emptyView, tvQuote);
                                    }
                                }).show();
                    }
                } else {
                    emptyView.empty()
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new IO().getFortuneQuote(emptyView, tvQuote);
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<FortuneResponse> call, Throwable t) {
                emptyView.error()
                        .setErrorTitle("")
                        .setErrorText(R.string.no_data)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new IO().getFortuneQuote(emptyView, tvQuote);
                            }
                        }).show();
            }
        });
    }

}
