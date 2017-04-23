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
package org.erlymon.core.model.api;

import android.content.Context;

import com.appunite.websocket.rx.RxWebSockets;
import com.appunite.websocket.rx.object.GsonObjectSerializer;
import com.appunite.websocket.rx.object.ObjectSerializer;
import com.appunite.websocket.rx.object.RxObjectWebSockets;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.erlymon.core.model.data.Event;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/18/16.
 */
public class ApiModule {
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;

    private static ApiModule ourInstance = new ApiModule();

    private Gson gson;
    private ApiInterface apiInterface;
    private RxObjectWebSockets webSockets;


    public synchronized static ApiModule getInstance() {
        return ourInstance;
    }

    private ApiModule() {}

    public void init(Context context, String dns, boolean sslOrTls, double protocolVersion) {
        // init gson
        gson = new GsonBuilder()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") 2017-04-20T08:17:04.429+0000
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        try {
                            return new Date(df.parse(json.getAsString()).getTime());
                        } catch (final java.text.ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .setVersion(protocolVersion)
                .create();


        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MINUTES)
                .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MINUTES)
                //.addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .cookieJar(cookieJar)
                .build();

        final RxWebSockets rxWebSockets = new RxWebSockets(client, new Request.Builder()
                .get()
                .url("{socket}://{dns}/api/socket".replace("{socket}", sslOrTls ? "wss" : "ws").replace("{dns}", dns))
                .addHeader("Sec-WebSocket-Protocol", "chat")
                .build());

        final ObjectSerializer serializer = new GsonObjectSerializer(gson, Event.class);
        webSockets = new RxObjectWebSockets(rxWebSockets, serializer);

        apiInterface = new Retrofit.Builder()
                .baseUrl("{protocol}://{dns}/api/".replace("{protocol}", sslOrTls ? "https" : "http").replace("{dns}", dns))
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiInterface.class);
    }

    public ApiInterface getApi() {
        return apiInterface;
    }

    public RxObjectWebSockets createWebSocket() {
        return webSockets;
    }

    public Gson getGson() {
        return gson;
    }
}
