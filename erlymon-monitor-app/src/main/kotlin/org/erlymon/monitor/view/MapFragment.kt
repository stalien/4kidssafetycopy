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
package org.erlymon.monitor.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.jakewharton.rxbinding.view.RxView
import com.squareup.picasso.Picasso
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.content_devices.*
import kotlinx.android.synthetic.main.content_main.*

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.util.GeoPoint
import org.slf4j.LoggerFactory
import java.util.*

import kotlinx.android.synthetic.main.fragment_map.*
import org.erlymon.core.model.data.*
import org.erlymon.core.presenter.MapPresenter
import org.erlymon.core.presenter.MapPresenterImpl
import org.erlymon.core.presenter.UsersListPresenterImpl
import org.erlymon.core.view.MapView
import org.erlymon.monitor.MainPref
import org.erlymon.monitor.R
import org.erlymon.monitor.view.map.marker.MarkerWithLabel
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.overlays.GroundOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBoxE6
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class MapFragment : BaseFragment<MapPresenter>(),
        MapView,
        MapEventsReceiver {

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        InfoWindow.closeAllInfoWindowsOn(mapview);
//        Toast.makeText(context, "Tapped", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        val circle = Polygon(context)
        circle.points = Polygon.pointsAsCircle(p, 50.0)
        circle.setFillColor(0x1200FF00)
        circle.setStrokeColor(Color.GREEN)
        circle.setStrokeWidth(2.00F);

       val myGroundOverlay = GroundOverlay(context);
        myGroundOverlay.setPosition(p);
        myGroundOverlay.setImage(getResources().getDrawable(R.drawable.logo).mutate());
        myGroundOverlay.setDimensions(20.0f);
        mapview.getOverlays().add(1,myGroundOverlay);

        circle.setInfoWindow(BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapview));
        circle.setTitle("Centered on "+ p?.getLatitude() +","+ p?.getLongitude());

        mapview.getOverlays().add(1,circle);
        mapview.postInvalidate()

        return true //To change body of created functions use File | Settings | File Templates.
    }



    private inner class DevicesMarkerClusterer(ctx: Context) : RadiusMarkerClusterer(ctx) {

        fun remove(marker: Marker) {
            mItems.remove(marker)
        }
    }



    private var mRadiusMarkerClusterer: DevicesMarkerClusterer? = null
    private var markers: MutableMap<Long, MarkerWithLabel> = HashMap()
    private var mLocationOverlay: MyLocationNewOverlay? = null
    private var mMarker: Marker? = null
    private var mPosition: Position? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    private var arrowDrawable: Drawable? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MapPresenterImpl(context, this)

        val mapEventsOverlay = MapEventsOverlay(context, this)


        arrowDrawable = resources.getDrawable(R.drawable.arrow_offline)

        mapview.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)
        mapview.minZoomLevel = 4

        mapview.getOverlays().add(0, mapEventsOverlay)



//        mapview.setBuiltInZoomControls(true)

        btnZoomIn.setOnClickListener {

        mapview.controller.zoomIn()
        MainPref.defaultZoom = mapview.zoomLevel
//        makeToast(mapview, "zoomIn")

        }

        btnZoomOut.setOnClickListener {


            if (mapview.canZoomOut()) {
                mapview.controller.zoomOut()
                MainPref.defaultZoom = mapview.zoomLevel



//                makeToast(mapview, "zoomLevel: " + mapview.zoomLevel)
            }
//            else  makeToast(mapview, "maximum_zoomOut" + mapview.minZoomLevel)

        }



        mapview.controller.animateTo(GeoPoint(MainPref.defaultLatitude.toDouble(), MainPref.defaultLongitude.toDouble()))
        mapview.controller.setZoom(MainPref.defaultZoom)



        myPlace.setOnClickListener{
            RxPermissions.getInstance(context)
                    .request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe({ granted ->
                if (granted) {

            mLocationOverlay?.enableFollowLocation()
            mLocationOverlay?.enableMyLocation()
            mLocationOverlay?.runOnFirstFix {
                mapview.post {
                    try {
                        mapview.controller.setZoom(MainPref.defaultZoom)
                        mapview.controller.animateTo(GeoPoint(
                                mLocationOverlay!!.lastFix.latitude,
                                mLocationOverlay!!.lastFix.longitude
                        ))
                        mapview.postInvalidate()
                    } catch (e: Exception) {

                        // try find position error
                        //makeToast(myPlace, error(e))

                    }
                }
            }}


        })}


        /*RxView.clicks(myPlace)
                .compose(RxPermissions.getInstance(context).ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
                .subscribe({ granted ->
                    if (granted) {
                        if (myPlace.isChecked) {
                            mLocationOverlay?.enableFollowLocation()
                            mLocationOverlay?.enableMyLocation()
                            mLocationOverlay?.runOnFirstFix {
                                mapview.post {
                                    try {
                                        mapview.controller.setZoom(MainPref.defaultZoom)
                                        mapview.controller.animateTo(GeoPoint(
                                                mLocationOverlay!!.lastFix.latitude,
                                                mLocationOverlay!!.lastFix.longitude
                                        ))
                                        mapview.postInvalidate()
                                    } catch (e: Exception) {

                                        // try find position error
                                        //makeToast(myPlace, error(e))

                                    }
                                }
                            }
                        } else {
                            mLocationOverlay?.disableFollowLocation()
                            mLocationOverlay?.disableMyLocation()


                        }
                    } else {
                        myPlace.isChecked = false
                        makeToast(myPlace, getString(R.string.errorPermissionCoarseLocation))
                    }
                })*/
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
       // makeToast(mapview, "restored")

        mapview.controller.animateTo(GeoPoint(MainPref.defaultLatitude.toDouble(), MainPref.defaultLongitude.toDouble()))





        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))

        mapview.overlays.add(mRadiusMarkerClusterer)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), mapview)
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()
        mapview.getOverlays().add(mLocationOverlay)

        presenter?.onOpenWebSocket()

    }

    override fun onStart() {
        super.onStart()
        makeToast(mapview, "start")

        mapview.controller.animateTo(GeoPoint(MainPref.defaultLatitude.toDouble(), MainPref.defaultLongitude.toDouble()))

        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))

        mapview.overlays.add(mRadiusMarkerClusterer)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), mapview)
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()
        mapview.getOverlays().add(mLocationOverlay)

        presenter?.onOpenWebSocket()
    }

    override fun onResume() {
        super.onResume()

//        mapview.controller.setZoom(MainPref.defaultZoom)
//      mapview.controller.animateTo(GeoPoint(55.7559067, 37.6171875))

        mapview.controller.animateTo(GeoPoint(MainPref.defaultLatitude.toDouble(), MainPref.defaultLongitude.toDouble()))





        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))

        mapview.overlays.add(mRadiusMarkerClusterer)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), mapview)
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()
        mapview.getOverlays().add(mLocationOverlay)

        presenter?.onOpenWebSocket()
    }

    override fun onPause() {
        mLocationOverlay?.disableFollowLocation()
        mLocationOverlay?.disableMyLocation()

        mapview.overlays.remove(mLocationOverlay)
        // mapview.overlays.remove(mRadiusMarkerClusterer)
        // markers.clear()
        super.onPause()
    }

    fun mMarker() {

    }

    fun animateTo(geoPoint: GeoPoint, zoom: Int) {
        mapview.controller.setZoom(zoom)
//        MainPref.defaultZoom = zoom
        mapview.controller.animateTo(geoPoint)
        mapview.postInvalidate()
    }

    fun updateUnitMarker(device: Device, position: Position) {
        try {
            logger.debug("UPDATE MARKER: device: $device position: $position")
            var marker: MarkerWithLabel? = markers[device.id]
            if (marker == null) {
                marker = MarkerWithLabel(mapview, device.name, 0.0F)
                mRadiusMarkerClusterer?.add(marker)
                markers.put(device.id, marker)

            }

//            marker = MarkerWithLabel(mapview, device.name, position.accuracy)

            if(device.status == "online"){
            marker.setRaduis(position.accuracy)
            } else marker.setRaduis(0.0F)

            marker.title = device.name


//            marker.title = position.fixTime.toString()
//            marker.snippet = device.id.toString()
 /*           marker.snippet = ("id=" + device.id +
                                 ", uniqueId='" + device.uniqueId + '\'' +
                                ", status='" + device.status + '\'' +
                                ", accuracy=" + position.accuracy +
                                ", lastFix=" + position.fixTime.toString())  */

            marker.snippet = (position.address)
            marker.subDescription = (mapview.getOverlays().size.toString())

            if (device.status == "offline") {
                marker.snippet = "Device offline"
            }

            if (device.status == "online"){
            arrowDrawable = resources.getDrawable(R.drawable.arrow_online)
//                makeToast(mapview, "online")
            } else arrowDrawable = resources.getDrawable(R.drawable.arrow_offline)


            marker.setIcon(arrowDrawable)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            if (position.course != null) {
               // marker.rotation = position.course
            }
            marker.position = GeoPoint(position.latitude, position.longitude)

            marker.isDraggable = true



// override default lattitude & longitude
            MainPref.defaultLatitude = position.latitude.toString()
            MainPref.defaultLongitude = position.longitude.toString()
//

// marker is tranparent when device is offline

            if (device.status == "offline"){
                marker.alpha = 0.2F
            } else marker.alpha = 1F
//

        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }



    override fun showEvent(event: Event) {
        event.devices?.forEach { device ->
            event.positions?.forEach { position ->
//                if (device.id == position.deviceId && device.showOnMap == true) {
                if (device.id == position.deviceId) {
                    updateUnitMarker(device, position)
                }
            }
        }

        mRadiusMarkerClusterer?.invalidate()
        mapview?.postInvalidate()
    }



    override fun showError(error: String) {
        makeToast(mapview, error)
    }

    fun Marker.OnMarkerDragListener(){

    }

    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)
    }
}