package com.hofit.hofituser.ui.fragment.user_dashboard.outlet_details

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.OrderSummary
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.CenterImageAdapter
import com.hofit.hofituser.adapter.ServiceAdapter
import com.hofit.hofituser.models.CenterImageModel
import com.hofit.hofituser.models.ServiceModel
import com.hofit.hofituser.ui.fragment.user_location.UserLocation
import java.text.SimpleDateFormat
import java.util.*


class CenterFullDetails : Fragment() {

    private lateinit var viewPager3: ViewPager2
    private lateinit var handler1: Handler
    private lateinit var mCenterImageAdapter: CenterImageAdapter
    private val TAG = UserLocation::class.java.simpleName

    private lateinit var mCenterId: String

    private lateinit var mCenterName: TextView
    private lateinit var mCenterAddress: TextView
    private lateinit var mCenterRating: TextView

    private lateinit var mCenterServiceView: RecyclerView

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mDocReference: DocumentReference
    private lateinit var mDocReference1: DocumentReference

    private lateinit var mBookFreeSlot: Button

    private lateinit var dialog: BottomSheetDialog

    private lateinit var mEditTextDate: TextInputEditText
    private lateinit var mEditTextTime: TextInputEditText

    private var mDaySelected: String? = null
    private var mDateSelected: String? = null
    private var mDateDB: String? = null
    private lateinit var mTimeSelected: String

    private lateinit var mTitle: String
    private lateinit var centerCategory: String
    private lateinit var centerAddress: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_center_full_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        fetchCenterImages()
        viewPager3.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager3.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler1.removeCallbacks(runnable)
                handler1.postDelayed(runnable, 3000)
            }
        })

        fetchCenterDetails()
        fetchCenterServices()

        checkButtonStatus()

        mBookFreeSlot.setOnClickListener {
            bookFreeSlot(view)
        }

    }

    private fun checkButtonStatus() {
        val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        val db = Firebase.firestore.collection("hofit_user").document(currentUserUID)
        db.get()
            .addOnSuccessListener{ checkButton ->
                if (checkButton != null) {
                    val freeTrial = checkButton.get("free_trial").toString()
                    if (freeTrial == "false"){
                        mBookFreeSlot.visibility = View.INVISIBLE
                    } else {
                        mBookFreeSlot.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun bookFreeSlot(view: View) {
        dialog = BottomSheetDialog(view.context)
        val view1 = layoutInflater.inflate(R.layout.layout_timing, null)
        val openDate = view1.findViewById<ImageButton>(R.id.select_data_icon)
        mEditTextDate = view1.findViewById(R.id.select_date1)
        mEditTextTime = view1.findViewById(R.id.select_time1)

        val myCalendar = Calendar.getInstance()

        val dataPicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }

        openDate.setOnClickListener {
            val dataPick = DatePickerDialog(
                view1.context,
                dataPicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dataPick.datePicker.minDate = System.currentTimeMillis() - 1000
            dataPick.show()
        }
        val openTime = view1.findViewById<ImageButton>(R.id.select_time_icon)
        openTime.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext()).create()
            val view2 = layoutInflater.inflate(R.layout.layout_time_slot, null)
            val radioGroup: RadioGroup = view2.findViewById(R.id.radioGroup)
            val radioButton1: RadioButton = view2.findViewById(R.id.radioTime)
            val radioButton2: RadioButton = view2.findViewById(R.id.radioTime1)
            val radioButton3: RadioButton = view2.findViewById(R.id.radioTime2)
            val radioButton4: RadioButton = view2.findViewById(R.id.radioTime3)
            val radioButton5: RadioButton = view2.findViewById(R.id.radioTime4)
            val radioButton6: RadioButton = view2.findViewById(R.id.radioTime5)
            val radioButton7: RadioButton = view2.findViewById(R.id.radioTime6)
            val radioButton8: RadioButton = view2.findViewById(R.id.radioTime7)
            val radioButton9: RadioButton = view2.findViewById(R.id.radioTime8)
            val radioButton10: RadioButton = view2.findViewById(R.id.radioTime9)
            val textViewDay: TextView = view2.findViewById(R.id.dialog_date)
            textViewDay.text = mDateSelected
            mDocReference1
                .get()
                .addOnSuccessListener { time ->
                    if (time != null) {
                        val timeSlot1 = time.get("time_1").toString()
                        radioButton1.text = timeSlot1
                        val timeSlot2 = time.get("time_2").toString()
                        radioButton2.text = timeSlot2
                        val timeSlot3 = time.get("time_3").toString()
                        radioButton3.text = timeSlot3
                        val timeSlot4 = time.get("time_4").toString()
                        radioButton4.text = timeSlot4
                        val timeSlot5 = time.get("time_5").toString()
                        radioButton5.text = timeSlot5
                        val timeSlot6 = time.get("time_6").toString()
                        radioButton6.text = timeSlot6
                        val timeSlot7 = time.get("time_7").toString()
                        radioButton7.text = timeSlot7
                        val timeSlot8 = time.get("time_8").toString()
                        radioButton8.text = timeSlot8
                        val timeSlot9 = time.get("time_9").toString()
                        radioButton9.text = timeSlot9
                        val timeSlot10 = time.get("time_10").toString()
                        radioButton10.text = timeSlot10
                    }
                }
            val doneBTN : Button = view2.findViewById(R.id.select_time_btn)
            doneBTN.setOnClickListener {
                try {
                    val selectedId = radioGroup.checkedRadioButtonId
                    val radioButton: RadioButton = view2.findViewById(selectedId)
                    val time = radioButton.text.toString().trim()
                    mEditTextTime.setText(time)
                    mTimeSelected = time
                    builder.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Pick your timing..!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.setView(view2)
            builder.show()
        }

        val proceedBtn = view1.findViewById<Button>(R.id.btn_proceed)
        proceedBtn.setOnClickListener {
            if (mEditTextDate.text.toString().isNotEmpty() && mEditTextTime.text.toString()
                    .isNotEmpty()
            ) {
                dialog.dismiss()
                val intent = Intent(requireContext(), OrderSummary::class.java)
                intent.putExtra("outlet_category", centerCategory)
                intent.putExtra("outlet_name", mTitle)
                intent.putExtra("order_date", mDateSelected)
                intent.putExtra("order_time", mTimeSelected)
                intent.putExtra("outlet_address", centerAddress)
                intent.putExtra("session_amount", "0")
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Select Your Date and Time", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.setContentView(view1)
        dialog.show()
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val myFormatDB = "dd_MM_yyyy"
        val day = "dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val sdfDay = SimpleDateFormat(day, Locale.getDefault())
        val sdfDB = SimpleDateFormat(myFormatDB, Locale.getDefault())
        val dateString = sdf.format(myCalendar.time).toString()
        val dayString = sdfDay.format(myCalendar.time).toString()
        val dayStringDB = sdfDB.format(myCalendar.time).toString()
        mEditTextDate.setText(dateString, TextView.BufferType.EDITABLE)
        mDaySelected = dayString
        mDateSelected = dateString
        mDateDB = dayStringDB
    }

    private fun init(view: View) {

        mCenterId = requireArguments().getString("outlet_id").toString()

        viewPager3 = view.findViewById(R.id.center_image_viewpager)
        handler1 = Handler(Looper.myLooper()!!)

        mCenterName = view.findViewById(R.id.center_name_all)
        mCenterAddress = view.findViewById(R.id.center_fullAddress)
        mCenterRating = view.findViewById(R.id.avg_rating)

        mCenterServiceView = view.findViewById(R.id.grid_recyclerView_service)
        mCenterServiceView.layoutManager = GridLayoutManager(view.context, 2)

        mFirestore = FirebaseFirestore.getInstance()

        mDocReference =
            mFirestore.collection("super_admin").document("rohit-20072022")
                .collection("sports_centers").document(mCenterId)

        mDocReference1 = mFirestore.collection("super_admin").document("rohit-20072022")
            .collection("free_trial_time").document("time_slot")

        mBookFreeSlot = view.findViewById(R.id.book_a_slot)

    }

    private val runnable = Runnable {
        viewPager3.currentItem = viewPager3.currentItem + 1
    }

    private fun fetchCenterImages() {
        val centerImageList = ArrayList<CenterImageModel>()

        mDocReference.collection("outlet_images")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("outlet_image").toString()
                    centerImageList.add(CenterImageModel(keyImage))
                    Log.i(
                        TAG,
                        keyImage
                    )
                }
                mCenterImageAdapter = CenterImageAdapter(requireActivity(), centerImageList, viewPager3)
                viewPager3.adapter = mCenterImageAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun fetchCenterDetails() {

        mDocReference
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    mTitle = result.get("outlet_name").toString()
                    centerCategory = result.get("outlet_category").toString()
                    centerAddress = result.get("outlet_address").toString()
//                    val rating = result.get("center_rating").toString()

                    mCenterName.text = mTitle
                    mCenterAddress.text = centerAddress
//                    mCenterRating.text = rating
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun fetchCenterServices() {
        val serviceList = ArrayList<ServiceModel>()

        mDocReference.collection("facilities")
            .get()
            .addOnSuccessListener { service ->
                for (result in service) {
                    serviceList.add(ServiceModel(result.get("outlet_f").toString()))
                    Log.d(TAG, "fetchCenterServices: $serviceList")
                }
                val serviceAdapter = ServiceAdapter(serviceList)
                mCenterServiceView.adapter = serviceAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
}