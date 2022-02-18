package com.example.instapic

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.instapic.fragments.ComposeFragment
import com.example.instapic.fragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

/*
// Let user create a post by taking a photo with their camera.
*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.instagram_logo)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item -> //creates variable called item

            var fragmentToShow: Fragment? = null
            when (item.itemId) {

                R.id.action_home -> {
                    fragmentToShow = HomeFragment()
                    //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.action_compose -> {
                    fragmentToShow = ComposeFragment()

                }
                R.id.action_profile -> {

                }
            }

            if (fragmentToShow != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()

            }
            // Return true to say that we've handled this user interaction on the item.
            true
        }

        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home

        queryPosts()
    }


    companion object {
        const val TAG = "MainActivity"
    }
}