package com.nabil.submission1_appstory.Data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class UserModel(
    var email: String?,
    var password: String?
): Parcelable
