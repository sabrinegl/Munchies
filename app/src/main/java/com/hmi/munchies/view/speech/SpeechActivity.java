/*-----------------------------------------------------------------------------
 - Developed by Haerul Muttaqin                                               -
 - Last modified 7/8/19 12:43 AM                                              -
 - Subscribe : https://www.youtube.com/haerulmuttaqin                         -
 - Copyright (c) 2019. All rights reserved                                    -
 -----------------------------------------------------------------------------*/
package com.hmi.munchies.view.speech;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hmi.munchies.R;
import com.hmi.munchies.view.home.HomeActivity;
import com.hmi.munchies.view.search.SearchResultActivity;

import java.util.Locale;


public class SpeechActivity extends AppCompatActivity {


    private int repeat = 0;
    MediaPlayer mp;
    private EditText ingredients, food_type, allergies, food_dislike;
    private String mainstr, ing, foodtype, allergy, dislike, keyword;
    private int index;
    private Button go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        ingredients = (EditText) findViewById(R.id.ingredients);
        food_type = (EditText) findViewById(R.id.food_type);
        allergies = (EditText) findViewById(R.id.allergies);
        food_dislike = (EditText) findViewById(R.id.food_dislike);
        go = (Button) findViewById(R.id.button2);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                foodtype = food_type.getText().toString();
                ing = ingredients.getText().toString();
                allergy = allergies.getText().toString();
                if (!((allergy.isEmpty()==false) && ing.isEmpty() && foodtype.isEmpty())) {
                    Intent intent = null;
                    if (foodtype.isEmpty() && ing.isEmpty() && allergy.isEmpty()) {
                        intent = new Intent(SpeechActivity.this, HomeActivity.class);
                    } else {

                        intent = new Intent(SpeechActivity.this, SearchResultActivity.class);
                        intent.putExtra("ing", ing);
                        intent.putExtra("cat", foodtype);
                        intent.putExtra("allergy", allergy);
                        intent.putExtra("dislike", dislike);
                    }
                    startActivity(intent);
                } else {
                    playSound(R.raw.alert);
                }

            }
        });

    }

    public void OnclickRecord(View v) {
        if (repeat == 4) {
            repeat = 0;
            changeInput(ingredients, "");
            changeInput(food_type, "");
            changeInput(allergies, "");
            changeInput(food_dislike, "");
        }
        playSound(R.raw.ingredients);

    }

    public synchronized int promptSpeechInput(int repeat) {


        if (repeat <= 4) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

            try {
                startActivityForResult(i, 100);
            } catch (ActivityNotFoundException a) {

                Toast.makeText(SpeechActivity.this, "Sorry your device doesn't support speech language !", Toast.LENGTH_LONG).show();
            }
        }
        return repeat;
    }

    public void onActivityResult(int request_code, int result_code, Intent i) {

        super.onActivityResult(request_code, result_code, i);

        switch (request_code) {
            case 100:
                if (result_code == RESULT_OK && i != null) {
                    String input = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    if (!input.equals("next")) {
                        switch (repeat) {
                            case 1: {
                                ing = input;
                                changeInput(ingredients, ing);
                            }
                            break;
                            case 2: {
                                foodtype = input;
                                changeInput(food_type, foodtype);
                            }
                            break;

                            case 3: {
                                allergy = input;
                                changeInput(allergies, allergy);
                            }
                            break;

                            case 4: {
                                dislike = input;
                                changeInput(food_dislike, dislike);
                            }
                            break;
                        }
                    }

                }
                break;
        }

        switch (repeat) {
            case 1:
                playSound(R.raw.foodtype);
                break;
            case 2:
                playSound(R.raw.allergy);
                break;
            case 3:
                playSound(R.raw.dislike);
                break;
        }

    }


    public void changeInput(EditText e, String s) {
        e.setText(s);
    }

    public void playSound(int sound) {
        if (sound != R.raw.alert) {
            mp = MediaPlayer.create(this, sound);
            mp.setOnCompletionListener(mp1 -> {
                repeat = promptSpeechInput(repeat + 1);
            });
            mp.start();
        }

    }
}