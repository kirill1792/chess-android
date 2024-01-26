package com.kirill1636.chessmate.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.kirill1636.chessmate.model.Coordinates;
import com.kirill1636.chessmate.model.Figure;
import com.kirill1636.chessmate.model.GameContext;

import java.util.List;

public class MyCanvas extends View {

    public Paint paint;
    private GameContext gameContext;


    public MyCanvas(Context context) {
        super(context);
        init(context);
    }

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        //setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getWidth()));
        //setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        int a = getWidth();
        int b = getMeasuredHeight();
        int c = 0;
    }

    public void setContext(GameContext context){
        this.gameContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        super.onDraw(canvas);
        drawBoard(canvas);
        if(gameContext.getSelectedFigure() != null){
            Coordinates coordinates = gameContext.getBoard().getElementCoordinates(gameContext.getSelectedFigure());
            drawCell(canvas, coordinates.getRow(), coordinates.getColumn());
            markPossibleFields(canvas);
        }
    }
    private void drawCell(Canvas canvas, int row, int column){
        int squareSideLength = getWidth() / 8;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        refreshDrawableState();
        canvas.drawRect(column * squareSideLength,row * squareSideLength,
                column * squareSideLength + squareSideLength,
                row * squareSideLength + squareSideLength, paint);
    }

    private void drawBoard(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        int baseX = 0;
        int baseY = 0;
        int squareSideLength = getWidth() / 8;
        int currentX = baseX;
        int currentY = baseY;
        String whiteColor = "#FFE4B5";
        String brownColor = "#8B4513";
        String currentColor = whiteColor;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                paint.setColor(Color.parseColor(currentColor));
                canvas.drawRect(currentX , currentY, currentX + squareSideLength, currentY + squareSideLength , paint);
                currentX += squareSideLength;
                if(j < 7){
                    if (currentColor.equals(whiteColor)){
                        currentColor = brownColor;
                    }
                    else {
                        currentColor = whiteColor;
                    }
                }
            }
            currentX = baseX;
            currentY += squareSideLength;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawRect(0,0, canvas.getWidth(), canvas.getWidth(), paint);
    }

    public void setImages() {
        for (int i = 0; i < gameContext.getBoard().getFields().size(); i++) {
            for (int j = 0; j < gameContext.getBoard().getFields().get(i).size(); j++) {
                if(gameContext.getBoard().getFields().get(i).get(j) != null) {
                    Figure figure = gameContext.getBoard().getFields().get(i).get(j);
                    int xPos = j * (100 + 35);
                    int yPos = 140 + i * (100 + 35) + 12;
//                    int xPos = j * (100 + 35);
//                    int yPos = 65 + i * (100 + 35) + 12;
                    figure.getMyImage().setX(xPos);
                    figure.getMyImage().setY(yPos);
                    figure.getMyImage().setLayoutParams(new ViewGroup.LayoutParams(135, 135));
                    figure.getMyImage().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void markPossibleFields(Canvas canvas){
        List<Coordinates> possibleMoves = gameContext.getCoordinates();
        System.out.println(possibleMoves);
        for(Coordinates coordinates: possibleMoves){
            if(gameContext.getBoard().getFields().get(coordinates.getRow()).get(coordinates.getColumn()) == null){
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(4);
                canvas.drawCircle(coordinates.getColumn() * (100 + 35) + 65, 65 + coordinates.getRow() * (100 + 35) + 12, 15, paint);
                //java.lang.NullPointerException: Attempt to invoke interface method 'java.util.Iterator java.util.List.iterator()' on a null object reference
            }
        }
    }
}
