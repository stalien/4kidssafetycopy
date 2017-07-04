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
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.content_device.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.presenter.DevicePresenter
import org.erlymon.core.presenter.DevicePresenterImpl
import org.erlymon.core.view.DeviceView
import org.erlymon.monitor.R
import org.slf4j.LoggerFactory
import android.view.View
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.activity_geofence.*
import kotlinx.android.synthetic.main.content_geofence.*
import kotlinx.android.synthetic.main.content_intro.*
import kotlinx.android.synthetic.main.list_device.*
import org.erlymon.core.model.data.Geofence
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.GeofencePresenter
import org.erlymon.core.presenter.GeofencePresenterImpl
import org.erlymon.core.view.GeofenceView
import org.erlymon.monitor.MainPref
import java.util.*

class GeofenceActivity : BaseActivity<GeofencePresenter>(), GeofenceView {


    val REQUEST_CODE = 100
    val PERMISSION_REQUEST = 200

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence)

        setSupportActionBar(toolbar_geo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = GeofencePresenterImpl(this, this)


        val geofence = intent.getParcelableExtra<Geofence>("geofence")
        logger.debug("Geofence ID: " + geofence?.id + " GEOFENCE: " + geofence?.toString())
        geofence_name.setText(geofence?.name)
        geofence_description.setText(geofence?.description)

        fab_geofence_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }




    override fun showData(data: Geofence) {
        logger.debug(geofence.toString())
        val intent = Intent()
        intent.putExtra("geofence", geofence)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun getGeofenceId(): Long {
        val geofence = intent.getParcelableExtra<Geofence>("geofence")
        logger.debug("getGeofenceId => GEOFENCE: " + geofence)
        return if (geofence != null) geofence.id else 0
    }





    override fun getGeofence(): Geofence {
        var geofence = intent.getParcelableExtra<Geofence>("geofence")
        if (geofence == null) {
            geofence = Geofence()
        }
        geofence.name = geofence_name.text.toString()
        geofence.description = geofence_description.text.toString()
        geofence.area = "CIRCLE (48.6914811873322 44.4939265180183, 100.0)"

        return geofence
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GeofenceActivity::class.java)
    }
}
