package com.example.kotlinmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val avatar: String): Parcelable {
    constructor() : this("", "", "")
}