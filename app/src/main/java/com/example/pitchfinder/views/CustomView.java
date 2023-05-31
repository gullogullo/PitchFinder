package com.example.pitchfinder.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

import org.puredata.core.PdBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
//import static java.lang.Math.round;

public class CustomView extends View {


    private Bitmap bitmapVolume, bitmapVolumeToDraw;
    public boolean bitmapSelection = false;
    private int bitmapPositionX = 0;
    private int bitmapPositionY = 0;
    public StringBuilder dataStringView = new StringBuilder();

    private Paint mPaint;
    public int startNumberBands = 3;
    final float maxScaleFactor = 3f;
    int numberBands = startNumberBands;
    private final int numberLevels = 1;
    public float lowestFreq = 500f; //62.5f;
    public float highestFreq = 4000f; //8000f;
    private double scaleFactor;
    private List<Paint[]> paintList = Collections.emptyList();
    public int viewWidth;
    private int viewHeight;
    RectF rect = new RectF();
    private ScaleGestureDetector mScaleDetector;
    public float mScaleFactor = 1.f;
    public float mPosX;
    //private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastTouchXDown;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    public float stepFreq;
    public float stepAmp;
    public float randomTestFreq;

    // public int panning = 0;
    public String doubletone = "bangMyTone";
    public int doubleToneInit = 1;
    public float timbreTau = 0.1f;
    public int delayDouble = 250;
    // public boolean testBool = false;
    // private int frequenciesTotal = 250;
    // private int testTotal = 3;




    public CustomView(Context context) {
        super(context);

        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }


    public void init() {

        // FROM ARRAYLIST TO MATRIX
        float[][][] hslMatrix = new float[numberLevels][startNumberBands][3];
        Paint[][] paintMatrix = new Paint[numberLevels][startNumberBands];
        //Bitmap[][] bitmapMatrix = new Bitmap[numberLevels][startNumberBands];

        bitmapVolume = BitmapFactory.decodeResource(getResources(),
                com.example.pitchfinder.R.drawable.sound_on2);

        for(int i = 0; i<numberLevels; i++){
            for(int j = 0; j<startNumberBands; j++){
                float[] hslRect = new float[]{0f, 0f, 0.55f};
                hslRect[0] = j * 330/startNumberBands;
                hslRect[1] = 0.7f - i * 0.5f/numberLevels;
                int colorRect = ColorUtils.HSLToColor(hslRect);
                hslMatrix[i][j] = hslRect;
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(colorRect);
                paintMatrix[i][j] = mPaint;
                //bitmapMatrix[i][j] = bitmapVolume;
            }
        }
        //hslList = Arrays.asList(hslMatrix);
        paintList = Arrays.asList(paintMatrix);
        //bitmapList = Arrays.asList(bitmapMatrix);

        PdBase.sendMessage("bangInit", "bang");

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
    }

    public String randomTestFreq() {

        Random r = new Random();
        double stepPower = Math.log(highestFreq/lowestFreq)/log(2);
        stepPower = lowestFreq* pow(2,stepPower/(startNumberBands*((int) maxScaleFactor)-1)*
                r.nextInt(startNumberBands*((int) maxScaleFactor)));
        randomTestFreq = (float)stepPower;
        PdBase.sendFloat("freqTest CustomView", randomTestFreq);
        String currentTimeTestFreq = new SimpleDateFormat("HH:mm:ss.SSS",
                Locale.getDefault()).format(new Date());
        //Log.e("Test Frequency:", randomTestFreq + " " + currentTimeTestFreq);
        return(String.valueOf(randomTestFreq));
    }

    // CREATE AN ARRAY OF RANDOMLY DISTRIBUTED SELECTED FREQUENCIES AND SET THEM IN
    // HIGHLIGHT COLOR METHOD
    public List customTestFreq(int testsPerFreq,
                               int testFreq1,
                               int testFreq2,
                               int testFreq3,
                               int testFreq4,
                               int testFreq5) {

        List<Integer> list = new ArrayList<>();
        List<Integer> testList = new ArrayList<>();
        for (int t=0;t<testsPerFreq; t++) {
            list.add(testFreq1);
            list.add(testFreq2);
            list.add(testFreq3);
            list.add(testFreq4);
            list.add(testFreq5);
        }
        Random randomizer = new Random();
        for (int t=0;t<testsPerFreq*3; t++) {
            Integer random = randomizer.nextInt(list.size());
            testList.add(list.get(random));
            list.remove(list.get(random));
        }
        return(testList);
    }


    public float equalLoudness(float freq, float phon) {
        float[][] iso226Table = new float[][]{{20f, 0.532f, -31.6f, 78.5f},
                {25f, 0.506f, -27.2f, 68.7f}, {31.5f, 0.480f, -23.0f, 59.5f},
                {40f, 0.455f, -19.1f, 51.1f}, {50f, 0.432f, -15.9f, 44.0f},
                {63f, 0.409f, -13.0f, 37.5f}, {80f, 0.387f, -10.3f, 31.5f},
                {100f, 0.367f, -8.1f, 26.5f}, {125f, 0.349f, -6.2f, 22.1f},
                {160f, 0.330f, -4.5f, 17.9f}, {200f, 0.315f, -3.1f, 14.4f},
                {250f, 0.301f, -2.0f, 11.4f}, {315f, 0.288f, -1.1f, 8.6f},
                {400f, 0.276f, -0.4f, 6.2f}, {500f, 0.267f, 0.0f, 4.4f},
                {630f, 0.259f, 0.3f, 3.0f}, {800f, 0.253f, 0.5f, 2.2f},
                {1000f, 0.250f, 0.0f, 2.4f}, {1250f, 0.246f, -2.7f, 3.5f},
                {1600f, 0.244f, -4.1f, 1.7f}, {2000f, 0.243f, -1.0f, -1.3f},
                {2500f, 0.243f, 1.7f, -4.2f}, {3150f, 0.243f, 2.5f, -6.0f},
                {4000f, 0.242f, 1.2f, -5.4f}, {5000f, 0.242f, -2.1f, -1.5f},
                {6300f, 0.245f, -7.1f, 6.0f}, {8000f, 0.254f, -11.2f, 12.6f},
                {10000f, 0.271f, -10.7f, 13.9f}, {12500f, 0.301f, -3.1f, 12.3f}};
        float distance = Math.abs(iso226Table[0][0] - freq);
        int freqIndex = 0;
        for(int c = 0; c < 29; c++){
            float cdistance = Math.abs(iso226Table[c][0] - freq);
            if(cdistance < distance){
                freqIndex = c;
                distance = cdistance;
            }
        }
        float freqApprox = iso226Table[freqIndex][0];
        float t_f = iso226Table[freqIndex][3];
        float l_u = iso226Table[freqIndex][2];
        float alpha_f = iso226Table[freqIndex][1];
        float af = 4.47f*(float)pow(10,-3)*((float)(pow(10,0.025*phon))-1.14f)+
                (float)(pow(0.4*pow(10,(t_f+l_u)/10-9),alpha_f));
        return(10/alpha_f*(float)log10(af)-l_u+94+15);
    }

    public void highlightColor(float x, float y) {
        bitmapSelection = true;
        int realBands = startNumberBands*((int) (floor(mScaleFactor)));
        int xIndex = max(0, min((int) floor(x/viewWidth*realBands), realBands-1));
        int yIndex = max(0, min((int) floor(y/viewHeight*numberLevels), numberLevels-1));
        double stepPower = Math.log(highestFreq/lowestFreq)/log(2);
        stepFreq = (float) (lowestFreq* pow(2,stepPower/(realBands-1)*xIndex));
        // ATTENZIONE! AGGIUNTO x3!!!
        // TODO CHECK GAIN
        stepAmp = (float)(( (double) numberLevels*3-yIndex)/ numberLevels*3*65);
        //Log.e("stepAmp:", String.valueOf(stepAmp));
        float equaldB = equalLoudness(stepFreq, stepAmp);
        PdBase.sendFloat("freq", stepFreq);
        PdBase.sendFloat("tauTest", timbreTau);
        PdBase.sendFloat("tau", timbreTau);
        PdBase.sendFloat("gainTotal", equaldB);
        String currentTimeFreqAmp = new SimpleDateFormat("HH:mm:ss.SSS",
                Locale.getDefault()).format(new Date());
        //Log.e("Frequency:", stepFreq + " " + currentTimeFreqAmp);
        //Log.e("Amplitude:", stepAmp + " " + currentTimeFreqAmp);
        //Log.e("dB corrispettivi", String.valueOf(equaldB));
        float equaldBTest = equalLoudness(randomTestFreq, stepAmp);
        PdBase.sendFloat("gainTotalTest", equaldBTest);
        PdBase.sendFloat("freqTest", randomTestFreq*doubleToneInit);
        PdBase.sendFloat("delayTime", delayDouble);
        PdBase.sendMessage(doubletone, "bang");
        dataStringView.append("Frequency, ").append(stepFreq).append(", ").append(currentTimeFreqAmp).append("\n");


        // FROM ARRAYLIST TO MATRIX
        float[][][] hslMatrix = new float[numberLevels][realBands][3];
        Paint[][] paintMatrix = new Paint[numberLevels][realBands];
        //Bitmap[][] bitmapMatrix = new Bitmap[numberLevels][realBands];

        for(int i = 0; i<numberLevels; i++){
            for(int j = 0; j<realBands; j++){
                float[] hslRect = new float[]{0f, 0f, 0.55f};
                hslRect[0] = j * 330/realBands;
                hslRect[1] = 0.7f - i * 0.5f/numberLevels;
                int colorRect = ColorUtils.HSLToColor(hslRect);
                hslMatrix[i][j] = hslRect;
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(colorRect);
                paintMatrix[i][j] = mPaint;
                //bitmapMatrix[i][j] = bitmapVolume;
            }
        }

        paintMatrix[yIndex][xIndex].setColor(
                ColorUtils.HSLToColor(
                        new float[]{hslMatrix[yIndex][xIndex][0],
                                (hslMatrix[yIndex][xIndex][1] = 1f),
                                hslMatrix[yIndex][xIndex][2]}));

        paintList = Arrays.asList(paintMatrix);
        bitmapPositionX = xIndex;
        bitmapPositionY = yIndex;

        postInvalidate();
    }

    public void lowLightColor(float x, float y) {
        bitmapSelection = false;
        int realBands = startNumberBands*((int) (floor(mScaleFactor)));
        int xIndex = max(0, min((int) floor(x/viewWidth*realBands), realBands-1));
        int yIndex = max(0, min((int) floor(y/viewHeight*numberLevels), numberLevels-1));
        float[][][] hslMatrix = new float[numberLevels][realBands][3];
        Paint[][] paintMatrix = new Paint[numberLevels][realBands];
        Bitmap[][] bitmapMatrix = new Bitmap[numberLevels][realBands];
        for(int i = 0; i<numberLevels; i++){
            for(int j = 0; j<realBands; j++){
                float[] hslRect = new float[]{0f, 0f, 0.55f};
                hslRect[0] = j * 330/realBands;
                hslRect[1] = 0.7f - i * 0.5f/numberLevels;
                int colorRect = ColorUtils.HSLToColor(hslRect);
                hslMatrix[i][j] = hslRect;
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(colorRect);
                paintMatrix[i][j] = mPaint;
            }
        }
        paintList = Arrays.asList(paintMatrix);
        postInvalidate();
    }


    public void zoomingColors() {

        int realBands = startNumberBands*((int) (floor(mScaleFactor)));
        float[][][] hslMatrix = new float[numberLevels][realBands][3];
        Paint[][] paintMatrix = new Paint[numberLevels][realBands];

        for(int i = 0; i<numberLevels; i++){
            for(int j = 0; j<realBands; j++){
                float[] hslRect = new float[]{0f, 0f, 0.55f};
                hslRect[0] = j * 330/realBands;
                hslRect[1] = 0.7f - i * 0.5f/numberLevels;
                int colorRect = ColorUtils.HSLToColor(hslRect);
                hslMatrix[i][j] = hslRect;
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(colorRect);
                paintMatrix[i][j] = mPaint;
            }
        }

        paintList = Arrays.asList(paintMatrix);
        postInvalidate();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mLastTouchXDown = x;

                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;

                    mPosX += dx;
                    mPosX = Math.max(viewWidth*(1-mScaleFactor),Math.min(mPosX,0));

                    invalidate();
                }
                else {

                    zoomingColors();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                if (mLastTouchXDown >= ev.getX()-100 && mLastTouchXDown <= ev.getX()+100) {
                    highlightColor((ev.getX()-mPosX)/mScaleFactor,ev.getY());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 5 seconds
                            lowLightColor((ev.getX()-mPosX)/mScaleFactor,ev.getY());
                        }
                    }, 1000);
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    numberBands = startNumberBands*((int) (floor(mScaleFactor)));
                }
                break;
            }
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mPosX, 0);
        canvas.scale(mScaleFactor, 1);

        int realBands = paintList.get(0).length;
        for(int i = 0; i<numberLevels; i++){
            for(int j = 0; j<realBands; j++){
                rect.set(j*viewWidth /realBands+5/mScaleFactor,
                        i*viewHeight/numberLevels+5,
                        (j+1)*viewWidth /realBands-5/mScaleFactor,
                        (i+1)*viewHeight /numberLevels-5);
                //rectMatrix[i][j] = rect;
                //canvas.drawRect(rectMatrix[i][j], paintList.get(i)[j]);
                canvas.drawRoundRect(rect, 50/mScaleFactor, 50, paintList.get(i)[j]);
                /*
                if (bitmapSelection) {
                    bitmapList.get(i)[j] = Bitmap.createScaledBitmap(bitmapList.get(i)[j],
                            viewWidth / realBands,
                            viewHeight / 3*numberLevels, true);
                    canvas.drawBitmap(bitmapList.get(i)[j],
                            j * viewWidth / realBands,
                            viewHeight/ 3*numberLevels,
                            paintList.get(i)[j]);
                }*/

            }
        }
        if (bitmapSelection) {
            bitmapVolumeToDraw = Bitmap.createScaledBitmap(bitmapVolume,
                    viewWidth / realBands / 2,
                    viewHeight / 3*numberLevels, true);
            canvas.drawBitmap(bitmapVolumeToDraw,
                    (float) (bitmapPositionX * viewWidth / realBands + viewWidth / realBands * 0.25),
                    viewHeight/ 3*numberLevels, null);
        }


        //rectList = Arrays.asList(rectMatrix);

        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scaleFactor = detector.getScaleFactor();
            mScaleFactor *= scaleFactor;
            //mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, maxScaleFactor));
            if(mScaleFactor-scaleFactor>=1) {
                Snackbar snackbar = Snackbar.make(getRootView(),
                        "Zoom: x" + (int) (floor(mScaleFactor)),Snackbar.LENGTH_SHORT);
                View view = snackbar.getView();
                TextView textView = view.findViewById(
                        com.google.android.material.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    //textView.setGravity(Gravity.BOTTOM);
                }
                snackbar.show();
            }

            if(mScaleFactor < maxScaleFactor) {

                final float centerX = detector.getFocusX();
                //final float centerY = detector.getFocusY();
                // 2 Calculating difference
                float diffX = centerX - mPosX;
                //float diffY = centerY - mPosY;
                // 3 Scaling difference
                diffX = (float) (diffX * scaleFactor - diffX);
                //diffY = diffY * scaleFactor - diffY;
                // 4 Updating image origin
                mPosX -= diffX;
                //mPosY -= diffY;
            }
            invalidate();
            return true;
        }
    }
}