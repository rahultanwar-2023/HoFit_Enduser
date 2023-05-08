package com.hofit.hofituser

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PaymentPass : AppCompatActivity(), PaymentResultListener {

    private lateinit var context: Context

    private lateinit var mPassType: TextView
    private lateinit var mPassSessionLimit: TextView
    private lateinit var mPassStartDate: TextView
    private lateinit var mPassUserName: TextInputEditText
    private lateinit var mPassUserNumber: TextInputEditText
    private lateinit var mPassUserEmail: TextInputEditText
    private lateinit var mPassActualPrice: TextView
    private lateinit var mPassDiscountPrice: TextView
    private lateinit var mPassPayableAmount: TextView
    private lateinit var mPayToProceed: Button
    private lateinit var mBackOneKey: ImageButton

    private lateinit var mSessionTitle: TextView
    private lateinit var mSessionCenter: TextView
    private lateinit var mSessionDate: TextView

    //Layout Switch Between Session
    private lateinit var mLayoutPass: ConstraintLayout
    private lateinit var mLayoutSession: ConstraintLayout

    //Center String Store Data
    private var sessionName: String? = null
    private var centerName: String? = null
    private var combinedDateTIme: String? = null
    private var totalPayableAmount: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_pass)
        init()

        mBackOneKey.setOnClickListener {
            finish()
        }

        val getSessionId = intent.getStringExtra("layout_id")

        if (getSessionId == "Session_id_59"){
            showSessionLayout()
        }else {
            showOneKeyLayout()
        }

        mPayToProceed.setOnClickListener {
            proceedToPay()
        }

    }

    private fun init() {
        mPassType = findViewById(R.id.pass_type)
        mPassSessionLimit = findViewById(R.id.number_session_limit)
        mPassStartDate = findViewById(R.id.pass_start_date)
        mPassUserName = findViewById(R.id.check_user_name3)
        mPassUserNumber = findViewById(R.id.check_user_number3)
        mPassUserEmail = findViewById(R.id.check_user_email3)
        mPassActualPrice = findViewById(R.id.total_amount)
        mPassDiscountPrice = findViewById(R.id.hofit_discount)
        mPassPayableAmount = findViewById(R.id.total_amount_pay)
        mPayToProceed = findViewById(R.id.proceed_to_pay)
        mBackOneKey = findViewById(R.id.back_to_oneKey)

        mSessionTitle = findViewById(R.id.session_title_1)
        mSessionCenter = findViewById(R.id.session_center1)
        mSessionDate = findViewById(R.id.session_time1)

        mLayoutPass = findViewById(R.id.constraintLayout)
        mLayoutSession = findViewById(R.id.constraintLayout3)
    }

    private fun proceedToPay() {
        val mName = mPassUserName.text.toString().trim()
        val mNumber = mPassUserNumber.text.toString().trim()
        val mEmail = mPassUserEmail.text.toString().trim()
        val regexStr = "(0/91)?[7-9][0-9]{9}".toRegex()

        if (mName.isNotEmpty() && mNumber.isNotEmpty() && mEmail.isNotEmpty()) {
            if (mNumber.matches(regexStr)) {
                if (Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    bookingOrderProceed(mName, mNumber, mEmail)
                    Handler(Looper.myLooper()!!).postDelayed({

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOneKeyLayout() {

        val getPassType = intent.getStringExtra("pass_type")
        val getPassSessionLimit = intent.getStringExtra("pass_session")
        val getPassActualPrice = intent.getStringExtra("pass_actual")
        val getPassDiscountPrice = intent.getStringExtra("pass_price")

        mPassType.text = "$getPassType Month"
        mPassSessionLimit.text = getPassSessionLimit

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss")
        val formatted = current.format(formatter)

        combinedDateTIme = formatted
        mPassStartDate.text = formatted

        mPassActualPrice.text = getPassActualPrice
        //mPassDiscountPrice.text = getPassDiscountPrice
        mPassPayableAmount.text = getPassDiscountPrice

        val startIndex = 1
        val endIndexActualPrice = getPassActualPrice!!.length
        val endIndexDiscountPrice = getPassDiscountPrice!!.length
        val substring1 = getPassActualPrice.subSequence(startIndex, endIndexActualPrice).toString()
        val substring2 = getPassDiscountPrice.subSequence(startIndex, endIndexDiscountPrice).toString()

        val passPayableAmount = substring1.toInt() - substring2.toInt()

        val discount = "₹$passPayableAmount"

        mPassDiscountPrice.text = discount

    }

    private fun showSessionLayout() {
        mLayoutPass.visibility = View.INVISIBLE
        mLayoutSession.visibility = View.VISIBLE

        val getSessionTitle = intent.getStringExtra("session_title")
        val getSessionCenter = intent.getStringExtra("session_center")
        val getSessionDate = intent.getStringExtra("session_date")
        val getSessionAmount = "₹" + intent.getStringExtra("session_amount")
        val getSessionDiscountAmount = "₹" + intent.getStringExtra("session_discount")

        mSessionTitle.text = getSessionTitle
        sessionName = getSessionTitle
        mSessionCenter.text = getSessionCenter
        centerName = getSessionCenter
        mSessionDate.text = getSessionDate
        combinedDateTIme = getSessionDate
        mPassActualPrice.text = getSessionAmount

        val startIndex = 1
        val endIndexActualPrice = getSessionAmount.length
        val endIndexDiscountPrice = getSessionDiscountAmount.length
        val substring1 = getSessionAmount.subSequence(startIndex, endIndexActualPrice).toString()
        val substring2 = getSessionDiscountAmount.subSequence(startIndex, endIndexDiscountPrice).toString()

        val passPayableAmount = substring1.toInt() - substring2.toInt()

        totalPayableAmount = passPayableAmount.toString()
        val payableAmount = "₹$passPayableAmount"

        mPassPayableAmount.text = payableAmount

    }

    private fun bookingOrderProceed(
        getName: String,
        getNumber: String,
        getEmail: String
    ) {

        val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        val mFireStore = Firebase.firestore.collection("hofit_user").document(currentUserUID)
            .collection("my_bookings").document("bookings").collection("upcoming_booking")
            .document()

        val id = mFireStore.id


        val userData = hashMapOf(
            "book_identity" to id,
            "session_name" to sessionName,
            "center_name" to centerName,
            "session_schedule" to combinedDateTIme,
            "user_name" to getName,
            "user_number" to "+91$getNumber",
            "user_email" to getEmail,
            "session_amount" to totalPayableAmount
        )

        mFireStore.set(userData)

    }

    //-----------------Payment Process ------------------

    private fun proceedToPayment() {
        val activity: PaymentPass = this
        val co = Checkout()
        co.setKeyID("rzp_test_wr3Kaxu788qsor")

        val razorAmount = (100)

        try {
            val options = JSONObject()

            options.put("name", "HoFIT")
            options.put("image", R.mipmap.ic_launcher_round)
            options.put("theme.color", "#F2785C");
            options.put("currency", "INR");
            //options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount", "000")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email", "mEmail")
            prefill.put("contact", "+91mNumber")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {}

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.i("RazorPay", "onPaymentError: $p1")
        Toast.makeText(context, "$p1", Toast.LENGTH_SHORT).show()
    }

}