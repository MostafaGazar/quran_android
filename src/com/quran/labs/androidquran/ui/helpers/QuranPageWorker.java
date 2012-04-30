package com.quran.labs.androidquran.ui.helpers;

import java.lang.ref.WeakReference;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class QuranPageWorker {
   private static final String TAG = "QuranPageWorker";
   
   private LruCache<Integer, Bitmap> mMemoryCache = null;

   public QuranPageWorker(Context context){
      final int memClass = ((ActivityManager)context.getSystemService(
            Context.ACTIVITY_SERVICE)).getMemoryClass();
      final int cacheSize = 1024 * 1024 * memClass / 8;
      mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize){
         @Override
         protected int sizeOf(Integer key, Bitmap bitmap){
            Log.d(TAG, "row bytes: " + bitmap.getRowBytes() + ", height: " +
                  bitmap.getHeight() + ", " + (bitmap.getRowBytes() *
                        bitmap.getHeight()));
            return bitmap.getRowBytes() * bitmap.getHeight();
         }
      };
      
      Log.d(TAG, "initial LruCache size: " + (memClass/8));
   }
   
   public void addBitmapToCache(Integer key, Bitmap bitmap) {
      if (bitmap != null && getBitmapFromCache(key) == null) {
         mMemoryCache.put(key, bitmap);
      }
      
      Log.d(TAG, "cache: number of puts: " + mMemoryCache.putCount() + 
            ", number of evicts: " + mMemoryCache.evictionCount());
   }

   public Bitmap getBitmapFromCache(Integer key) {
      return mMemoryCache.get(key);
   }

   public void loadPage(int pageNumber, ImageView imageView) {
      final Bitmap bitmap = getBitmapFromCache(pageNumber);
      if (bitmap != null){
         imageView.setImageBitmap(bitmap);
      }
      else {
         // TODO: set a placeholder image while loading
         QuranPageWorkerTask task = new QuranPageWorkerTask(imageView);
         task.execute(pageNumber);
      }
   }

   class QuranPageWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
      private final WeakReference<ImageView> imageViewReference;
      private int data = 0;

      public QuranPageWorkerTask(ImageView imageView) {
         // use a WeakReference to ensure the ImageView can be garbage collected
         imageViewReference = new WeakReference<ImageView>(imageView);
      }

      @Override
      protected Bitmap doInBackground(Integer... params) {
         data = params[0];
         final Bitmap bitmap = QuranDisplayHelper.getQuranPage(data);
         if (bitmap == null){ Log.w(TAG, "got bitmap back as null..."); }

         addBitmapToCache(data, bitmap);
         return bitmap;
      }

      // once complete, see if ImageView is still around and set bitmap.
      @Override
      protected void onPostExecute(Bitmap bitmap) {
         if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
               imageView.setImageBitmap(bitmap);
            }
            else { Log.w(TAG, "failed to set bitmap in imageview"); }
         }
      }
   }
}