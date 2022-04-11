package com.example.kotlinmessenger.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityRegisterBinding
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var selectedPhotoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Bouton "S'inscrire" :
        binding.buttonRegister.setOnClickListener{
            performRegister()
        }
        // Bouton "Se connecter" :
        binding.loginRedirection.setOnClickListener{
            // Back to MainActivity layout (Login page) :
            finish()
        }
        // Bouton "Photo" :
        binding.avatarRegister.setOnClickListener{
            openPhotoSelector()
        }
    }


    private fun performRegister() {
        val username = binding.usernameRegister.text.toString()
        val email = binding.emailRegister.text.toString()
        val password = binding.passwordRegister.text.toString()

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.incomplete_registration_toast), Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Auth : create a new user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                // If registering is successful:
                Log.d("Register Activity", getString(R.string.successful_registration_log) + it.result.user?.uid)
                Toast.makeText(this, getString(R.string.successful_registration_toast), Toast.LENGTH_LONG).show()
                // Upload avatar to Firebase :
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("Register Activity", getString(R.string.failed_registration_log) + it.message)
                Toast.makeText(this, getString(R.string.failed_registration_toast), Toast.LENGTH_LONG).show()
            }
    }

    private fun openPhotoSelector() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    // Is triggered when startActivityForResult completes:
    // data : picture picked
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Register Activity", getString(R.string.successful_avatar_selection))
            // Set image to avatar button:
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //binding.avatarRegister.setBackgroundDrawable(bitmapDrawable)
            binding.selectedImageRegister.setImageBitmap(bitmap) // Set selected image to CircleImageView
            binding.avatarRegister.alpha = 0f // Set 'Upload photo' button opacity to 0
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d("Register Activity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Register Activity", "File location: $it")

                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    val username = binding.usernameRegister.text.toString()
                    val avatar = it.toString()
                    val user = User(uid, username, avatar)

                    // Save user to database :
                    saveUserToDatabase(user)

                    // Open LatestMessages activity :
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // Clear all former activities so Back button closes the application
                    startActivity(intent)
                }
            }
            .addOnFailureListener{
                Log.d("Register Activity", getString(R.string.failed_photo_uploading_log))
            }
    }

    private fun saveUserToDatabase(user: User) {
        val ref = FirebaseDatabase.getInstance().getReference("users") // Inside field "users" in database

        // Use user.uid as field name for each new entry:
        ref.child(user.uid).setValue(user)
            .addOnSuccessListener {
                Log.d("Register Activity", getString(R.string.successfully_saved_user_to_dabatase_log))
            }
            .addOnFailureListener{
                Log.d("Register Activity", getString(R.string.failed_saving_user_to_dabatase_log))
            }
    }
}

