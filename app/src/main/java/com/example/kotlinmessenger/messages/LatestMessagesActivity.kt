package com.example.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kotlinmessenger.auth.LoginActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityLatestMessagesBinding
import com.example.kotlinmessenger.models.Message
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLatestMessagesBinding
    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        val view = binding.root

        binding.recyclerviewLatestMessages.adapter = adapter

        verifyLogin() // Check if user is logged in
        fetchCurrentUser() // Fetch logged-in user :

        setContentView(view)

        listenForLatestMessages()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // Clear all former activities so Back button closes the application
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun verifyLogin() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            // Start Login activity:
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // Clear all former activities so Back button closes the application
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("Latest Activity", "Current logged-in user is: ${currentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun listenForLatestMessages() {
        val senderId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$senderId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                adapter.add(LatestMessageRow(message))
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                adapter.add(LatestMessageRow(message))

            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }

    class LatestMessageRow(val message: Message): Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            //viewHolder.itemView.latestMessageRowUsername.text = "???"
            viewHolder.itemView.latestMessageRowText.text = message.text
        }
    }
}