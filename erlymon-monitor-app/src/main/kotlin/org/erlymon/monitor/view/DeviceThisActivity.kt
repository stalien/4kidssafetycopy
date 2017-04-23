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
import kotlinx.android.synthetic.main.content_intro.*
import kotlinx.android.synthetic.main.list_device.*

class DeviceThisActivity : BaseActivity<DevicePresenter>(), DeviceView {

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
        setContentView(R.layout.activity_thisdevice)









        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = DevicePresenterImpl(this, this)


        val device = intent.getParcelableExtra<Device>("device")
        logger.debug("DEVICE ID: " + device?.id + " DEVICE: " + device?.toString())
        nameDevice.setText(device?.name)
        identifierID.setText(device?.uniqueId)
        category.setText(device?.category)
        sim.setText(device?.phone)

        fab_device_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        //    super.onActivityResult(requestCode, resultCode, data)
        //     if (requestCode === REQUEST_CODE && resultCode === Activity.RESULT_OK) {
        if (data != null) {
            val barcode = data.getParcelableExtra <Barcode>("barcode")
            //             identifierID.post(Runnable { identifierID.setText(barcode.displayValue) })
            identifierID.setText(barcode.displayValue)

        }
        //   }
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
        device.name = nameDevice.text.toString()
        device.uniqueId = identifierID.text.toString()
        device.category = "arrow"
        device.status = "unknown"
        device.phone = sim.text.toString()
        return device
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeviceActivity::class.java)
    }
}