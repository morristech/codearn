package uz.codearn.codearnapp.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.constants.Constants
import uz.codearn.codearnapp.databinding.ActivityMainBinding
import uz.codearn.codearnapp.model.User

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var destinationChangedListener: NavController.OnDestinationChangedListener
    private val userCollectionRef = Firebase.firestore.collection("users")
    private val firebaseCurrentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavViewHeader()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragmentHome,
                R.id.fragmentLeaderboard,
                R.id.fragmentProfile
            ), binding.drawerLayout
        )

        destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.fragmentHome -> binding.navView.setCheckedItem(R.id.fragmentHome)
                    R.id.fragmentLeaderboard -> binding.navView.setCheckedItem(R.id.fragmentLeaderboard)
                    R.id.fragmentProfile -> binding.navView.setCheckedItem(R.id.fragmentProfile)
                }
            }

        binding.navView.setupWithNavController(navController)
        binding.bottomNavView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setupNavViewHeader()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavViewHeader() = CoroutineScope(Dispatchers.IO).launch {
        try {
            firebaseCurrentUser?.let {
                val snapshot =
                    userCollectionRef.whereEqualTo("userId", firebaseCurrentUser.uid).get()
                        .await().documents[0]
                val user = snapshot?.toObject(User::class.java)
                val navDrawerHeader = binding.navView.getHeaderView(0)
                user?.let {
                    withContext(Dispatchers.Main) {
                        navDrawerHeader.findViewById<ImageView>(R.id.user_profile_pic_header)
                            .setImageResource(Constants.getAllProfilePics()[user.profilePhotoIndex])
                        navDrawerHeader.findViewById<TextView>(R.id.user_name_header).text =
                            user.displayName
                        navDrawerHeader.findViewById<TextView>(R.id.user_score_header).text =
                            user.score.toString()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}