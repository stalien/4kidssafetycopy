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
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.PopupMenu
import android.widget.AutoCompleteTextView
import com.jakewharton.rxbinding.view.RxView
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.content_devices.*
import kotlinx.android.synthetic.main.content_forgot.*
import kotlinx.android.synthetic.main.content_signin.*
import org.erlymon.core.model.api.ApiModule
import org.erlymon.core.model.data.Server
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.SignInPresenter
import org.erlymon.core.presenter.SignInPresenterImpl
import org.erlymon.core.view.SignInView
import org.erlymon.monitor.MainPref
import org.erlymon.monitor.R
import org.erlymon.monitor.view.fragment.SettingsDialogFragment


class SignInActivity : BaseActivity<SignInPresenter>(), SignInView, SettingsDialogFragment.ServerConfigListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        presenter = SignInPresenterImpl(this, this, R.string.sharedLoading)
//кавабанга
      sign_in_email.setText(MainPref.email)
       sign_in_password.setText(MainPref.password);
        //sign_in_email.setText("")
        //sign_in_password.setText("");


        RxView.clicks(sign_in_button)
                .compose(RxPermissions.getInstance(this).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe({ granted ->
                    if (granted) {
                        MainPref.email = email
                        MainPref.password = password
                        presenter?.onCreateSession()
                    } else {
                        makeToast(sign_in_button, getString(R.string.errorPermissionWriteStorage))
                    }
                })

        sign_up_button.setOnClickListener {

            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)

        }
        btn_forgot.setOnClickListener {
            val intent = Intent(this@SignInActivity, ForgotActivity::class.java)
            startActivity(intent)

        }

        serverConfig.setOnClickListener({
            val popupMenu = PopupMenu(this@SignInActivity, serverConfig)
            popupMenu.inflate(R.menu.settings_popupmenu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_settings -> {
                        val dialog = SettingsDialogFragment.newInstance(MainPref.dns, MainPref.sslOrTls, MainPref.protocolVersion.toDouble())
                        dialog.show(supportFragmentManager, "settings_dialog")
                        true
                    }
                    R.id.action_about -> {
                        startActivity(Intent(this@SignInActivity, AboutActivity::class.java))
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
            popupMenu.show()
        })
    }

    override fun onResume() {
        sign_in_email.setText(MainPref.email)
        sign_in_password.setText(MainPref.password)
        super.onResume()
        presenter?.onGetServer()
    }

    override fun onChangeServerConfig(dns: String, sslOrTls: Boolean, protocolVersion: Double) {
        Realm.getDefaultInstance().executeTransaction { realm -> realm.deleteAll() }
        MainPref.dns = dns
        MainPref.sslOrTls = sslOrTls
        MainPref.protocolVersion = protocolVersion.toFloat()
        ApiModule.getInstance().init(applicationContext, MainPref.dns, MainPref.sslOrTls, MainPref.protocolVersion.toDouble())
        presenter?.onGetServer()
    }

    override fun showServer(server: Server) {
        logger.debug("showServer => " + server.toString())
        intent.putExtra("server", server)
        presenter?.onGetSession()
    }

    override fun showSession(user: User) {
        logger.debug("showSession => " + user.toString())
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                .putExtra("server", intent.getParcelableExtra<Server>("server"))
                .putExtra("session", user)
        startActivity(intent)
    }

    override fun showError(error: String) {
     //Тут ошибки всякие
        if(error == "HTTP 401 Unauthorized"){

            val alert = AlertDialog.Builder(this@SignInActivity)
            alert.setTitle("Ошибка")
            alert.setMessage("Логин или пароль введены неверно!")
            alert.setPositiveButton("Повторить", null)
            alert.show()

            //makeToast(ll_sign_in, "Логин или пароль неправильные")
        }
    }

    override fun getEmail(): String {
        return sign_in_email.text.toString()
    }

    override fun getPassword(): String {
        return sign_in_password.text.toString()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SignInActivity::class.java)
        private val REQUEST_WRITE_STORAGE = 1
    }
}
