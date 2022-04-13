package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_received_row.view.*

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