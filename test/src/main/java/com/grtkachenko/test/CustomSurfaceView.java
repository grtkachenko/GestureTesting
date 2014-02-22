package com.grtkachenko.test;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceView;

/**
 * Created by gtkachenko on 22.02.14.
 */
public class CustomSurfaceView extends SurfaceView {
    public CustomSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void draw(Canvas canvas) {
        int x;

        super.draw(canvas);
    }
}
