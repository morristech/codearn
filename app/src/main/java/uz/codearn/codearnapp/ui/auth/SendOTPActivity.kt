package uz.codearn.codearnapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.ActivitySendOTPBinding
import uz.codearn.codearnapp.model.User
import uz.codearn.codearnapp.ui.main.MainActivity
import uz.codearn.codearnapp.ui.main.editprofile.EditProfileActivity
import java.util.concurrent.TimeUnit

class SendOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendOTPBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String
    private lateinit var timer: CountDownTimer
    private val userCollectionRef = Firebase.firestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_o_t_p)
        auth = Firebase.auth
        phoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()
        supportActionBar?.hide()

        initVerifyButton()
        initBackButton()
        initClearanceText()
        initTimer()
    }

    private fun initClearanceText() {
        binding.enterVerificationCodeClearance.text =
            getString(R.string.enter_verification_code_clearance, phoneNumber)
    }

    private fun initTimer() {
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedTime = DateUtils.formatElapsedTime(millisUntilFinished / 1000)
                binding.resendCodeCountTv.text =
                    getString(R.string.resend_verification_code_count, elapsedTime)
            }

            override fun onFinish() {
                resendVerificationCode(phoneNumber, intent.getParcelableExtra("RESENDING_TOKEN"))
                binding.resendCodeCountTv.visibility = View.INVISIBLE
            }

        }

        timer.start()
    }

    private fun initBackButton() {
        binding.backButtonAuth.setOnClickListener {
            finish()
        }
    }

    private fun initVerifyButton() {
        binding.verifyBtn.setOnClickListener {
            val enteredVerificationCode = binding.verificationCodeInput.text
            if (!enteredVerificationCode.isNullOrEmpty() && enteredVerificationCode.length == 6) {
                val verificationCode = intent.getStringExtra("VERIFICATION_ID")
                if (verificationCode != null) {
                    val credential = PhoneAuthProvider.getCredential(
                        verificationCode,
                        enteredVerificationCode.toString()
                    )
                    signInWithCredential(credential)
                }
            } else {
                binding.verificationCodeInputContainer.error = "Kodni to'g'ri kiriting"
            }
        }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> showSnackbar("Xato telefon raqam kiritildi")
                        is FirebaseTooManyRequestsException -> showSnackbar("Siz ko'p so'rov yuborganingiz uchun bloklandingiz")
                        else -> showSnackbar("Internetga ulaning va qayta urinib ko'ring")
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    val intent = Intent(this@SendOTPActivity, SendOTPActivity::class.java)
                    intent.putExtra("PHONE_NUMBER", phoneNumber)
                    intent.putExtra("VERIFICATION_ID", verificationId)
                    intent.putExtra("RESENDING_TOKEN", token)
                    startActivity(intent)
                    finish()
                }

            })
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startMainActivity()
        } else {
            binding.verificationCodeInput.requestFocus()
        }
    }

    private fun signInWithCredential(phoneAuthCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUser = Firebase.auth.currentUser
                currentUser?.let { saveUser(User(currentUser.uid, phoneNumber)) }
                startEditProfileActivity()
            } else {
                val snackbar =
                    Snackbar.make(binding.activitySendOtp, "Kodni xato", Snackbar.LENGTH_LONG)
                val layoutParams = CoordinatorLayout.LayoutParams(snackbar.view.layoutParams)
                layoutParams.setMargins(0, 16, 0, 0)
                snackbar.view.layoutParams = layoutParams
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.show()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        timer.cancel()
        finish()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    private fun startEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
        timer.cancel()
        finish()
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.activitySendOtp, message, Snackbar.LENGTH_LONG)
        val layoutParams = CoordinatorLayout.LayoutParams(snackbar.view.layoutParams)
        layoutParams.setMargins(0, 16, 0, 0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }

    private fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val userQuery = userCollectionRef.whereEqualTo("userId", user.userId).get().await()
            if (userQuery.documents.isEmpty()) {
                userCollectionRef.add(user).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SendOTPActivity,
                        "Muvaffaqiyatli ro'yxatdan o'tildi!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                startMainActivity()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SendOTPActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}