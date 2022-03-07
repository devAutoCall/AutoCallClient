package com.example.dtpapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TokenInfoModel @JvmOverloads constructor(
    @JvmField var phone_number: String = "",
    @JvmField var token: String = "",
) : Parcelable
