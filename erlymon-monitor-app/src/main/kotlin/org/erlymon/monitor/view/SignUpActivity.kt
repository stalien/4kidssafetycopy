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
import android.os.SystemClock
import android.support.design.widget.Snackbar

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_signup.*
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UserPresenter
import org.erlymon.core.presenter.UserPresenterImpl
import org.erlymon.core.view.UserView
import org.erlymon.monitor.R
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_device.*
import kotlinx.android.synthetic.main.content_signin.*
import org.erlymon.monitor.MainPref
import android.text.util.Linkify
import android.widget.TextView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.system.Os.link
import android.text.InputType


class SignUpActivity : BaseActivity<UserPresenter>(), UserView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
       // setSupportActionBar(toolbar)
        btn_back.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        val tvDisplay = findViewById(R.id.politics) as TextView
        tvDisplay.movementMethod = LinkMovementMethod.getInstance()


        presenter = UserPresenterImpl(this, this)
        btn_save.setOnClickListener {
            if (validateName() && validateEmail() && validatePass()){
                Snackbar.make(btn_save, "Новый пользователь зарегистрирован", Snackbar.LENGTH_LONG).show()
                SystemClock.sleep(2000)
                presenter?.onSaveButtonClick()}
        }
        //fab_account_save.setOnClickListener { v -> presenter?.onSaveButtonClick() }
    }

    fun validateName(): Boolean {

        if(name.text.toString().isEmpty()){
            this.name.setError("Поле не может быть пустым")
            name.requestFocus()
            return false
        } else {
//            input_name.isErrorEnabled = false
            return true
        }
    }

    fun validateEmail(): Boolean {

        if(email.text.toString().isEmpty()){
            email.setError("Поле не может быть пустым")
            email.requestFocus()
            return false
        } else if (!isEmailValid(email.text.toString())){
            email.setError("Введите правильный e-mail")
            email.requestFocus()
            return false
        }else {
//            input_name.isErrorEnabled = false
            return true
        }
    }

    fun validatePass(): Boolean {

        if(password.text.toString().isEmpty()){
            password.setError("Поле не может быть пустым")
            password.requestFocus()
            return false
        } else if (password.text.toString().length < 6){
            password.setError("Пароль должен быть не короче 6 символов")
            password.requestFocus()
            return false
        }else {
//            input_name.isErrorEnabled = false
            return true
        }
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

//        val em = email.text.toString()
//        if (em.isEmpty()) {

//            makeToast(btn_save, "пустая строка")
//        } else
//            if (isEmailValid(em)) {

               // user.id = 0;
                user.name = name.text.toString()
                user.email = email.text.toString()
                user.password = password.text.toString()
                MainPref.email = email.text.toString()
                MainPref.password = ""

//            } else {// ошибка валидации
//                Snackbar.make(btn_save, "ошибка валидации: e-mail", Snackbar.LENGTH_LONG).show()
 //               makeToast(btn_save, "ошибка валидации")
//            }


            return user;
        }


    override fun showError(error: String) {
       // makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SignUpActivity::class.java)
    }
}
