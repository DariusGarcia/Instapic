package com.example.instapic

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject


class InstapicApplication  : Application() {

        override fun onCreate() {
            super.onCreate()

            ParseObject.registerSubclass(Post::class.java)
            // sets up the use for the Post class.
            Parse.initialize(
                // Initializing parse right when the app first starts.
                Parse.Configuration.Builder(this)
                    .applicationId(getString(R.string.back4app_app_id))
                    .clientKey(getString(R.string.back4app_client_key))
                    .server(getString(R.string.back4app_server_url))
                    .build());
        }
    }
