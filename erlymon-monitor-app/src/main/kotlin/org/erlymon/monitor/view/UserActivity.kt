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
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.erlymon.core.model.data.Server
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UserPresenter
import org.erlymon.core.presenter.UserPresenterImpl
import org.erlymon.core.view.UserView
import org.erlymon.monitor.MainPref
import org.erlymon.monitor.R
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.util.*

class UserActivity : BaseActivity<UserPresenter>(), UserView {

    private val PICK_IMAGE_ID = 234 // the number doesn't matter
    private var bitmap: Bitmap? = null

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


//    val deviceThis = intent.getParcelableExtra<User>("device")

    public fun pickImage() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID)
        //ImagePickerWithCrop.pickImage(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        var imageView = findViewById(R.id.iv_user_logo) as ImageView

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = UserPresenterImpl(this, this)

   /*     var mCustomLayout = findViewById(R.id.user_custom_layout) as CustomLayout
        Picasso.with(applicationContext)
                .load("http://13.94.117.29/upload/" + MainPref.email + "/" + MainPref.userImage + ".jpeg")
                .into(mCustomLayout) */

        Picasso.with(applicationContext)
                .load("http://13.94.117.29/upload/" + MainPref.email + "/" + MainPref.userImage)
                .placeholder(R.drawable.userphoto_default)
          //      .transform(CircularTransformation())
                .into(imageView)

        imageView.setOnClickListener {
            pickImage()
        }

        val session = intent.getParcelableExtra<User>("session")
        val user = intent.getParcelableExtra<User>("user")
        logger.debug("USER ID: " + user?.id + " USER: " + user?.toString())
        name.setText(user?.name)
        user_phone.setText(user?.phone)
        email.setText(user?.email)
        password.setText(user?.password)
        admin.isChecked = if (user?.admin != null) user.admin else false
        admin.isEnabled = session?.admin as Boolean
        tv_user_name.setText(user?.name)
        tv_user_email.setText(user?.email)
        deviceLimit.setText(if (user?.deviceLimit != null) user.deviceLimit.toString() else 0.toString())
//        map.setText(user?.map)
//        distanceUnit.setText(user?.distanceUnit)
//        speedUnit.setText(user?.speedUnit)
//        latitude.setText(if (user?.latitude != null) user.latitude.toString() else 0.0.toString())
//        longitude.setText(if (user?.longitude != null) user.longitude.toString() else 0.0.toString())
//        zoom.setText(if (user?.zoom != null) user.zoom.toString() else 0.toString())
//        twelveHourFormat.isChecked = if (user?.twelveHourFormat != null) user.twelveHourFormat else false

        fab_account_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    override fun showData(data: User) {
        logger.debug(user.toString())
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    override fun getUserId(): Long {
        val user = intent.getParcelableExtra<User>("user")
        return if (user != null) user.id else 0
    }

    override fun getUser(): User {
        var user = intent.getParcelableExtra<User>("user")
        if (user == null) {
            user = User()
        }
        user.id = userId
        user.name = name.text.toString()
        user.phone = user_phone.text.toString()
        user.email = email.text.toString()
      //  user.password = password.text.toString()
      //  user.admin = admin.isChecked

        user.distanceUnit = null
        user.map = MainPref.userImage
        user.speedUnit = null
        user.latitude = null
        user.longitude = null
        user.zoom = null
        user.twelveHourFormat = null
        user.deviceLimit = if (deviceLimit.text.isNotEmpty()) deviceLimit.text.toString().toInt() else 0


//        user.map = map.text.toString()
//        user.language = Locale.getDefault().language
//        user.distanceUnit = distanceUnit.text.toString()
//        user.speedUnit = speedUnit.text.toString()
//        user.latitude = if (latitude.text.isNotEmpty()) latitude.text.toString().toDouble() else 0.0
//        user.longitude = if (longitude.text.isNotEmpty()) longitude.text.toString().toDouble() else 0.0
//        user.zoom = if (zoom.text.isNotEmpty()) zoom.text.toString().toInt() else 0
//        user.twelveHourFormat = twelveHourFormat.isChecked
        return user
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageName = ""
     //   val user = intent.getParcelableExtra<User?>("user")
        when (requestCode) {

            PICK_IMAGE_ID ->
                if (resultCode == RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    Toast.makeText(applicationContext, "4", Toast.LENGTH_LONG).show()

                    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap)
                    roundedBitmapDrawable.isCircular = true
                  //  iv_user_logo.setImageDrawable(roundedBitmapDrawable)
                    iv_user_logo.setImageBitmap(bitmap)

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
                            params.put("foldername", user.email.toString())
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

    companion object {
        private val logger = LoggerFactory.getLogger(UserActivity::class.java)
    }
}
