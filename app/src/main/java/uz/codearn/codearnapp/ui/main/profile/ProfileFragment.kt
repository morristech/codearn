package uz.codearn.codearnapp.ui.main.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentProfileBinding
import uz.codearn.codearnapp.ui.auth.LoginActivity
import uz.codearn.codearnapp.ui.main.editprofile.EditProfileActivity

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val user = Firebase.auth.currentUser!!
    private val usersRef = Firebase.firestore.collection("users")
    private val viewModel: ProfileViewModel by lazy {
        val viewModelFactory = ProfileViewModelFactory(requireActivity().application)
        ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initEditButton()
        initTelegramChannelButton()
        initSignOutButton()
        initDeleteProfileBtn()

        return binding.root
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
            AlertDialog.Builder(context)
                .setTitle("Hisobni o'chirish")
                .setMessage("Hisobingizni o'chirishni xohlaysizmi?")
                .setPositiveButton("Ha") { _, _ ->
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            deleteUserDataFromFirestore()
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
                .setNegativeButton("Yo'q", null)
                .show()
        }
    }

    private fun deleteUserDataFromFirestore() = CoroutineScope(Dispatchers.IO).launch {
        val userQuery = usersRef.whereEqualTo("userId", user.uid).get().await()
        if (userQuery.documents.isNotEmpty()) {
            try {
                if (userQuery.documents.isNotEmpty()) {
                    for (doc in userQuery) {
                        usersRef.document(doc.id).delete().await()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initSignOutButton() {
        binding.signOutBtnProfile.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hisobdan chiqish")
                .setMessage("Hisobingizdan chiqishni xohlaysizmi?")
                .setPositiveButton("Ha") { _, _ ->
                    Firebase.auth.signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Yo'q", null)
                .show()
        }
    }

    private fun initTelegramChannelButton() {
        binding.telegramChannelBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/codearn_official"))
            startActivity(intent)
        }
    }
}