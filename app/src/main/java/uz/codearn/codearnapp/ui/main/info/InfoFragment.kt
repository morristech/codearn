package uz.codearn.codearnapp.ui.main.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private lateinit var navigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInfoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false)
        navigationView = activity?.findViewById(R.id.bottomNavView)!!
        navigationView.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        navigationView.visibility = View.VISIBLE
    }


}