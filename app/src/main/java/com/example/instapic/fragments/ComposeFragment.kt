package com.example.instapic.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.instapic.MainActivity
import com.example.instapic.Post
import com.example.instapic.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

// Main difference of fragments is their life cycle compared to main activities.
class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var ivPreview: ImageView // with out kotlin will say you created this variable but never initiallized it. Lateinit means youll initialiae it later. INiziatllized it later in the code.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set onClickListeners and setup logic.

        ivPreview = view.findViewById(R.id.imageView)

        view.findViewById<Button>(R.id.btnPost).setOnClickListener {
            // Send post to server without an image.
            // Get the description that they have inputted.
            val description = view.findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(MainActivity.TAG, "Error while submitting post... Try again.")
                Toast.makeText(requireContext(), "Have to take a picture before posting!", Toast.LENGTH_SHORT).show()
            }

        }

        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture.
            onLaunchCamera()
        }


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
                Log.e(MainActivity.TAG, "Error while saving post...")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error while saving post... Try again.", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(MainActivity.TAG, "Successfully saved post...")
                Toast.makeText(requireContext(), "Successfully uploaded post!", Toast.LENGTH_LONG).show()
                // TODO: Resetting the EditText field to be empty.
                view?.findViewById<EditText>(R.id.description)?.getText()?.clear()
                // TODO: Reset the ImageView field to be empty.
                view?.findViewById<ImageView>(R.id.imageView)?.setImageResource(R.drawable.instagram_logo)
            }
        }
    }

    fun onLaunchCamera() { // Launches the camera app.
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // Using an intent to launch android camera app. lets user choose specific app to use to take picture.
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)
        Toast.makeText(requireContext(), "Opening camera...!", Toast.LENGTH_SHORT).show()

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!) // requireContext() is how you get an object inside a fragment instead of 'this'
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
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
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // WHen user comes back to app after opening the camera app.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}