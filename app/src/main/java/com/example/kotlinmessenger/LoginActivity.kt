package com.example.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.databinding.ActivityLoginBinding
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
            // Back to MainActivity layout:
            finish()
        }
    }

    private fun performLogin() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "⚠️ Veuillez saisir une adresse email et un mot de passe.", Toast.LENGTH_SHORT).show()
            return
        }
        // Firebase Auth : sign in user with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                // If registering is successful:
                Log.d("Main", "Successfully signed in.")
                Toast.makeText(this, "✔️ Connexion réussie.", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener{
                Log.d("Main", "Failed while attempting to log in user. Error: ${it.message}")
                Toast.makeText(this, "❌  Echec lors de la connexion.", Toast.LENGTH_SHORT).show()
            }
    }

}