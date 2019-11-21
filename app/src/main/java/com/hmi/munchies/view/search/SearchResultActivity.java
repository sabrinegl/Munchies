/*-----------------------------------------------------------------------------
 - Developed by Haerul Muttaqin                                               -
 - Last modified 7/8/19 12:07 AM                                              -
 - Subscribe : https://www.youtube.com/haerulmuttaqin                         -
 - Copyright (c) 2019. All rights reserved                                    -
 -----------------------------------------------------------------------------*/
package com.hmi.munchies.view.search;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.hmi.munchies.R;
import com.hmi.munchies.Utils;
import com.hmi.munchies.adapter.RecyclerViewMealByCategory;
import com.hmi.munchies.model.Meals;
import com.hmi.munchies.view.detail.DetailActivity;
import com.hmi.munchies.view.home.HomeActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    private ProgressBar mLoadingProgress;
    private RecyclerView mRecycler;
    private List<Meals.Meal> mResultMealList;
    private List<String> ingredients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mLoadingProgress= findViewById(R.id.activity_search_result_progress);
        mRecycler = findViewById(R.id.activity_search_result_recycler);
        String category = getIntent().getExtras().getString("cat");
        String ingredient = getIntent().getExtras().getString("ing");
        String allergy = getIntent().getExtras().getString("allergy");
        getMealByCategory(category, ingredient, allergy);
    }



    void  getMealByCategory(String category, String ingredient, String allergy){



        mLoadingProgress.setVisibility(View.VISIBLE);
        if(category.isEmpty()){
            getMealsByIngredient(ingredient, allergy);
            return;
        }

        Call<Meals> mealsCall = (Call<Meals>) Utils.getApi().getMealByCategory(category);
        mealsCall.enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(@NonNull Call<Meals> call, @NonNull Response<Meals> response) {

                if (response.isSuccessful() && response.body() != null) {

                    mResultMealList = response.body().getMeals();

                    getMealsByIngredient(ingredient, allergy);
                }
                else {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Meals> call, @NonNull Throwable t) {
                mLoadingProgress.setVisibility(View.GONE);
            }
        });



    }

    private void getMealsByIngredient(String ingredient, String allergy) {


        if(ingredient.isEmpty()){
            getMealsByAllergy(allergy);
            return;
        }

        Call<Meals> mealsCall = (Call<Meals>) Utils.getApi().getMealByIngredient(ingredient);
        mealsCall.enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(Call<Meals> call, Response<Meals> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Meals.Meal> ingredientResultMealList = response.body().getMeals();
                    if(mResultMealList != null) {
                        List<Meals.Meal> tempList = new ArrayList<>();

                        for (Meals.Meal meal : mResultMealList) {
                            if (ingredientResultMealList.contains(meal)) {
                                tempList.add(meal);
                            }
                        }
                        mResultMealList = tempList;
                    }else{
                        mResultMealList = ingredientResultMealList;
                    }
                    if(!allergy.equals(""))
                        getMealsByAllergy(allergy);
                    else
                        displayResult(mResultMealList);
                }
            }

            @Override
            public void onFailure(Call<Meals> call, Throwable t) {
                mLoadingProgress.setVisibility(View.GONE);
            }
        });
    }

    private void getMealsByAllergy(String allergy) {

        Call<Meals> mealsCall = (Call<Meals>) Utils.getApi().getMealByIngredient(allergy);
        mealsCall.enqueue(new Callback<Meals>() {

            @Override
            public void onResponse(Call<Meals> call, Response<Meals> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Meals.Meal> ingredientResultMealList = response.body().getMeals();
                    if(mResultMealList != null) {
                        Iterator iterator = mResultMealList.iterator();
                        Meals.Meal meal = null;
                        while (iterator.hasNext()) {
                            meal = (Meals.Meal) iterator.next();
                            if (ingredientResultMealList.contains(meal)) {
                                iterator.remove();
                            }

                        }
                    }
                    mLoadingProgress.setVisibility(View.GONE);
                    displayResult(mResultMealList);
                }
            }

            @Override
            public void onFailure(Call<Meals> call, Throwable t) {
                mLoadingProgress.setVisibility(View.GONE);
            }
        });


    }

    private void displayResult(@NonNull List<Meals.Meal> mealList) {
        RecyclerViewMealByCategory mealsAdapter = new RecyclerViewMealByCategory(SearchResultActivity.this, mealList);
        mRecycler.setAdapter(mealsAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(SearchResultActivity.this, 3,
                GridLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(layoutManager);
        mealsAdapter.setOnItemClickListener(new RecyclerViewMealByCategory.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Meals.Meal meal = mealList.get(position);
                Intent intent = new Intent(SearchResultActivity.this, DetailActivity.class);
                intent.putExtra(HomeActivity.EXTRA_DETAIL, meal.getStrMeal());
                startActivity(intent);
            }
        });
        mealsAdapter.notifyDataSetChanged();

    }


}
