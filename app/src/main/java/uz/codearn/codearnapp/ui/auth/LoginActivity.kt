package uz.codearn.codearnapp.ui.auth

import android.content.Intent
import android.os.Bundle
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
import uz.codearn.codearnapp.databinding.ActivityLoginBinding
import uz.codearn.codearnapp.model.User
import uz.codearn.codearnapp.ui.main.MainActivity
import uz.codearn.codearnapp.ui.main.editprofile.EditProfileActivity
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val userCollectionRef = Firebase.firestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar?.hide()
        auth = Firebase.auth

        initLoginButton()
    }

    private fun initLoginButton() {
        binding.loginBtn.setOnClickListener {
            binding.loginBtn.visibility = View.INVISIBLE
            if (binding.phoneNumberInput.text.isNotEmpty() && binding.phoneNumberInput.text.contains(
                    "+"
                )
            ) {
                val enteredPhoneNumber = binding.phoneNumberInput.text.toString()
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(enteredPhoneNumber)
                    .setTimeout(120L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                binding.loginBtn.visibility = View.VISIBLE
                                signInWithCredential(phoneAuthCredential)
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                binding.loginBtn.visibility = View.VISIBLE
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
                                binding.loginBtn.visibility = View.VISIBLE
                                val intent = Intent(this@LoginActivity, SendOTPActivity::class.java)
                                intent.putExtra("PHONE_NUMBER", enteredPhoneNumber)
                                intent.putExtra("VERIFICATION_ID", verificationId)
                                intent.putExtra("RESENDING_TOKEN", token)
                                startActivity(intent)
                            }

                        }
                    ).build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                binding.phoneNumberInput.error = "Iltimos, telefon raqamingizni kiriting"
                binding.loginBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun signInWithCredential(phoneAuthCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            if (it.isSuccessful) {
                val phoneNumber = binding.phoneNumberInput.text.toString()
                val currentUser = Firebase.auth.currentUser
                currentUser?.let { saveUser(User(currentUser.uid, phoneNumber)) }
                startEditProfileActivity()
            } else {
                showSnackbar("Internetga ulaning va qayta urinib ko'ring")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startMainActivity()
        } else {
            binding.phoneNumberInput.requestFocus()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.activityLogin, message, Snackbar.LENGTH_LONG)
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
                        this@LoginActivity,
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
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}