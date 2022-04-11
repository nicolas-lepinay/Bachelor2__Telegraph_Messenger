package com.example.kotlinmessenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.databinding.ActivityNewMessageBinding
import com.example.kotlinmessenger.databinding.UserRowNewMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewMessageBinding
    private lateinit var bindingNewMessage: UserRowNewMessageBinding

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
                    Log.d("TEST", bindingNewMessage.newMessageUsername.text.toString())
                    if(user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                binding.newMessageRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // CODE
            }
        })
    }

}

class UserItem(val user: User?): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.newMessageUsername.text = user?.username // Set username to text view
        Picasso.get().load(user?.avatar).into(viewHolder.itemView.newMessageAvatar) // Send avatar to image view
    }
}

