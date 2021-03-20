package uz.codearn.codearnapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentHomeBinding
import uz.codearn.codearnapp.ui.main.test.TestActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by lazy {
        val viewModelFactory = HomeViewModelFactory(requireActivity().application)
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.codingPathList.adapter = CodingPathsAdapter(CodingPathClickListener {
            if (it == "Tez kunda...") {
                Toast.makeText(activity, "Tez kunda...", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, TestActivity::class.java)
                intent.putExtra("PROGRAMMING_LANG", it)
                startActivity(intent)
            }
        })

        binding.newsFeedRead.setOnClickListener {
            val bottomNavView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView)
            bottomNavView.visibility = View.INVISIBLE
            requireActivity().findNavController(R.id.navHostFragment).navigate(R.id.fragmentInfo)
        }

        return binding.root
    }
}