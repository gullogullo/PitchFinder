package com.example.pitchfinder.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.pitchfinder.R;


public class ResultView extends View {

    public boolean isCorrect = false;
    private Bitmap bitmapAnswer;


    public ResultView(Context context) {
        super(context);
        init();
    }


    public ResultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        bitmapAnswer = BitmapFactory.decodeResource(getResources(),
                R.drawable.tick_right);
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // HEARING AID VERSION
        /*
        bitmapAnswer = Bitmap.createScaledBitmap(bitmapAnswer,
                getWidth(), getHeight(), true);
        canvas.drawBitmap(bitmapAnswer,
                0, 0, null);
*       */
        // NORMAL HEARING VERSION
        if (!isCorrect) {
            bitmapAnswer = BitmapFactory.decodeResource(getResources(),
                    R.drawable.tick_wrong);
            //Log.i("WRONG ANSWER", String.valueOf(getRootView().getWidth()));
        } else {
            bitmapAnswer = BitmapFactory.decodeResource(getResources(),
                    R.drawable.tick_right);
            //Log.i("RIGHT ANSWER", String.valueOf(getRootView().getWidth()));
        }
        //Log.i("Dimensioni", String.valueOf(getRootView().getWidth()));
        //bitmapAnswer = Bitmap.createScaledBitmap(bitmapAnswer, getRootView().getWidth(), getRootView().getHeight(), true);
        bitmapAnswer = Bitmap.createScaledBitmap(bitmapAnswer,
                200, 200, true);
        int centreX = (getWidth()  - bitmapAnswer.getWidth()) /2;
        int centreY = (getHeight() - bitmapAnswer.getHeight()) /2;
        canvas.drawBitmap(bitmapAnswer,
                centreX, centreY, null);
        canvas.restore();
    }
}

