package com.example.kotlinmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class Message(val id: String, val text: String, val senderId: String, val receiverId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}