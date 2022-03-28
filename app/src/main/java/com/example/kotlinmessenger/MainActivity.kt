package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlinmessenger.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonRegister.setOnClickListener{
            performRegister()
        }
        binding.loginRedirection.setOnClickListener{
            // Show LoginActivity layout:
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val username = binding.usernameRegister.text.toString()
        val email = binding.emailRegister.text.toString()
        val password = binding.passwordRegister.text.toString()

        Log.d("Main", "TEST BEFORE")

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "⚠️ Veuillez saisir un nom d'utilisateur, une adresse email et un mot de passe.", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Auth : create a new user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                // If registering is successful:
                Log.d("Main", "Successfully registered with UID: ${it.result.user?.uid}")
                Log.d("Main", getString(R.string.text_example));
                Toast.makeText(this, "✔️ Inscription réussie.", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener{
                Log.d("Main", "Failed while attempting to register user. Error: ${it.message}")
                Toast.makeText(this, "❌  Echec lors de l'inscription. Veuillez vérifier que votre adresse email est valide.", Toast.LENGTH_LONG).show()
            }
    }
}