package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.databinding.ActivityConversationBinding
import com.example.kotlinmessenger.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val user = intent.getParcelableExtra<User?>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username

        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(MessageReceivedItem())
        adapter.add(MessageSentItem())
        adapter.add(MessageReceivedItem())
        adapter.add(MessageSentItem())
        adapter.add(MessageReceivedItem())
        adapter.add(MessageSentItem())
        adapter.add(MessageReceivedItem())
        adapter.add(MessageSentItem())
        adapter.add(MessageReceivedItem())

        binding.recyclerviewConversation.adapter = adapter
    }
}

class MessageReceivedItem: Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_received_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}

class MessageSentItem: Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_sent_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}