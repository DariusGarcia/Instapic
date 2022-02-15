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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

/*
// Let user create a post by taking a photo with their camera.
*/

class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.instagram_logo)
        // 1. Setting the description of the post
        // 2. A button to launch the camera to take a picture.
        // 3. An imageView to show the picture the user has taken.
        // 4. A button to save and send the post to our Parse server.

        findViewById<Button>(R.id.btnPost).setOnClickListener {
            // Send post to server without an image.
            // Get the description that they have inputted.
            val description = findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(TAG, "Error while submitting post... Try again.")
                Toast.makeText(this, "Have to take a picture before posting!", Toast.LENGTH_SHORT).show()
            }

        }

        findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture.
            onLaunchCamera()
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item -> //creates variable called item

            when (item.itemId) {

                R.id.action_home -> {
                    // TODO Naviagate to the home scree
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.action_compose -> {
                    Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show()
                }
                R.id.action_profile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }
            }
            // Return true to say that we've handled this user interaction on the item.
            true
        }
        queryPosts()
    }

    // Send a Post object to our Parse server.
    fun submitPost(description: String, user: ParseUser, file: File) {
        // Create the Post object which will be sent to the Parse server.
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))


        // .saveInBackground: takes this Parse object we created and set fields for and send it to
        // the Parse server.
        post.saveInBackground { exception ->
            if (exception != null) {
                // Something has went wrong.
                Log.e(TAG, "Error while saving post...")
                exception.printStackTrace()
                Toast.makeText(this, "Error while saving post... Try again.", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Successfully saved post...")
                Toast.makeText(this, "Successfully uploaded post!", Toast.LENGTH_LONG).show()
                // TODO: Resetting the EditText field to be empty.
                findViewById<EditText>(R.id.description).getText().clear()
                // TODO: Reset the ImageView field to be empty.
                findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.instagram_logo)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // WHen user comes back to app after opening the camera app.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onLaunchCamera() { // Launches the camera app.
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // Using an intent to launch android camera app. lets user choose specific app to use to take picture.
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)
        Toast.makeText(this, "Opening camera...!", Toast.LENGTH_SHORT).show()

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) // start camera app take pic, save pic, then come back with request code.
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
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