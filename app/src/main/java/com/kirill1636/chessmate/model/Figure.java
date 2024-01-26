package com.kirill1636.chessmate.model;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public abstract class Figure {
    public boolean isMoved = false;
    private ImageView myImage;
    public String color;

    public Figure(String color, Context context) {
        this.color = color;
        setUpImage(context);
    }

    private void setUpImage(Context context) {
        String url = "@drawable/" + this.color + "_" + this.getClass().getSimpleName().toLowerCase();
        ImageView i = new ImageView(context);
        int imageResource = context.getResources().getIdentifier(
                url, null, context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(imageResource);
        i.setImageDrawable(drawable);
        // set the ImageView bounds to match the Drawable's dimensions
        i.setAdjustViewBounds(true);
        i.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        this.myImage = i;
        int a = 0;
        // Add the ImageView to the layout and set the layout as the content view.
        //root.addView(i);
    }

    public ImageView getMyImage() {
        return myImage;
    }

    public abstract List<Coordinates> calculatePossibleMoves(Coordinates figureCoordinates, Board board);

    @Override
    public String toString() {
        return  getClass().getSimpleName() + "{" +
                "isMoved=" + isMoved +
                ", color='" + color + '\'' +
                '}';
    }
}
