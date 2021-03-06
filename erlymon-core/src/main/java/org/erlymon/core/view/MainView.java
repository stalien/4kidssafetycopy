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
package org.erlymon.core.view;


import org.erlymon.core.model.data.Command;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Event;
import org.erlymon.core.model.data.Position;
import org.erlymon.core.model.data.User;

import io.realm.RealmResults;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public interface MainView extends View {
    void showPosition(Position position);
    void showCompleted();
    void showRemoveDeviceCompleted();
    void showRemoveUserCompleted();
    long getDeviceId();
    long getPositionId();
    long getUserId();
    Command getCommand();
}
