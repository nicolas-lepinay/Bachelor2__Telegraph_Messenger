package com.example.kotlinmessenger.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.Message
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val message: Message): Item<GroupieViewHolder>() {
    var user: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        val userId: String
        // Si l'expéditeur est l'utilisateur loggé :
        if(message.senderId == FirebaseAuth.getInstance().uid) {
            userId = message.receiverId
        } else {
            userId = message.senderId
        }

        val ref = FirebaseDatabase.getInstance().getReference("users/$userId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                viewHolder.itemView.latestMessageRowUsername.text = user?.username // Load username
                Picasso.get().load(user?.avatar).into(viewHolder.itemView.latestMessageRowAvatar) // Load avatar
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        viewHolder.itemView.latestMessageRowText.text = message.text // Load latest message
    }
}