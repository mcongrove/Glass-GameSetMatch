/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.glass.sample.compass;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * View used to draw a running timer.
 */
public class ScoreView extends FrameLayout {

    /**
     * Interface to listen for changes on the view layout.
     */
    public interface ChangeListener {
        /** Notified of a change in the view. */
        public void onChange();
    }
    
    private ChangeListener mChangeListener;
	
    private final TextView mMeGameView;
    private final TextView mThemGameView;
    private final TextView mSet0View;
    private final TextView mSet1View;
    private final TextView mSet2View;
    private final TextView mMeNameView;
    private final TextView mThemNameView;

    public ScoreView(Context context) {
        this(context, null, 0);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);

        LayoutInflater.from(context).inflate(R.layout.score, this);

        mMeGameView = (TextView) findViewById(R.id.MeGame);
        mThemGameView = (TextView) findViewById(R.id.ThemGame);
        mSet0View = (TextView) findViewById(R.id.Set0);
        mSet1View = (TextView) findViewById(R.id.Set1);
        mSet2View = (TextView) findViewById(R.id.Set2);
        mMeNameView = (TextView) findViewById(R.id.MeName);
        mThemNameView = (TextView) findViewById(R.id.ThemName);

        mMeGameView.setText("0");
        mThemGameView.setText("0");
        mSet0View.setText("0 - 0");
        mSet1View.setText("0 - 0");
        mSet2View.setText("0 - 0");
        mMeNameView.setText(context.getResources().getString(R.string.me_name));
        mThemNameView.setText(context.getResources().getString(R.string.them_name));
    }

    /**
     * Set a {@link ChangeListener}.
     */
    public void setListener(ChangeListener listener) {
        mChangeListener = listener;
    }
    
    public void setMeGame(String score) {
    	mMeGameView.setText(score);
    	updateText();
    }
    
    public void setThemGame(String score) {
    	mThemGameView.setText(score);
    	updateText();
    }
    
    public void setSet0(String score) {
    	mSet0View.setText(score);
    	updateText();
    }
    
    public void setSet0Winner(int winner) {
    	mSet0View.setTextColor(winner == 0 ? Color.parseColor("#99CC33") : Color.parseColor("#CC3333"));
    	updateText();
    }
    
    public void setSet1(String score) {
    	mSet1View.setText(score);
    	updateText();
    }
    
    public void setSet1Winner(int winner) {
    	mSet1View.setTextColor(winner == 0 ? Color.parseColor("#99CC33") : Color.parseColor("#CC3333"));
    	updateText();
    }
    
    public void setSet2(String score) {
    	mSet2View.setText(score);
    	updateText();
    }
    
    public void setSet2Winner(int winner) {
    	mSet2View.setTextColor(winner == 0 ? Color.parseColor("#99CC33") : Color.parseColor("#CC3333"));
    	updateText();
    }

    /**
     * Updates the displayed text with the provided values.
     */
    private void updateText() {
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }
}