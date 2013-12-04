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

package com.mcongrove.glass.glennis;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

/**
 * Service owning the LiveCard living in the timeline.
 */
public class GlennisService extends Service {

    private static final String LIVE_CARD_ID = "timer";
    
    private static int ME_GAME = 0;
    private static int THEM_GAME = 0;
    private static int ME_SETS[] = { 0, 0, 0 };
    private static int THEM_SETS[] = { 0, 0, 0 };
    private static int ME_MATCH = 0;
    private static int THEM_MATCH = 0;
    private static int SET = 0;
    private static int GAME = 0;
    private static boolean ACTIVE = true;

    /**
     * Binder giving access to the underlying {@code Glennis}.
     */
    public class GlennisBinder extends Binder {
    	public void myPoint() {
    		if(ACTIVE) {
	    		ME_GAME++;
	    		
	    		calculateGame();
    		}
        }

    	public void theirPoint() {
    		if(ACTIVE) {
	    		THEM_GAME++;
	    		
	    		calculateGame();
    		}
        }
    	
    	public void calculateGame() {
    		if(ME_GAME == THEM_GAME) {
    			if(ME_GAME >= 3 && THEM_GAME >= 3) {
    				// Deuce
    				mScoreRenderer.setMeGame("D");
	    			mScoreRenderer.setThemGame("D");
    			} else {
    				// Tied
	    			mScoreRenderer.setMeGame(convertToTennisScore(ME_GAME));
	    			mScoreRenderer.setThemGame(convertToTennisScore(THEM_GAME));
    			}
    		} else if(ME_GAME >= 4 || THEM_GAME >= 4) {
    			int difference = ME_GAME - THEM_GAME;
    			
    			if(difference == 1) {
    				// Advantage, ME
    				mScoreRenderer.setMeGame("AD");
	    			mScoreRenderer.setThemGame("");
    			} else if(difference == -1) {
    				// Advantage, THEM
    				mScoreRenderer.setMeGame("");
	    			mScoreRenderer.setThemGame("AD");
    			} else if(difference >= 2) {
    				// Win, ME
    				ME_SETS[SET]++;
    				
    				endGame();
    			} else {
    				// Win, THEM
    				THEM_SETS[SET]++;
    				
    				endGame();
    			}
    		} else {
    			mScoreRenderer.setMeGame(convertToTennisScore(ME_GAME));
    			mScoreRenderer.setThemGame(convertToTennisScore(THEM_GAME));
    		}
    		
    		calculateSet();
    	}
    	
    	public void calculateSet() {
    		switch(SET) {
				case 0:
					mScoreRenderer.setSet0(ME_SETS[0] + " - " + THEM_SETS[0]);
				case 1:
					mScoreRenderer.setSet1(ME_SETS[1] + " - " + THEM_SETS[1]);
				case 2:
					mScoreRenderer.setSet2(ME_SETS[2] + " - " + THEM_SETS[2]);
			}
    		
    		if(ME_SETS[SET] >= 6 || THEM_SETS[SET] >= 6) {
    			int difference = ME_SETS[SET] - THEM_SETS[SET];
    			
    			if(difference >= 2 || difference <= -2) {
    				int winner;
    				
    				if(difference >= 2) {
    					winner = 0;
    					
    					ME_MATCH++;
    				} else {
    					winner = 1;
    					
    					THEM_MATCH++;
    				}
    				
    	    		switch(SET) {
    	    			case 0:
    	    				mScoreRenderer.setSet0Winner(winner);
    	    				break;
    	    			case 1:
    	    				mScoreRenderer.setSet1Winner(winner);
    	    				break;
    	    			case 2:
    	    				mScoreRenderer.setSet2Winner(winner);
    	    				break;
    	    		}
    				
    				SET++;
    				
    				if(SET == 3) {
    					String message = "";
    					String status = "";
    					
    					if(ME_MATCH > THEM_MATCH) {
    						status = "won";
    					} else {
    						status = "lost";
    					}
    					
    					message += "Game, set, match.";
    					message += "You " + status + " " + ME_MATCH + " to " + THEM_MATCH + ".";
    					
    					tts(message);
    					
    					endAll();
    				}
    			}
    		}
    	}
    	
    	public void endGame() {
    		String status = null;
    		String message = null;
    		
    		GAME++;
    		
    		if(ME_GAME > THEM_GAME) {
    			message = "Congratulations on your win!";
    		} else {
    			message = "Sorry, you've lost this game.";
    		}
    		
    		if(ME_SETS[SET] > THEM_SETS[SET]) {
    			status = "winning";
    		} else if(THEM_SETS[SET] > ME_SETS[SET]) {
    			status = "losing";
    		} else {
    			status = "tied";
    		}
    		
    		message += " You're " + status + " " + ME_SETS[SET] + " game" + (ME_SETS[SET] == 1 ? "" : "s") + " to " + THEM_SETS[SET] + ".";
    		
    		if(GAME % 2 != 0) {
    			message += " Switch sides.";
    		}
    		
    		tts(message);
    		
    		ME_GAME = 0;
    		THEM_GAME = 0;
    		
    		mScoreRenderer.setMeGame("0");
			mScoreRenderer.setThemGame("0");
    	}
    	
    	public void endAll() {
    		ACTIVE = false;
    		
    		showEndMenu();
    	}
    	
    	public String convertToTennisScore(int points) {
    		switch(points) {
    			case 0:
    				return "0";
    			case 1:
    				return "15";
    			case 2:
    				return "30";
    			case 3:
    				return "40";
    			default:
    				return "0";
    		}
    	}
    	
    	public void tts(String words) {
    		mSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    	}
    }

    private final GlennisBinder mBinder = new GlennisBinder();

    private ScoreRenderer mScoreRenderer;
    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;
    private TextToSpeech mSpeech;
    
    public void showEndMenu() {
    	Intent menuIntent = new Intent(this, EndMenuActivity.class);
        mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        mTimelineManager = TimelineManager.from(this);
        mScoreRenderer = new ScoreRenderer(this);
        
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);

            mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mScoreRenderer);
            mLiveCard.setNonSilent(true);

            Intent menuIntent = new Intent(this, MenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.getSurfaceHolder().removeCallback(mScoreRenderer);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        
        mSpeech.shutdown();
        mSpeech = null;
        
        ME_GAME = 0;
        THEM_GAME = 0;
        ME_SETS[0] = 0;
        ME_SETS[1] = 0;
        ME_SETS[2] = 0;
        THEM_SETS[0] = 0;
        THEM_SETS[1] = 0;
        THEM_SETS[2] = 0;
        SET = 0;
        ACTIVE = true;
        
        super.onDestroy();
    }
}
