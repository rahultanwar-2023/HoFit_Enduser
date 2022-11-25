package com.hofit.hofituser.models

data class SessionModel(
    val url: String,
    val session_id: String,
    val session_title: String,
    val session_center: String,
    val session_date: String,
    val session_time: String,
    val session_amount: String,
    val session_discount: String
)