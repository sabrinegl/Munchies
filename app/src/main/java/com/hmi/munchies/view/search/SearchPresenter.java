
package com.hmi.munchies.view.search;

import android.support.annotation.NonNull;
import com.google.gson.JsonObject;
import com.hmi.munchies.Utils;
import com.hmi.munchies.model.Categories;
import com.hmi.munchies.model.Meals;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SearchPresenter {

    private SearchView view;

    public SearchPresenter(SearchView view) {
        this.view = view;
    }

    void getMeals() {



        view.showLoading();

        Call<Meals> mealsCall = Utils.getApi().getMeal();
        mealsCall.enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(@NonNull Call<Meals> call, @NonNull Response<Meals> response) {
                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {


                    view.setMeal(response.body().getMeals());

                }
                else {
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


    void getCategories() {

        view.showLoading();

        Call<Categories> categoriesCall = Utils.getApi().getCategories();
        categoriesCall.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(@NonNull Call<Categories> call,
                                   @NonNull Response<Categories> response) {

                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {

                    view.setCategory(FilterSearch(call, response));


                }
                else {
                    view.onErrorLoading(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Categories> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }

       public List<Categories.Category> FilterSearch (@NonNull Call<Categories> call, @NonNull Response<Categories> response){

        String ing,cat,all,dis;

           ArrayList<String> filter;


           ing= SearchActivity.ing;
           cat=SearchActivity.cat;
           dis=SearchActivity.dis;
           all=SearchActivity.all;

           JsonObject post = new JsonObject().get(response.body().getCategories().toString()).getAsJsonObject();

           if (post.get("Ingridients").getAsString().contains(ing) && post.get("Categories").getAsString().contains(cat)

                   && ! post.get("Ingridients").getAsString().contains(all)
                   && ! post.get("Ingridients").getAsString().contains(dis))

           return response.body().getCategories();
           return null;
       }

}
