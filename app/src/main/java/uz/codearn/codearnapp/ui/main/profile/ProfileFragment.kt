package uz.codearn.codearnapp.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentProfileBinding
import uz.codearn.codearnapp.ui.auth.LoginActivity
import uz.codearn.codearnapp.ui.main.BaseFragment
import uz.codearn.codearnapp.ui.main.editprofile.EditProfileActivity

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    private val user = Firebase.auth.currentUser!!
    private lateinit var userUid: String

    override fun getLayoutId() = R.layout.fragment_profile
    override fun getViewModelClass() = ProfileViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        userUid = user.uid

        initEditButton()
        initTelegramChannelButton()
        initSignOutButton()
        initDeleteProfileBtn()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshData()
    }

    private fun initEditButton() {
        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initDeleteProfileBtn() {
        binding.deleteProfileBtn.setOnClickListener {
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserDataFromFirestore()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun deleteUserDataFromFirestore() {
        val userDoc = Firebase.firestore.collection("users").document(userUid)
        userDoc.delete()
    }

    private fun initSignOutButton() {
        binding.signOutBtnProfile.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun initTelegramChannelButton() {
        binding.telegramChannelBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/codearn_official"))
            startActivity(intent)
        }
    }
}