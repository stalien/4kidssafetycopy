/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of Erlymon Monitor.
 *
 * Erlymon Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Erlymon Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Erlymon Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.monitor.view.map.marker;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.design.widget.Snackbar;

import org.erlymon.core.model.data.Position;
import org.erlymon.monitor.view.Utils;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by sergey on 6/25/16.
 */
public class MarkerWithLabel extends Marker {
    Paint textPaint = null;
    Paint circlePaint = null;
    String mLabel = null;
    MapView mMapView;
    private Bitmap bmpText;
    private Position position;



    public MarkerWithLabel(MapView mapView, String label) {
        super( mapView);
        mLabel = label;
        mMapView = mapView;
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);
        circlePaint.setAlpha(30);
        circlePaint.setStyle(Paint.Style.FILL);

/*        public void drawCircle(final Canvas c, final MapView osmv) {
            Paint circlePaint = new Paint();
            Point p = this.mPositionPixels;
            c.save();
            c.drawCircle(p.x, p.y, 10.0F, circlePaint);
            c.restore();
        } */


    }

    public void setTitle(String title) {
        if (mTitle == null || !mTitle.equals(title)) {
            bmpText = Utils.createDrawableText(mMapView.getContext(),title, mMapView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
        super.setTitle(title);
    }

    public void setRaduis(Float raduis) {

        Canvas canvas = new Canvas();
        Position position = new Position();
        MapView mapView ;

        mapView = mMapView;

//        drawCircle(raduis, canvas, mapView);
//        Snackbar.make(mMapView, "радиус: "+ raduis, Snackbar.LENGTH_LONG).show();


    }

    public void setRelatedObject(Object relatedObject){

        super.setRelatedObject(relatedObject);
    }



    public void draw(final Canvas c, final MapView osmv, boolean shadow) {
//        drawCircle(position, c, osmv);
        draw( c, osmv);

    }

    public void draw( final Canvas c, final MapView osmv) {

        super.draw( c, osmv, false);

 //       Position position = new Position();
 //       final float radius = position.getAccuracy() * 100.0F / (float) TileSystem.GroundResolution(position.getLatitude(), osmv.getZoomLevel());

        if (bmpText == null)
            return;

        Point p = this.mPositionPixels;  // already provisioned by Marker

        Paint textPaint = new Paint();
        c.save();

//        c.drawBitmap(bmpText, p.x - (int)(mAnchorU* bmpText.getWidth()), p.y + (int)(mAnchorV *  mIcon.getIntrinsicHeight()), textPaint);

        c.drawBitmap(bmpText, p.x - (int)(mAnchorU* bmpText.getWidth()), p.y + 20, textPaint);
//        c.drawCircle(p.x, p.y, 10.0F, circlePaint);
        c.restore();

    }

    public void drawCircle( Position position, final Canvas c, final MapView osmv) {
        super.draw( c, osmv, false);

//               Position position = new Position();
               final float radius = position.getAccuracy() / (float) TileSystem.GroundResolution(mPosition.getLatitude(), osmv.getZoomLevel());

        Point p = this.mPositionPixels;
        c.save();
        c.drawCircle(p.x, p.y, radius, circlePaint);
        c.restore();

    }
}