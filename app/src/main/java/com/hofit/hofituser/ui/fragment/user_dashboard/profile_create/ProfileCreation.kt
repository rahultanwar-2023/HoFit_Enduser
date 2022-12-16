package com.hofit.hofituser.ui.fragment.user_dashboard.profile_create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hofit.hofituser.R

class ProfileCreation : AppCompatActivity() {

    private lateinit var mEditName: TextInputEditText
    private lateinit var mEditDOB: TextInputEditText
    private lateinit var mEditEmail: TextInputEditText
    private lateinit var mSaveData: MaterialButton
    private lateinit var mClearData: MaterialButton

    private lateinit var mBackStack: ImageButton

    private var TAG = ProfileCreation::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_creation)
        init()

        mSaveData.setOnClickListener {
            saveUserData()
        }
        mClearData.setOnClickListener {
            clearUserData()
        }
        mBackStack.setOnClickListener {
            finish()
        }



    }

    private fun saveUserData() {
        val getName = mEditName.text.toString()
        val getDOB = mEditDOB.text.toString()
        val getEmail = mEditEmail.text.toString()

        if (getName.isNotEmpty() && getDOB.isNotEmpty() && getEmail.isNotEmpty()){
            if (Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()){
                userSaveToFirebase(getName, getDOB, getEmail)
            }else{
                Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Field Cannot be Empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userSaveToFirebase(getName: String, getDOB: String, getEmail: String) {

        val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        val mFirestore = FirebaseFirestore.getInstance().collection("hofit_user").document(currentUserUID)

        val userData = hashMapOf(
            "user_name" to getName,
            "user_dob" to getDOB,
            "user_email" to getEmail
        )

        mFirestore.set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Your Profile Saved", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "User Data Saved")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        clearUserData()

    }

    private fun clearUserData() {
        mEditName.setText("")
        mEditDOB.setText("")
        mEditEmail.setText("")
    }

    private fun init(){
        mEditName = findViewById(R.id.user_name1)
        mEditDOB = findViewById(R.id.user_dob1)
        mEditEmail = findViewById(R.id.user_email1)
        mSaveData = findViewById(R.id.btn_save_profile)
        mClearData = findViewById(R.id.btn_clear)

        mBackStack = findViewById(R.id.backToSettings)
    }

}