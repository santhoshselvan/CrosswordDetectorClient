package com.example.tsanthosh.crosswordscan;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class CustomView extends View {

    private Rect rectangle;
    private List<List<Integer>> crosswordMatrix;

    public CustomView(Context context, AttributeSet attrs) {

        super(context, attrs);
        int x = 0;
        int y = 0;
        int sideLength = 200;

        // create a rectangle that we'll draw later
        rectangle = new Rect(x, y, sideLength, sideLength);

    }

    public void setCrosswordMatrix(List<List<Integer>> crosswordMatrix) {
        this.crosswordMatrix = crosswordMatrix;
        invalidate();
    }

    public int getRandomSingleColor(){
        return (int) (0xff * Math.random());
    }


    public Paint getRandomPaint(){
        Paint paint = new Paint();
        paint.setARGB(255, getRandomSingleColor(), getRandomSingleColor(), getRandomSingleColor());
        return paint;
    }

    public Paint getCellColorForValue(int value){

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(value == -1? Color.BLACK: Color.WHITE);
        return paint;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.CYAN);

        if (crosswordMatrix != null) {
            int canvasWidth = canvas.getWidth();
            int crosswordWidth = canvasWidth;
            int crosswordCellWidth = crosswordWidth / crosswordMatrix.get(0).size();

            for(int i = 0; i < crosswordMatrix.size(); i++){
                for(int j= 0; j < crosswordMatrix.get(i).size(); j++){
                    Rect r = new Rect(j * crosswordCellWidth, i * crosswordCellWidth, (j + 1) * crosswordCellWidth, (i + 1) * crosswordCellWidth);

                    Paint paint = getCellColorForValue(crosswordMatrix.get(i).get(j));
                    canvas.drawRect(r, paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(r, paint);
                }
            }
        }


    }

}