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

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View
import org.erlymon.core.presenter.Presenter


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/16.
 */
open class BaseFragment<P : Presenter> : Fragment() {
    protected var presenter: P? = null

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.onStop()
    }

    protected fun makeToast(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }
}
