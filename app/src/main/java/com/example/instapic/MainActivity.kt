package com.example.instapic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

/*
// Let user create a post by taking a photo with their camera.
*/

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Settin gthe description of the post
        // 2. A button to launch the camera to take a picture.
        // 3. An imageView to show the picture the user has taken.
        // 4. A button to save and send the post to our Parse server.

        queryPosts()
    }

    // Query for all posts in our server.
    fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects and return it to us.
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " +
                                    post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }

    companion object {
        const val TAG = "MainActivity"
    }
}