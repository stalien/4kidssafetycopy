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

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import io.realm.RealmResults
import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.list_device.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.presenter.DevicesListPresenter
import org.erlymon.core.presenter.DevicesListPresenterImpl
import org.erlymon.core.view.DevicesListView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.DevicesAdapter
import android.support.v4.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.fragment_geofences.*
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.list_device.view.*
import org.erlymon.core.model.data.Geofence
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.GeofencesListPresenter
import org.erlymon.core.presenter.GeofencesListPresenterImpl
import org.erlymon.core.view.GeofencesListView
import org.erlymon.monitor.view.adapter.GeofencesAdapter


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class GeofencesFragment : BaseFragment<GeofencesListPresenter>(), GeofencesListView {

    interface OnActionGeofenceListener {
        fun onEditGeofence(device: Geofence)
        fun onRemoveGeofence(device: Geofence)
        fun onLoadPositions(device: Geofence)
        fun onShowOnMapGeofence(device: Geofence)


    }

    private var listener: OnActionGeofenceListener? = null


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as OnActionGeofenceListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context!!.toString() + " must implement OnActionGeofenceListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_geofences, container, false)


    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = GeofencesListPresenterImpl(context, this)


        //lv_devices.adapter = DevicesAdapter(context, storage.devicesSorted)

        lv_geofences.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val geofence = lv_geofences.getItemAtPosition(position) as Geofence
                val popupMenu = PopupMenu(context, view)
                popupMenu.inflate(R.menu.fragment_geofences_popupmenu)
                popupMenu.setOnMenuItemClickListener(OnExecPopupMenuItem(geofence))
                popupMenu.show()
            }
        }





    }

    override fun onResume() {
        super.onResume()
        presenter?.onLoadGeofencesCache()
    }

    override fun showData(data: RealmResults<Geofence>) {
        lv_geofences.adapter = GeofencesAdapter(context, data)
    }

    override fun showError(error: String) {
        makeToast(lv_devices, error)


    }

    private inner class OnExecPopupMenuItem(internal var geofence: Geofence) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(item: MenuItem): Boolean {
            // Toast.makeText(PopupMenuDemoActivity.this,
            // item.toString(), Toast.LENGTH_LONG).show();
            // return true;
            when (item.itemId) {
                R.id.action_geofence_edit -> {
                    listener!!.onEditGeofence(geofence)
                    return true
                }
                R.id.action_geofence_remove -> {
                    listener!!.onRemoveGeofence(geofence)
                    return true
                }
                R.id.action_show_on_map_geofence -> {
                    listener!!.onShowOnMapGeofence(geofence)
                    return true
                }
                else -> return false
            }
        }
    }

    /*
    companion object {
        private val logger = LoggerFactory.getLogger(DevicesFragment::class.java)

        fun newIntent(devices: Array<out Device>?) : DevicesFragment {
            val fragment = DevicesFragment()
            val args = Bundle()
            args.putParcelableArray("devices", devices)
            fragment.arguments = args
            return fragment;
        }
    }*/
}