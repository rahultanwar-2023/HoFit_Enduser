package com.hofit.hofituser

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hofit.hofituser.models.OnBoardingData
import com.hofit.hofituser.ui.OnBoard.OnBoardAdapter
import java.util.concurrent.TimeUnit

class OnBoarding : Fragment() {

    private var onBoardAdapter: OnBoardAdapter? = null
    private var tabLayout: TabLayout? = null
    private var onBoardViewPager: ViewPager? = null
    var btnNext: FloatingActionButton? = null
    var btnGetStarted: Button? = null
    var position = 0

    //Mobile Number Verification
    var dialog: BottomSheetDialog? = null
    private lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var number: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    //Dialog Views
    private var verifyOTP: Button? = null
    private var loginLoading: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.action_onBoarding_to_userLocation)
        }

        tabLayout = view.findViewById(R.id.pagerSlider_indicator)
        btnNext = view.findViewById(R.id.btn_next)
        btnGetStarted = view.findViewById(R.id.btn_getStarted)

        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(R.string.onboardTitle1),
                R.drawable.onemorepass
            )
        )
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(R.string.onboardTitle2),
                R.drawable.easytouse
            )
        )
        /*onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(R.string.onboardTitle3),
                R.drawable.bestguidence
            )
        )
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(R.string.onboardTitle4),
                R.drawable.shareble
            )
        )*/
        setOnBoardingViewPagerAdapter(view, onBoardingData)

        position = onBoardViewPager!!.currentItem
        btnNext?.setOnClickListener {
            if (position < onBoardingData.size) {
                position++
                onBoardViewPager!!.currentItem = position
            }
        }
        btnGetStarted?.setOnClickListener {
            dialog = BottomSheetDialog(view.context)
            val view1 = layoutInflater.inflate(R.layout.layout_user_registration, null)
            verifyOTP = view1.findViewById(R.id.send_otp)
            loginLoading = view1.findViewById(R.id.progressBarMobileLogin)
            verifyOTP!!.setOnClickListener {
                login(view1)
            }
            dialog!!.setContentView(view1)
            dialog!!.show()
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    btnNext!!.visibility = View.INVISIBLE
                    btnGetStarted!!.visibility = View.VISIBLE
                } else {
                    btnGetStarted!!.visibility = View.INVISIBLE
                    btnNext!!.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                }
                loginLoading!!.visibility = View.INVISIBLE
                verifyOTP!!.visibility = View.VISIBLE
                Toast.makeText(view.context, "Try Again Later", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                dialog!!.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(
                        R.id.action_onBoarding_to_OTPVerify,
                        Bundle().apply {
                            putString("storedVerificationId", storedVerificationId)
                            putParcelable("resendToken", resendToken)
                            putString("userMobileNumber", number)
                        })
                    loginLoading!!.visibility = View.INVISIBLE
                    verifyOTP!!.visibility = View.VISIBLE
                }, 190)
            }
        }
        return view
    }

    private fun setOnBoardingViewPagerAdapter(view: View, onBoardingData: List<OnBoardingData>) {
        onBoardViewPager = view.findViewById(R.id.onboarding_Pager)
        onBoardAdapter = OnBoardAdapter(view.context, onBoardingData)
        onBoardViewPager!!.adapter = onBoardAdapter
        tabLayout?.setupWithViewPager(onBoardViewPager)
    }

    private fun login(view: View) {
        val mobileNumber = view.findViewById<TextInputEditText>(R.id.user_mobileNumber1)
        number = mobileNumber.text.toString().trim()
        val regexStr =
            "(0/91)?[7-9][0-9]{9}".toRegex()
        if (number.isNotEmpty()) {
            if (number.matches(regexStr)) {
                verifyOTP!!.visibility = View.INVISIBLE
                loginLoading!!.visibility = View.VISIBLE
                number = "+91$number"
                sendVerificationCode(number)
            } else {
                Toast.makeText(view.context, "Enter valid number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(view.context, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Authentication Completed", Toast.LENGTH_SHORT)
                        .show()
                    loginLoading!!.visibility = View.INVISIBLE
                    verifyOTP!!.visibility = View.VISIBLE
                    findNavController().navigate(R.id.action_onBoarding_to_userLocation)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}