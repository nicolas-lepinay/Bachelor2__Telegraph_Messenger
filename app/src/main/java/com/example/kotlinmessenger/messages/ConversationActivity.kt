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
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.message_received_row.view.*
import kotlinx.android.synthetic.main.message_sent_row.view.*

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    var receiverUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerviewConversation.adapter = adapter

        receiverUser = intent.getParcelableExtra<User?>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = receiverUser?.username

        listenForMessages()

        binding.sendButtonConversation.setOnClickListener {
            performSendMessage()
        }
    }

    // Fetch messages from database :
    private fun listenForMessages() {
        val senderId = FirebaseAuth.getInstance().uid // Logged-in user's id
        val receiverId = receiverUser?.uid // Receiver's id

        val ref = FirebaseDatabase.getInstance().getReference("conversations/$senderId/$receiverId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if(message != null) {
                    // Message is sent by logged-in user :
                    if(message.senderId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(MessageSentItem(message.text, currentUser!!))
                    // Message is sent by another user :
                    } else {
                        adapter.add(MessageReceivedItem(message.text, receiverUser))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }

    // Send message to Firebase Database :
    private fun performSendMessage() {
        val receiver = intent.getParcelableExtra<User?>(NewMessageActivity.USER_KEY)
        val receiverId = receiver?.uid
        val senderId = FirebaseAuth.getInstance().uid
        val text = binding.textInputConversation.text.toString().trim()
        val timestamp = System.currentTimeMillis() / 1000

        if(senderId == null || receiverId == null) return

        // Current user's conversation :
        val ref = FirebaseDatabase.getInstance().getReference("conversations/$senderId/$receiverId").push()
        // Receiver's conversation :
        val receiverRef = FirebaseDatabase.getInstance().getReference("conversations/$receiverId/$senderId").push()
        // Latest message reference:
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("latest-messages/$senderId/$receiverId")
        // Receiver's latest message reference:
        val receiverLatestMessageRef = FirebaseDatabase.getInstance().getReference("latest-messages/$receiverId/$senderId")

        val message = Message(ref.key!!, text, senderId, receiverId, timestamp)

        ref.setValue(message)
            .addOnSuccessListener {
                Log.d("Conversation", getString(R.string.successful_sent_message_log))
                // Clears out text input :
                binding.textInputConversation.text.clear()
                // Scrolls down to last message :
                binding.recyclerviewConversation.scrollToPosition(adapter.itemCount - 1)
            }
        receiverRef.setValue(message)
        latestMessageRef.setValue(message)
        receiverLatestMessageRef.setValue(message)
    }
}

class MessageReceivedItem(val textContent: String, val user: User?): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_received_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // Load message's text content :
        viewHolder.itemView.messageReceivedRowText.text = textContent
        // Load user's avatar :
        Picasso.get().load(user?.avatar).into(viewHolder.itemView.messageReceivedRowAvatar)
    }
}

class MessageSentItem(val textContent: String, val user: User?): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_sent_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // Load message's text content :
        viewHolder.itemView.messageSentRowText.text = textContent
        // Load logged-in user's avatar :
        Picasso.get().load(user?.avatar).into(viewHolder.itemView.messageSentRowAvatar)
    }
}
