/*-----------------------------------------------------------------------------
 - Developed by Haerul Muttaqin                                               -
 - Last modified 6/3/19 2:20 AM                                               -
 - Subscribe : https://www.youtube.com/haerulmuttaqin                         -
 - Copyright (c) 2019. All rights reserved                                    -
 -----------------------------------------------------------------------------*/
package com.hmi.munchies.view.detail;

import android.support.annotation.NonNull;

import com.hmi.munchies.Utils;
import com.hmi.munchies.model.Meals;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPresenter {
    private DetailView view;

    public DetailPresenter(DetailView view) {
        this.view = view;
    }

    void getMealById(String mealName) {
        
        view.showLoading();





        Utils.getApi().getMealByName(mealName)
                .enqueue(new Callback<Meals>() {
                    @Override
                    public void onResponse(@NonNull Call<Meals> call,@NonNull Response<Meals> response) {
                    view.hideLoading();
                    if (response.isSuccessful() && response.body() != null){
                        view.setMeal(response.body().getMeals().get(0));
                    }else{

                        view.onErrorLoading(response.message());
                    }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Meals> call, @NonNull Throwable t) {

                        view.hideLoading();
                        view.onErrorLoading(t.getLocalizedMessage());

                    }
                });
        

    }
}
