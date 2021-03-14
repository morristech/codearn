package uz.codearn.codearnapp.ui.main.editprofile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var lastSelectedProfile: CardView? = null
    private var selectedPicIndex = 0
    private var user = Firebase.auth.currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        initSaveBtn()
    }

    private fun initSaveBtn() {
        binding.saveBtn.setOnClickListener {
            updateUserInfo()
            finish()
        }
    }

    private fun updateUserInfo() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val map = getUserMap()
            if (map.isNotEmpty()) {
                Firebase.firestore.collection("users").document(user.uid)
                    .set(map, SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@EditProfileActivity,
                "Iltimos, internetingizni tekshiring!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getUserMap(): Map<String, Any> {
        var map = mapOf<String, Any>()
        val displayName = binding.userNameInput.text.toString()
        when {
            displayName.isEmpty() -> Toast.makeText(
                this,
                "Iltimos, ismingizni kiriting!",
                Toast.LENGTH_SHORT
            ).show()
            lastSelectedProfile == null -> Toast.makeText(
                this,
                "Iltimos, profil rasm tanlang!",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                map = mapOf<String, Any>(
                    "displayName" to displayName,
                    "profilePhotoIndex" to selectedPicIndex
                )
            }
        }
        return map
    }

    fun onProfilePicClicked(view: View) {
        if (lastSelectedProfile != null) {
            lastSelectedProfile!!.cardElevation = 5.0f
            val imageViewInsideView = lastSelectedProfile!!.getChildAt(0) as ImageView
            imageViewInsideView.alpha = 0.4f
        }
        val pictureContainerCard = view as CardView
        lastSelectedProfile = pictureContainerCard
        pictureContainerCard.cardElevation = 1.0f
        val imageViewInsideView = pictureContainerCard.getChildAt(0) as ImageView
        imageViewInsideView.alpha = 1.0f
        var index = 0
        for (child in binding.profilePicGridlayout.children) {
            if (((child as CardView).getChildAt(0) as ImageView).drawable == imageViewInsideView.drawable) {
                selectedPicIndex = index
                break
            }
            index++
        }
    }
}