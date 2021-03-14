package uz.codearn.codearnapp.ui.main.leaderboard

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.FragmentLeaderboardBinding
import uz.codearn.codearnapp.ui.main.BaseFragment

class LeaderboardFragment : BaseFragment<FragmentLeaderboardBinding, LeaderboardViewModel>() {

    override fun getLayoutId() = R.layout.fragment_leaderboard

    override fun getViewModelClass() = LeaderboardViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.topUsersList.adapter = TopUsersAdapter()

        binding.topUsersList.setDivider(R.drawable.recycler_view_divider)
    }

    private fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
        val divider = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
        )
        val drawable = ContextCompat.getDrawable(
            this.context,
            drawableRes
        )
        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }
}