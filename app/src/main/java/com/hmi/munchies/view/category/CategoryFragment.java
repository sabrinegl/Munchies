/*-----------------------------------------------------------------------------
 - Developed by Haerul Muttaqin                                               -
 - Last modified 6/26/19 10:38 PM                                             -
 - Subscribe : https://www.youtube.com/haerulmuttaqin                         -
 - Copyright (c) 2019. All rights reserved                                    -
 -----------------------------------------------------------------------------*/
package com.hmi.munchies.view.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hmi.munchies.R;
import com.hmi.munchies.Utils;
import com.hmi.munchies.adapter.RecyclerViewMealByCategory;
import com.hmi.munchies.model.Meals;
import com.hmi.munchies.view.detail.DetailActivity;
import com.hmi.munchies.view.home.HomeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends Fragment implements CategoryView {

    @BindView(R.id.recyclerView) 
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.imageCategory)
    ImageView imageCategory;
    @BindView(R.id.imageCategoryBg)
    ImageView imageCategoryBg;
    @BindView(R.id.textCategory)
    TextView textCategory;
    
    AlertDialog.Builder descDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            textCategory.setText(getArguments().getString("EXTRA_DATA_DESC"));
            Picasso.get()
                    .load(getArguments().getString("EXTRA_DATA_IMAGE"))
                    .into(imageCategory);
            Picasso.get()
                    .load(getArguments().getString("EXTRA_DATA_IMAGE"))
                    .into(imageCategoryBg);
            descDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(getArguments().getString("EXTRA_DATA_NAME"))
                    .setMessage(getArguments().getString("EXTRA_DATA_DESC"));
            
            CategoryPresenter presenter = new CategoryPresenter(this);
            presenter.getMealByCategory(getArguments().getString("EXTRA_DATA_NAME"));
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setMeals(List<Meals.Meal> meals) {
        RecyclerViewMealByCategory adapter = 
                new RecyclerViewMealByCategory(getActivity(), meals);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setClipToPadding(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        adapter.setOnItemClickListener((view, position) -> {
            TextView mealName = view.findViewById(R.id.mealName);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(HomeActivity.EXTRA_DETAIL, mealName.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    public void onErrorLoading(String message) {
        Utils.showDialogMessage(getActivity(), "Error ", message);
    }
    
    @OnClick(R.id.cardCategory)
    public void onClick() {
        descDialog.setPositiveButton("CLOSE", (dialog, which) -> dialog.dismiss());
        descDialog.show();
    }
    
}
