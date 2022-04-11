package com.example.kotlinmessenger.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityLoginBinding
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonLogin.setOnClickListener{
            performLogin()
        }

        binding.registerRedirection.setOnClickListener{
            // Show RegisterActivity layout:
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.incomplete_login_toast), Toast.LENGTH_SHORT).show()
            return
        }
        // Firebase Auth : sign in user with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                // Show successful login message :
                Log.d("Main", getString(R.string.successful_login_log))
                Toast.makeText(this, getString(R.string.successful_login_toast), Toast.LENGTH_LONG).show()

                // Open LatestMessages activity :
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // Clear all former activities so Back button closes the application
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("Main", getString(R.string.failed_login_log) + it.message)
                Toast.makeText(this, getString(R.string.failed_login_toast), Toast.LENGTH_SHORT).show()
            }
    }

}