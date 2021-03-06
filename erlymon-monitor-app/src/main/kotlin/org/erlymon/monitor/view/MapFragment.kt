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
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.view.RxView
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.util.GeoPoint
import org.slf4j.LoggerFactory
import java.util.*

import kotlinx.android.synthetic.main.fragment_map.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.model.data.Event
import org.erlymon.core.model.data.Position
import org.erlymon.core.presenter.MapPresenter
import org.erlymon.core.presenter.MapPresenterImpl
import org.erlymon.core.presenter.UsersListPresenterImpl
import org.erlymon.core.view.MapView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.map.marker.MarkerWithLabel
import org.osmdroid.util.BoundingBoxE6
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class MapFragment : BaseFragment<MapPresenter>(), MapView {

    private inner class DevicesMarkerClusterer(ctx: Context) : RadiusMarkerClusterer(ctx) {

        fun remove(marker: Marker) {
            mItems.remove(marker)
        }
    }

    private var mRadiusMarkerClusterer: DevicesMarkerClusterer? = null
    private var markers: MutableMap<Long, MarkerWithLabel> = HashMap()
    private var mLocationOverlay: MyLocationNewOverlay? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    private var arrowDrawable: Drawable? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MapPresenterImpl(context, this)

        arrowDrawable = resources.getDrawable(R.drawable.ic_arrow)

        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)


        RxView.clicks(myPlace)
                .compose(RxPermissions.getInstance(context).ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
                .subscribe({ granted ->
                    if (granted) {
                        if (myPlace.isChecked) {
                            mLocationOverlay?.enableFollowLocation()
                            mLocationOverlay?.enableMyLocation()
                            mLocationOverlay?.runOnFirstFix {
                                mapview.post {
                                    try {
                                        mapview.controller.setZoom(15)
                                        mapview.controller.animateTo(GeoPoint(
                                                mLocationOverlay!!.lastFix.latitude,
                                                mLocationOverlay!!.lastFix.longitude
                                        ))
                                        mapview.postInvalidate()
                                    } catch (e: Exception) {

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
                })
    }

    override fun onResume() {
        super.onResume()

        mapview.controller.setZoom(4)
        mapview.controller.animateTo(GeoPoint(55.7559067, 37.6171875))





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
        //mLocationOverlay?.disableFollowLocation()
       // mLocationOverlay?.disableMyLocation()

      //  mapview.overlays.remove(mLocationOverlay)
       // mapview.overlays.remove(mRadiusMarkerClusterer)
       // markers.clear()
        super.onPause()
    }

    fun animateTo(geoPoint: GeoPoint, zoom: Int) {
        mapview.controller.setZoom(zoom)
        mapview.controller.animateTo(geoPoint)
        mapview.postInvalidate()
    }

    fun updateUnitMarker(device: Device, position: Position) {
        try {
            logger.debug("UPDATE MARKER: device: $device position: $position")
            var marker: MarkerWithLabel? = markers[device.id]
            if (marker == null) {
                marker = MarkerWithLabel(mapview, device.name)
                mRadiusMarkerClusterer?.add(marker)
                markers.put(device.id, marker)

            }
            marker.title = device.name
            if (position.fixTime != null) {
                marker.snippet = position.fixTime.toString()
            }

            marker.setIcon(arrowDrawable)
            if (position.course != null) {
                marker.rotation = position.course
            }
            marker.position = GeoPoint(position.latitude, position.longitude)
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun showEvent(event: Event) {
        event.devices?.forEach { device ->
            event.positions?.forEach { position ->
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

    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)
    }
}