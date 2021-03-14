package uz.codearn.codearnapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentHomeBinding
import uz.codearn.codearnapp.ui.main.BaseFragment
import uz.codearn.codearnapp.ui.main.test.TestActivity

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun getLayoutId() = R.layout.fragment_home

    override fun getViewModelClass() = HomeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
    }
}