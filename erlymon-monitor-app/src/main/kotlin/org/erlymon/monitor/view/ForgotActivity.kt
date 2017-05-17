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
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_about.*
import kotlinx.android.synthetic.main.content_forgot.*
import kotlinx.android.synthetic.main.content_signup.*
import org.erlymon.monitor.R

class ForgotActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_forgot)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        btn_send.setOnClickListener {



            //alert.setTitle("")

            if(forgot_mail.text.toString().isEmpty())
            {
                forgot_mail.setError("Поле не может быть пустым")
                forgot_mail.requestFocus()
            }
            else{
            val alert = AlertDialog.Builder(this@ForgotActivity)
            alert.setMessage("На введённый Email было отправлено письмо для сброса пароля.")
            alert.setPositiveButton("Ок",  { dialog, whichButton ->   val intent = Intent(this@ForgotActivity, SignInActivity::class.java)
                startActivity(intent) })
            alert.show()}
        }

    }


}
