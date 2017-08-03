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

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import io.realm.RealmResults

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_devices.*
import kotlinx.android.synthetic.main.content_intro.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main_devices.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.list_device.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.erlymon.core.model.data.*
import org.erlymon.core.presenter.MainPresenter
import org.erlymon.core.presenter.MainPresenterImpl
import org.erlymon.core.view.MainView
import org.erlymon.monitor.view.DevicesActivity
import org.erlymon.monitor.view.DevicesFragment
import org.erlymon.monitor.MainPref
import org.erlymon.monitor.Manifest
import org.erlymon.monitor.R
import org.erlymon.monitor.view.ImagePickerWithCrop.REQUEST_PICK
import org.erlymon.monitor.view.adapter.CustomFragmentPagerAdapter
import org.erlymon.monitor.view.adapter.DevicesAdapter
import org.erlymon.monitor.view.adapter.UsersAdapter
import org.erlymon.monitor.view.fragment.ConfirmDialogFragment
import org.erlymon.monitor.view.fragment.SendCommandDialogFragment
import org.osmdroid.util.GeoPoint

import org.slf4j.LoggerFactory
import org.erlymon.monitor.view.SettingsActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*

class MainActivity : BaseActivity<MainPresenter>(),
        MainView,
        NavigationView.OnNavigationItemSelectedListener,
        DevicesFragment.OnActionDeviceListener, DevicesToolboxFragment.OnActionDeviceListener,
        DeviceThisActivity.OnActionDeviceListener,
        UsersFragment.OnActionUserListener,
        GeofencesFragment.OnActionGeofenceListener,
        ConfirmDialogFragment.ConfirmDialogListener,
        SendCommandDialogFragment.SendCommandDialogListener {

    private var pagerAdapter: CustomFragmentPagerAdapter? = null
    private val PICK_IMAGE_ID = 234 // the number doesn't matter

    private var mAccountNameView: TextView? = null
    private var mAccountEmailView: TextView? = null
    private var bitmap: Bitmap? = null
//    val deviceThis = intent.getParcelableExtra<User>("device")

    public fun pickImage() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID)
        //ImagePickerWithCrop.pickImage(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(MainPref.devices == 0){
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setTitle("Устройства отсутствуют")
        alert.setMessage("Вы хотите добавить новое устройство?")
        alert.setPositiveButton("Добавить",  { dialog, whichButton ->  startActivity(Intent(this@MainActivity, DeviceActivity::class.java)) })
        alert.setNegativeButton("Отмена", { dialog, whichButton ->  MainPref.devices++ } )
        alert.show()
        }

        setSupportActionBar(toolbar)

        presenter = MainPresenterImpl(this, this, R.string.sharedLoading)

        val linearLayout = nav_view.getHeaderView(0) as LinearLayout
        mAccountNameView = linearLayout.getChildAt(1) as TextView
        mAccountEmailView = linearLayout.getChildAt(2) as TextView

        val session = intent.getParcelableExtra<User>("session")
        mAccountNameView?.text = session?.name
        mAccountEmailView?.text = session?.email

        val toggle = ActionBarDrawerToggle(
                  this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
          drawer_layout.addDrawerListener(toggle)
          toggle.syncState()

 //                        val toggle_right = ActionBarDrawerToggle(
 //              this, drawer_layout2, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
  //      drawer_layout2.addDrawerListener(toggle_right)
 //       toggle_right.syncState()


        var to_account = linearLayout.findViewById(R.id.tv_account_name) as TextView
        var to_account2 = linearLayout.findViewById(R.id.tv_account_email) as TextView
//        var pic_loader = linearLayout.findViewById(R.id.tv_app_name) as TextView


        var imageView = linearLayout.findViewById(R.id.iw4Kids) as ImageView



//        val bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.family)
//        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap)
//        roundedBitmapDrawable.isCircular = true
//        imageView.setImageDrawable(roundedBitmapDrawable)

        Picasso.with(applicationContext)
                .load("http://13.94.117.29/upload/" + MainPref.email + "/" + MainPref.userImage)
                .transform(CircularTransformation())
                .into(imageView)

        to_account.setOnClickListener {
            val intent = Intent(this@MainActivity, UserActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("user", intent.getParcelableExtra<User>("session"))
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)}
        drawer_layout.closeDrawer(GravityCompat.START)

        to_account2.setOnClickListener {
            val intent = Intent(this@MainActivity, UserActivity::class.java)
                    .putExtra("session", intent.getParcelableExtra<User>("session"))
                    .putExtra("user", intent.getParcelableExtra<User>("session"))
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)}
        drawer_layout.closeDrawer(GravityCompat.START)

        imageView.setOnClickListener {
            val intent = Intent(this@MainActivity, UserActivity::class.java)
                    .putExtra("session", intent.getParcelableExtra<User>("session"))
                    .putExtra("user", intent.getParcelableExtra<User>("session"))
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)
            drawer_layout.closeDrawer(GravityCompat.START)
            //pickImage()
            //    val intent = Intent(Intent.ACTION_PICK,
            //          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //     .putExtra("user", intent.getParcelableExtra<User>("session"))
            //   startActivityForResult(intent, REQUEST_CODE_USER_PIC)}
        }

        nav_view.setNavigationItemSelectedListener(this)

        (nav_view.menu.findItem(R.id.nav_users) as MenuItem).isVisible = session?.admin!!
        (nav_view.menu.findItem(R.id.nav_server) as MenuItem).isVisible = session.admin!!


        pagerAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        pagerAdapter?.addPage(MapFragment())
        pagerAdapter?.addPage(DevicesFragment())
//        pagerAdapter?.addPage(DevicesToolboxFragment())
        pagerAdapter?.addPage(UsersFragment())
        pagerAdapter?.addPage(GeofencesFragment())
        view_pager.setAdapter(pagerAdapter)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                logger.debug("onPageSelected, position = " + position)
                when (position) {
                    0 -> {
                        fab.visibility = View.GONE
                        supportActionBar?.setTitle(R.string.mapTitle)
                    }
                    1 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsDevices)
                    }
                    2 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsUsers)
                    }
                    3 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsGeofences)
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
//                Toast.makeText(baseContext, "Scroll", Toast.LENGTH_SHORT).show()
            }
        })

        fab.setOnClickListener {
            when (view_pager.currentItem) {
                1 -> {
                    logger.debug("Start DeviceActivity")
                    val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivity(intent)
                }
                2 -> {
                    logger.debug("Start UserActivity")
                    val intent = Intent(this@MainActivity, UserActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivity(intent)
                }
                3 -> {
                    logger.debug("Start GeofenceActivity")
                    val intent = Intent(this@MainActivity, GeofenceActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivity(intent)
                }
            }
        }




    }


    override fun onRestart() {
        super.onRestart()

        var imageView = findViewById(R.id.iw4Kids) as ImageView

        Picasso.with(applicationContext)
                .load("http://13.94.117.29/upload/" + MainPref.email + "/" + MainPref.userImage)
                .placeholder(R.drawable.userphoto_default)
                .transform(CircularTransformation())
                .into(imageView)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
            if (backPressed + 2000 > System.currentTimeMillis()) {
                presenter?.onDeleteSessionButtonClick()
            } else {
                Toast.makeText(baseContext, getString(R.string.sharedBackPressed), Toast.LENGTH_SHORT).show()
                backPressed = System.currentTimeMillis()
                finish();
                System.exit(0);
            }
        }
    }

    override fun showPosition(position: Position) {
        try {
//            (pagerAdapter?.getItem(0) as MapFragment).animateTo(GeoPoint(position.latitude, position.longitude), 15)
            (pagerAdapter?.getItem(0) as MapFragment).animateTo(GeoPoint(position.latitude, position.longitude), MainPref.defaultZoom)
            view_pager.setCurrentItem(0)
            nav_view.setCheckedItem(R.id.nav_map)
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun showCompleted() {
        finish()
    }

    override fun showRemoveDeviceCompleted() {
        intent.putExtra("deviceId", 0)
    }

    override fun showRemoveGeofenceCompleted() {
        intent.putExtra("geofenceId", 0)
    }

    override fun showRemoveUserCompleted() {
        intent.putExtra("userId", 0)
    }

    override fun getUserId(): Long {
        return intent.getLongExtra("userId", 0)
    }

    override fun getDeviceId(): Long {
        return intent.getLongExtra("deviceId", 0)
    }

    override fun getGeofenceId(): Long {
        return intent.getLongExtra("geofenceId", 0)
    }

    override fun getPositionId(): Long {
        return intent.getLongExtra("positionId", 0)
    }

    override fun getCommand(): Command {
        val command = intent.getParcelableExtra<Command>("command")
        command.deviceId = deviceId
        return command
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)

        if (error.contains("Attempt to invoke" )){
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setTitle("Предупреждение")
        alert.setMessage("Устройство еще не передавало данных на сервер")
        alert.setPositiveButton("Подождать", null)
        alert.show()
        }

        if (error.contains("401" )){
            val alert = AlertDialog.Builder(this@MainActivity)
            alert.setTitle("Предупреждение")
            alert.setMessage("Вы хотите выйти?")
            alert.setPositiveButton("Нет", null)
            alert.show()
           startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_map -> {
                view_pager.setCurrentItem(0)
                val center = calculateMapCenter()
                (pagerAdapter?.getItem(0) as MapFragment).animateTo(center.first, center.second)
            }
            R.id.nav_devices -> {
                view_pager.setCurrentItem(1)
            }
            R.id.nav_users -> {
                view_pager.setCurrentItem(2)
            }
            R.id.nav_geofences -> {
                view_pager.setCurrentItem(3)
            }
            R.id.nav_server -> {
                val intent = Intent(this@MainActivity, ServerActivity::class.java)
                        .putExtra("session", intent.getParcelableExtra<User>("session"))
                        .putExtra("server", intent.getParcelableExtra<Server>("server"))
                startActivityForResult(intent, REQUEST_CODE_UPDATE_SERVER)
            }
            R.id.nav_account -> {
                val intent = Intent(this@MainActivity, UserActivity::class.java)
                        .putExtra("session", intent.getParcelableExtra<User>("session"))
                        .putExtra("user", intent.getParcelableExtra<User>("session"))
                startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)
            }
            R.id.nav_about -> {
              /*  startActivity(Intent(this@MainActivity, AboutActivity::class.java))*/
                try {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_SUBJECT, "4kids")
                    var sAux = "Рекомендую установить приложение 4kids - Безопасность\n\n"
                    sAux = sAux + "https://play.google.com/store/apps/details?id=ru.rusmobilecontent.forkidssafety \n\n"
                    i.putExtra(Intent.EXTRA_TEXT, sAux)
                    startActivity(Intent.createChooser(i, "Выберите приложение"))
                } catch (e: Exception) {
                    //e.toString();
                }

            }
            R.id.nav_sign_out -> {
                presenter?.onDeleteSessionButtonClick()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageName = ""
        val user = intent.getParcelableExtra<User?>("user")
        when (requestCode) {
            REQUEST_CODE_UPDATE_SERVER ->
                if (resultCode == RESULT_OK) {
                    val server = data?.getParcelableExtra<Server>("server")
                    intent.putExtra("server", server)
                }
            REQUEST_CODE_UPDATE_ACCOUNT ->
                if (resultCode == RESULT_OK) {
                    val user = data?.getParcelableExtra<User>("user")
                    intent.putExtra("session", user)
                    mAccountNameView?.text = user?.name
                    mAccountEmailView?.text = user?.email
                }
            REQUEST_CODE_CREATE_OR_UPDATE_DEVICE ->
                if (resultCode == RESULT_OK) {
                }
            REQUEST_PICK ->
               /* if (resultCode == RESULT_OK && data != null) {
                   // val selectedImage = data?.getData()
                    //iw4Kids.setImageURI(selectedImage)

                    val filePatch = data?.getData() as Uri
                    try{
                        val inputStream = contentResolver.openInputStream(filePatch)
                        bitmap = BitmapFactory.decodeStream(inputStream)
                     //   val bitmapsimplesize = Bitmap.createScaledBitmap(bitmap, 480, 480, true)
                        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap)
                        roundedBitmapDrawable.isCircular = true
                        iw4Kids.setImageDrawable(roundedBitmapDrawable)

                       } catch (e: FileNotFoundException){
                        e.printStackTrace()
                    }

                    ////////
                    val stringRequest = object : StringRequest(Request.Method.POST, "http://13.94.117.29/upload.php",
                            Response.Listener<String> { response ->
                                try {
                                    val obj = JSONObject(response)
                                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                                    MainPref.userImage = imageName
                                    user?.map = imageName

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            object : Response.ErrorListener {
                                override fun onErrorResponse(volleyError: VolleyError) {
                                    Toast.makeText(applicationContext, "error: " + volleyError.toString(), Toast.LENGTH_LONG).show()
                                }
                            }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            val imageData = imageToString(bitmap)
                            imageName = Math.abs(Random().nextInt()).toString() + "_" + System.currentTimeMillis().toString() + ".jpeg"
                     //       val user = data?.getParcelableExtra<User>("user")
                            params.put("image", imageData)
                            params.put("foldername", tv_account_email.text.toString())
                            params.put("imagename", imageName)
                            return params
                                 }
                             }
                    val requestQueue = Volley.newRequestQueue(applicationContext)
                    requestQueue.add(stringRequest)
                    //////

                } */

                if (resultCode == RESULT_OK && requestCode == ImagePickerWithCrop.REQUEST_PICK) {
                    ImagePickerWithCrop.beginCrop(this, resultCode, data);
                    Toast.makeText(applicationContext, "1", Toast.LENGTH_LONG).show()
                } else if (requestCode == ImagePickerWithCrop.REQUEST_CROP) {
                    Toast.makeText(applicationContext, "2", Toast.LENGTH_LONG).show()
                    bitmap = ImagePickerWithCrop.getImageCropped(this, resultCode, data,
                    ImagePickerWithCrop.ResizeType.FIXED_SIZE, 100);

                    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap)
                    roundedBitmapDrawable.isCircular = true
                    iw4Kids.setImageDrawable(roundedBitmapDrawable)

                    ///////
                    val stringRequest = object : StringRequest(Request.Method.POST, "http://13.94.117.29/upload.php",
                            Response.Listener<String> { response ->
                                try {
                                    val obj = JSONObject(response)
                                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                                    MainPref.userImage = imageName
                                    user?.map = imageName

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            object : Response.ErrorListener {
                                override fun onErrorResponse(volleyError: VolleyError) {
                                    Toast.makeText(applicationContext, "error: " + volleyError.toString(), Toast.LENGTH_LONG).show()
                                }
                            }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            val imageData = imageToString(bitmap)
                            imageName = Math.abs(Random().nextInt()).toString() + "_" + System.currentTimeMillis().toString() + ".jpeg"
                            //       val user = data?.getParcelableExtra<User>("user")
                            params.put("image", imageData)
                            params.put("foldername", tv_account_email.text.toString())
                            params.put("imagename", imageName)
                            return params
                        }
                    }
                    val requestQueue = Volley.newRequestQueue(applicationContext)
                    requestQueue.add(stringRequest)
                    /////////

                //    Log.d(TAG, "bitmap picked: " + bitmap);
                } else {
                    super.onActivityResult(requestCode, resultCode, data)
                    Toast.makeText(applicationContext, "3", Toast.LENGTH_LONG).show()
                }
            PICK_IMAGE_ID ->
                if (resultCode == RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    Toast.makeText(applicationContext, "4", Toast.LENGTH_LONG).show()

                    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap)
                    roundedBitmapDrawable.isCircular = true
                    iw4Kids.setImageDrawable(roundedBitmapDrawable)

                    ///////
                    val stringRequest = object : StringRequest(Request.Method.POST, "http://13.94.117.29/upload.php",
                            Response.Listener<String> { response ->
                                try {
                                    val obj = JSONObject(response)
                                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                                    MainPref.userImage = imageName
                                    user?.map = imageName

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            object : Response.ErrorListener {
                                override fun onErrorResponse(volleyError: VolleyError) {
                                    Toast.makeText(applicationContext, "error: " + volleyError.toString(), Toast.LENGTH_LONG).show()
                                }
                            }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            val imageData = imageToString(bitmap)
                            imageName = Math.abs(Random().nextInt()).toString() + "_" + System.currentTimeMillis().toString() + ".jpeg"
                            //       val user = data?.getParcelableExtra<User>("user")
                            params.put("image", imageData)
                            params.put("foldername", tv_account_email.text.toString())
                            params.put("imagename", imageName)
                            return params
                        }
                    }
                    val requestQueue = Volley.newRequestQueue(applicationContext)
                    requestQueue.add(stringRequest)
                    /////////

                }



        }
    }

    fun imageToString(bitmap: Bitmap?):String{
        var outputStream = ByteArrayOutputStream()
        var bitmapsimplesize = bitmap
        if(bitmap!!.width >= bitmap!!.height && bitmap!!.width >= 1080) {
            val bitmapscale = Math.round((bitmap!!.width / 1080).toFloat())
            bitmapsimplesize = Bitmap.createScaledBitmap(bitmap, bitmap!!.width / bitmapscale, bitmap!!.height / bitmapscale, false)
        } else if(bitmap!!.width < bitmap!!.height && bitmap!!.height >= 1080){
            val bitmapscale = Math.round((bitmap!!.height / 1080).toFloat())
            bitmapsimplesize = Bitmap.createScaledBitmap(bitmap, bitmap!!.width / bitmapscale, bitmap!!.height / bitmapscale, false)
        } else {
            val bitmapscale = 1
            bitmapsimplesize = Bitmap.createScaledBitmap(bitmap, bitmap!!.width / bitmapscale, bitmap!!.height / bitmapscale, false)
        }
        bitmapsimplesize.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
        var imageBytes = outputStream.toByteArray()

        var encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return encodedImage
    }

    override fun onEditDevice(device: Device) {
        val intent = Intent(this@MainActivity, DeviceThisActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivity(intent)
    }

    override fun onRemoveDevice(device: Device) {
        intent.putExtra("deviceId", device.id)
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onLoadPositions(device: Device) {
        val intent = Intent(this@MainActivity, PositionsActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivity(intent)
    }

    override fun onShowOnMap(device: Device) {
        intent.putExtra("positionId", device.positionId)
        presenter?.onGetPostionByCache()
    }

    override fun onCallDevice(device: Device)  {
       /* val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + device.phone.toString())
        startActivity(callIntent)*/
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            callIntent.data = Uri.parse("tel:" + device.phone.toString())
            startActivity(callIntent)

    }

    override fun onSendCommand(device: Device) {
        intent.putExtra("deviceId", device.id)
        val dialogFragment = SendCommandDialogFragment.newInstance(device.id)
        dialogFragment.show(supportFragmentManager, "send_command_dialog")

    }

    override fun getUser(): User {
        var user = intent.getParcelableExtra<User>("user")
        if (user == null) {
            user = User()
        }

        user?.map = MainPref.userImage

        return user
    }

    override fun onEditUser(user: User) {
        val intent = Intent(this@MainActivity, UserActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("user", user)
        startActivity(intent)
    }

    override fun onRemoveUser(user: User) {
        intent.putExtra("userId", user.id)
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onPermissionForUser(user: User) {
        val intent = Intent(this@MainActivity, DevicesActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("user", user)
        startActivity(intent)
    }

    override fun onShowOnMapGeofence(geofence: Geofence) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadPositions(geofence: Geofence) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRemoveGeofence(geofence: Geofence) {
        intent.putExtra("geofenceId", geofence.id)
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onEditGeofence(geofence: Geofence) {
        val intent = Intent(this@MainActivity, GeofenceActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("geofence", geofence)
        startActivity(intent)
    }

    override fun onPositiveClick(dialog: DialogInterface, which: Int) {
        if (deviceId > 0) {
            presenter?.onDeleteDeviceButtonClick()
        }
        if (userId > 0) {
            presenter?.onDeleteUserButtonClick()
        }
        if (getGeofenceId() > 0) {
            presenter?.onDeleteGeofenceButtonClick()
        }
    }

    override fun onSendCommand(command: Command?) {
        intent.putExtra("command", command)
        presenter?.onSendCommandButtonClick()
    }

    private fun calculateMapCenter() :Pair<GeoPoint, Int> {
       //val user = intent.getParcelableExtra<User>("session")
//        if (user.latitude === 0.0 && user.longitude === 0.0 && user.zoom === 0) {
          //  val server = intent.getParcelableExtra<Server>("server")
//            if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
//                return Pair(GeoPoint(server.latitude, server.longitude), server.zoom)
//            }
//        } else {
//            return Pair(GeoPoint(user.latitude, user.longitude), user.zoom)
            return Pair(GeoPoint(MainPref.defaultLatitude.toDouble(), MainPref.defaultLongitude.toDouble()), MainPref.defaultZoom)
//        }
//        return Pair(GeoPoint(0, 0), 0)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
        private var backPressed: Long = 0

        val REQUEST_CODE_UPDATE_SERVER = 1
        val REQUEST_CODE_UPDATE_ACCOUNT = 2
        val REQUEST_CODE_CREATE_OR_UPDATE_DEVICE = 3
        val REQUEST_CODE_USER_PIC = 4
    }
}
