package org.erlymon.monitor.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_divicelist.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_about.*
import kotlinx.android.synthetic.main.content_forgot.*
import kotlinx.android.synthetic.main.fragment_devices.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.presenter.DevicesListPresenterImpl
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.DevicesAdapter

/**
 * Created by stalien on 09.05.2017.
 */
class DeviceListActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_divicelist)

        setSupportActionBar(devicelist_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


}