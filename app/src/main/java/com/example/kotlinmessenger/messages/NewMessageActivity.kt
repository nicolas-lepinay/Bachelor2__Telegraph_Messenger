package com.example.kotlinmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityNewMessageBinding
import com.example.kotlinmessenger.databinding.UserRowNewMessageBinding
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.views.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class NewMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewMessageBinding
    private lateinit var bindingNewMessage: UserRowNewMessageBinding

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        bindingNewMessage = UserRowNewMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Change nav bar title :
        supportActionBar?.title = getString(R.string.new_message_activity_title)

        // Fill list of users:
        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.newMessageRecyclerView.adapter = adapter
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user != null && user.uid != FirebaseAuth.getInstance().uid ) {
                        adapter.add(UserItem(user))
                    }
                }

                // Open ConversationActivity on click :
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ConversationActivity::class.java )
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                binding.newMessageRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}



