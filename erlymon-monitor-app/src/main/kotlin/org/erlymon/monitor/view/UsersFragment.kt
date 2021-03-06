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

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import io.realm.RealmResults
import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.fragment_users.*
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UsersListPresenter
import org.erlymon.core.presenter.UsersListPresenterImpl
import org.erlymon.core.view.UsersListView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.UsersAdapter

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class UsersFragment : BaseFragment<UsersListPresenter>(), UsersListView {

    interface OnActionUserListener {
        fun onEditUser(user: User)
        fun onRemoveUser(user: User)
        fun onPermissionForUser(user: User)
    }

    private var listener: OnActionUserListener? = null


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as OnActionUserListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context!!.toString() + " must implement OnActionUserListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = UsersListPresenterImpl(context, this)
        //lv_users.adapter = UsersAdapter(context, storage.getUsersSorted())
        lv_users.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val user = lv_users.getItemAtPosition(position) as User
                val popupMenu = PopupMenu(context, view)
                popupMenu.inflate(R.menu.fragment_users_popupmenu)
                popupMenu.setOnMenuItemClickListener(OnExecPopupMenuItem(user))
                popupMenu.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.onLoadUsersCache()
    }

    override fun showData(data: RealmResults<User>) {
        lv_users.adapter = UsersAdapter(context, data)
    }

    override fun showError(error: String) {
       makeToast(lv_users, error)
    }

    private inner class OnExecPopupMenuItem(internal var user: User) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(item: MenuItem): Boolean {
            // Toast.makeText(PopupMenuDemoActivity.this,
            // item.toString(), Toast.LENGTH_LONG).show();
            // return true;
            when (item.itemId) {
                R.id.action_user_edit -> {
                    listener!!.onEditUser(user)
                    return true
                }
                R.id.action_user_remove -> {
                    listener!!.onRemoveUser(user)
                    return true
                }
                R.id.action_user_devices -> {
                    listener!!.onPermissionForUser(user)
                    return true
                }
                else -> return false
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UsersFragment::class.java)
    }
}