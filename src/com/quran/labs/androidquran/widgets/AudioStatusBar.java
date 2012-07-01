package com.quran.labs.androidquran.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quran.labs.androidquran.R;

public class AudioStatusBar extends LinearLayout {

   public static final int STOPPED_MODE = 1;
   public static final int DOWNLOADING_MODE = 2;
   public static final int PLAYING_MODE = 3;
   public static final int PAUSED_MODE = 4;

   private Context mContext;
   private int mCurrentMode;
   private int mButtonWidth;
   private int mSeparatorWidth;
   private int mSeparatorSpacing;
   private AudioBarListener mAudioBarListener;

   public interface AudioBarListener {
      public void onPlayPressed();
      public void onPausePressed();
      public void onNextPressed();
      public void onPreviousPressed();
      public void onStopPressed();
   }

   public AudioStatusBar(Context context) {
      super(context);
      init(context);
   }

   public AudioStatusBar(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context);
   }

   public AudioStatusBar(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      init(context);
   }

   private void init(Context context) {
      mContext = context;
      Resources resources = getResources();
      mButtonWidth = resources.getDimensionPixelSize(
              R.dimen.audiobar_button_width);
      mSeparatorWidth = resources.getDimensionPixelSize(
              R.dimen.audiobar_separator_width);
      mSeparatorSpacing = resources.getDimensionPixelSize(
              R.dimen.audiobar_separator_padding);
      setOrientation(LinearLayout.HORIZONTAL);
      showStoppedMode();
   }

   public void switchMode(int mode){
      if (mode == STOPPED_MODE){
         showStoppedMode();
      }
      else if (mode == DOWNLOADING_MODE){
         showDownloadingMode();
      }
      else if (mode == PLAYING_MODE){
         showPlayingMode(false);
      }
      else { showPlayingMode(true); }
   }

   private void showStoppedMode() {
      mCurrentMode = STOPPED_MODE;
      removeAllViews();

      addButton(R.drawable.ic_play);
      addSeparator();

      // add sheikh area
      TextView qariView = new TextView(mContext);
      qariView.setText("Muhammad Sideeq al-Minshawi");
      qariView.setTextColor(Color.WHITE);
      qariView.setGravity(Gravity.CENTER_VERTICAL);
      addView(qariView, LayoutParams.WRAP_CONTENT,
              LayoutParams.MATCH_PARENT);
   }

   private void showDownloadingMode(){

   }

   private void showPlayingMode(boolean isPaused) {
      removeAllViews();

      int button;
      if (!isPaused){
         button = R.drawable.ic_pause;
         mCurrentMode = PAUSED_MODE;
      }
      else {
         button = R.drawable.ic_play;
         mCurrentMode = PLAYING_MODE;
      }

      addButton(R.drawable.ic_stop);
      addButton(R.drawable.ic_previous);
      addButton(button);
      addButton(R.drawable.ic_next);
      addButton(R.drawable.ic_repeat);
   }

   private void addButton(int imageId){
      ImageView button = new ImageView(mContext);
      button.setImageResource(imageId);
      button.setScaleType(ImageView.ScaleType.CENTER);
      button.setOnClickListener(mOnClickListener);
      button.setTag(imageId);
      addView(button, mButtonWidth,
              LayoutParams.MATCH_PARENT);
   }

   private void addSeparator(){
      ImageView separator = new ImageView(mContext);
      separator.setBackgroundColor(Color.WHITE);
      separator.setPadding(0, mSeparatorSpacing, 0, mSeparatorSpacing);
      LinearLayout.LayoutParams paddingParams =
              new LayoutParams(mSeparatorWidth, LayoutParams.MATCH_PARENT);
      paddingParams.setMargins(0, 0, mSeparatorSpacing, 0);
      addView(separator, paddingParams);
   }

   public void setAudioBarListener(AudioBarListener listener){
      mAudioBarListener = listener;
   }

   OnClickListener mOnClickListener = new OnClickListener() {
      @Override
      public void onClick(View view) {
         if (mAudioBarListener != null){
            int tag = (Integer)view.getTag();
            switch (tag){
               case R.drawable.ic_play:
                  mAudioBarListener.onPlayPressed();
                  break;
               case R.drawable.ic_stop:
                  mAudioBarListener.onStopPressed();
                  break;
               case R.drawable.ic_pause:
                  mAudioBarListener.onPausePressed();
                  break;
               case R.drawable.ic_next:
                  mAudioBarListener.onNextPressed();
                  break;
               case R.drawable.ic_previous:
                  mAudioBarListener.onPreviousPressed();
                  break;
            }
         }
      }
   };
}