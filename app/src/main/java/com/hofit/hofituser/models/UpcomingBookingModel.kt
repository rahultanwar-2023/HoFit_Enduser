package com.hofit.hofituser.models

data class UpcomingBookingModel(
    val booking_outlet: String,
    val booking_category: String,
    val booking_time: String,
    val booking_address: String
)