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

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_signup.*
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UserPresenter
import org.erlymon.core.presenter.UserPresenterImpl
import org.erlymon.core.view.UserView
import org.erlymon.monitor.R
import android.widget.EditText




class SignUpActivity : BaseActivity<UserPresenter>(), UserView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
       // setSupportActionBar(toolbar)

        btn_back.setOnClickListener { v ->
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        presenter = UserPresenterImpl(this, this)
        btn_save.setOnClickListener { v -> presenter?.onSaveButtonClick() }
        //fab_account_save.setOnClickListener { v -> presenter?.onSaveButtonClick() }
    }

    override fun showData(data: User) {
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun getUserId(): Long {
        return 0
    }



   /* fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }*/
   fun isEmailValid(email: CharSequence): Boolean {
       return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

   }

    override fun getUser(): User? {
        val user = User()

        val em = email.text.toString()
        if (isEmailValid(em)) {

        user.id = 0;
        user.name = name.text.toString()
        user.email = email.text.toString()
        user.password = password.text.toString()
        }

        //else {а что если косорукий дурак не знает, как вводить почту?}
        return user;
    }

    override fun showError(error: String) {
      //  makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SignUpActivity::class.java)
    }
}
