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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.content_device.*
import kotlinx.android.synthetic.main.content_thisdevice.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.presenter.DevicePresenter
import org.erlymon.core.presenter.DevicePresenterImpl
import org.erlymon.core.view.DeviceView
import org.erlymon.monitor.R
import org.slf4j.LoggerFactory

 class DeviceThisActivity : BaseActivity<DevicePresenter>(),
        DeviceView
      //  ,DevicesFragment.OnActionDeviceListener
{

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

        presenter = DevicePresenterImpl(this, this)


        val device = intent.getParcelableExtra<Device>("device")
        logger.debug("DEVICE ID: " + device?.id + " DEVICE: " + device?.toString())
        nameDeviceEdit.setText(device?.name)
        identifierIDEdit.setText(device?.uniqueId)
        simEdit.setText(device?.phone)
        device_status_edit.setText(device?.status)

        fab_device_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }

        showOnMapBtn.setOnClickListener {
//            onShowOnMap(device)
        }
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
        return device
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeviceActivity::class.java)
    }
}