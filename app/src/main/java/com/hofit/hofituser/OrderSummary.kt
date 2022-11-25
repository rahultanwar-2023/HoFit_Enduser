package com.hofit.hofituser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.razorpay.Checkout

class OrderSummary : AppCompatActivity() {

    private lateinit var mOrderSummaryLayout1: ConstraintLayout
    private lateinit var mOrderSummaryLayout2: ConstraintLayout
    private lateinit var mOrderSummaryProgressBar: ProgressBar

    private lateinit var mCenterNameAndCat: TextView
    private lateinit var mOrderTimeAndDate: TextView
    private lateinit var mSessionAmount: TextView
    private lateinit var mTotalSessionAmount: TextView

    private var centerType: String? = null
    private var centerName: String? = null

    private var combinedDateTIme: String? = null

    private lateinit var mOrderUserName: TextInputEditText
    private lateinit var mOrderUserNumber: TextInputEditText
    private lateinit var mOrderUserEmail: TextInputEditText

    private lateinit var mBack: ImageButton

    private lateinit var mProceedOrder: Button

    private var totalAmount: Int? = null

    private val fireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        init()

        centerType = intent.getStringExtra("center_category")
        centerName = intent.getStringExtra("center_name")

        val orderDate = intent.getStringExtra("order_date")
        val orderTime = intent.getStringExtra("order_time")
        val orderAmount = intent.getStringExtra("session_amount")

        combinedDateTIme = ("$orderDate,$orderTime")

        mCenterNameAndCat.text =
            resources.getString(R.string.show_booking_center_name, centerType, centerName)
        mOrderTimeAndDate.text = combinedDateTIme

        mSessionAmount.text = orderAmount

        totalAmount = orderAmount!!.toInt()

        mTotalSessionAmount.text = totalAmount.toString()

        Checkout.preload(applicationContext)

        mProceedOrder.setOnClickListener {
            val mName = mOrderUserName.text.toString().trim()
            val mNumber = mOrderUserNumber.text.toString().trim()
            val mEmail = mOrderUserEmail.text.toString().trim()
            val regexStr = "(0/91)?[7-9][0-9]{9}".toRegex()

            if (mName.isNotEmpty() && mNumber.isNotEmpty() && mEmail.isNotEmpty()) {
                if (mNumber.matches(regexStr)) {
                    if (Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                        mOrderSummaryLayout1.visibility = View.GONE
                        mOrderSummaryProgressBar.visibility = View.VISIBLE
                        proceedToCheckOut(mName, mNumber, mEmail)
                        Handler(Looper.myLooper()!!).postDelayed({
                            mOrderSummaryProgressBar.visibility = View.INVISIBLE
                            mOrderSummaryLayout2.visibility = View.VISIBLE
                            Thread.sleep(3000)
                            finish()
                        }, 2500)
                    } else {
                        Toast.makeText(this, "Enter valid email id", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill the details", Toast.LENGTH_SHORT).show()
            }

        }

        mBack.setOnClickListener {
            finish()
        }

    }

    private fun proceedToCheckOut(mName: String, mNumber: String, mEmail: String) {
        userSaveToFirebase(mName, mNumber, mEmail)
        val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        val mFireStore =
            FirebaseFirestore.getInstance().collection("hofit_user").document(currentUserUID)
        val isFreeSlotLeft = hashMapOf(
            "free_trial" to false,
        )
        mFireStore.set(isFreeSlotLeft, SetOptions.merge())
    }

    private fun userSaveToFirebase(
        getName: String,
        getNumber: String,
        getEmail: String
    ) {

        val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        val mFireStore =
            FirebaseFirestore.getInstance().collection("hofit_user").document(currentUserUID)
                .collection("my_bookings").document("bookings").collection("upcoming_booking")
                .document()

        val id = mFireStore.id

        val userData = hashMapOf(
            "booking_id" to id,
            "booking_type" to "free_trial",
            "center_type" to centerType,
            "center_name" to centerName,
            "session_schedule" to combinedDateTIme,
            "user_name" to getName,
            "user_number" to "+91$getNumber",
            "user_email" to getEmail,
            "session_amount" to totalAmount
        )

        mFireStore.set(userData)

    }

    private fun init() {
        mOrderSummaryLayout1 = findViewById(R.id.order_summary_id)
        mOrderSummaryLayout2 = findViewById(R.id.after_slot_book_id)
        mOrderSummaryProgressBar = findViewById(R.id.load_freeSlot)
        mCenterNameAndCat = findViewById(R.id.set_center_cat_name)
        mOrderTimeAndDate = findViewById(R.id.showDateTime)
        mSessionAmount = findViewById(R.id.session_amount)
        mTotalSessionAmount = findViewById(R.id.total_payable_amount)
        mOrderUserName = findViewById(R.id.check_user_name1)
        mOrderUserNumber = findViewById(R.id.check_user_number1)
        mOrderUserEmail = findViewById(R.id.check_user_email1)
        mProceedOrder = findViewById(R.id.btn_proceed_final)
        mBack = findViewById(R.id.backToChangeOrder)
    }
}