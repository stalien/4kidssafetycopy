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
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.content_device.*
import kotlinx.android.synthetic.main.content_thisdevice.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.model.data.Server
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.DevicePresenter
import org.erlymon.core.presenter.DevicePresenterImpl
import org.erlymon.core.view.DeviceView
import org.erlymon.monitor.R
import org.slf4j.LoggerFactory
import java.util.*

class DeviceThisActivity : BaseActivity<DevicePresenter>(),
        DeviceView
      //  ,DevicesFragment.OnActionDeviceListener
{
    interface OnActionDeviceListener {
        fun onEditDevice(device: Device)
        fun onRemoveDevice(device: Device)
        fun onLoadPositions(device: Device)
        fun onShowOnMap(device: Device)
        fun onSendCommand(device: Device)
    }

    private var listener: DeviceThisActivity.OnActionDeviceListener? = null


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
        setContentView(R.layout.activity_thisdevice)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

/*        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = applicationContext as OnActionDeviceListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(applicationContext!!.toString() + " must implement OnActionDeviceListener")
        } */

        presenter = DevicePresenterImpl(this, this)


        val device = intent.getParcelableExtra<Device>("device")
        logger.debug("DEVICE ID: " + device?.id + " DEVICE: " + device?.toString())
        nameDeviceEdit.setText(device?.name)
        identifierIDEdit.setText(device?.uniqueId)
        simEdit.setText(device?.phone)
        device_status_edit.setText(device?.status)
//        showOnMapSwitch.setChecked(device.showOnMap)

        if (device.showOnMap != null) {
            showOnMapSwitch.setChecked(device.showOnMap)
        }

        if (device.pedometer != null) {
            pedometrSwitch.setChecked(device.pedometer)
        }


        fab_device_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }

        callOnDeviceBtn.setOnClickListener {
            onCallDevice(device)
        }

        showOnMapBtn.setOnClickListener {
            makeToast(toolbar, "show on Map")

        }

        sendMsgBtn.setOnClickListener {
            makeToast(toolbar, "send Message")
        }

        showOnMapSwitch.setOnClickListener {
            if (showOnMapSwitch.isChecked){
                device.showOnMap = true
            }
            else {
                device.showOnMap = false
            }
        }

        pedometrSwitch.setOnClickListener {
            if (pedometrSwitch.isChecked){
                device.pedometer = true
            }
            else {
                device.pedometer = false
            }
        }


    }

     fun showSession(user: User) {
        DeviceThisActivity.logger.debug("showSession => " + user.toString())
        val intent = Intent(this@DeviceThisActivity, MainActivity::class.java)
                .putExtra("server", intent.getParcelableExtra<Server>("server"))
                .putExtra("session", user)
        startActivity(intent)
    }

    override fun showData(data: Device) {
        logger.debug(device.toString())
        val intent = Intent()
        intent.putExtra("device", device)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun getDeviceId(): Long {
        val device = intent.getParcelableExtra<Device>("device")
        logger.debug("getDeviceId => DEVICE: " + device)
        return if (device != null) device.id else 0
    }

    override fun getDevice(): Device {
        var device = intent.getParcelableExtra<Device>("device")
        if (device == null) {
            device = Device()
        }
        device.name = nameDeviceEdit.text.toString()
        device.uniqueId = identifierIDEdit.text.toString()
//        device.category = "arrow"
//        device.status = "unknown"
        device.phone = simEdit.text.toString()
        device.lastUpdate = null
        device.showOnMap = showOnMapSwitch.isChecked
        device.pedometer = pedometrSwitch.isChecked
        device.watchBattery = checkBatterySwitch.isChecked
        device.notifyOnRemoval = onHandSwitch.isChecked
        return device
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    fun onCallDevice(device: Device) {

        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        callIntent.data = Uri.parse("tel:" + device.phone.toString())
        startActivity(callIntent)

        // val callIntent = Intent(Intent.ACTION_CALL)
        // callIntent.data = Uri.parse("tel:" + device.phone.toString())
        // startActivity(callIntent)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeviceActivity::class.java)
    }
}