package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityConversationBinding
import com.example.kotlinmessenger.models.Message
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.message_received_row.view.*
import kotlinx.android.synthetic.main.message_sent_row.view.*

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerviewConversation.adapter = adapter

        val user = intent.getParcelableExtra<User?>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username

        //setupDummyData()
        listenForMessages()

        binding.sendButtonConversation.setOnClickListener {
            performSendMessage()
        }
    }

    private fun setupDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(MessageReceivedItem("Hello Nick! How're you? :)"))
        adapter.add(MessageSentItem("This is my reply."))
        adapter.add(MessageReceivedItem("Hello Nick! How're you? :)"))
        adapter.add(MessageSentItem("This is my reply."))
        adapter.add(MessageReceivedItem("Hello Nick! How're you? :)"))
        adapter.add(MessageSentItem("This is my reply."))
        adapter.add(MessageReceivedItem("Hello Nick! How're you? :)"))
        adapter.add(MessageSentItem("This is my reply."))

        binding.recyclerviewConversation.adapter = adapter
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("messages")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if(message != null) {
                    // Message is sent by logged-in user :
                    if(message.senderId == FirebaseAuth.getInstance().uid) {
                        adapter.add(MessageSentItem(message.text))
                    // Message is sent by another user :
                    } else {
                        adapter.add(MessageReceivedItem(message.text))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }

    private fun performSendMessage() {
        // SEND MESSAGE TO FIREBASE
        val ref = FirebaseDatabase.getInstance().getReference("messages").push()

        val receiver = intent.getParcelableExtra<User?>(NewMessageActivity.USER_KEY)
        val receiverId = receiver?.uid
        val senderId = FirebaseAuth.getInstance().uid
        val text = binding.textInputConversation.text.toString()
        val timestamp = System.currentTimeMillis() / 1000

        if(senderId == null || receiverId == null) return

        val message = Message(ref.key!!, text, senderId, receiverId, timestamp)

        ref.setValue(message)
            .addOnSuccessListener {
                Log.d("Conversation", getString(R.string.successful_sent_message_log))
            }
    }
}

class MessageReceivedItem(val textContent: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_received_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageReceivedRowText.text = textContent
    }
}

class MessageSentItem(val textContent: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_sent_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageSentRowText.text = textContent
    }
}
