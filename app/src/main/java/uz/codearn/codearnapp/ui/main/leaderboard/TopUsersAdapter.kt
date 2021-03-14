package uz.codearn.codearnapp.ui.main.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.codearn.codearnapp.databinding.UsersListItemBinding
import uz.codearn.codearnapp.model.User

class TopUsersAdapter : ListAdapter<User, TopUsersAdapter.TopUserViewHolder>(DiffUtilCallback) {
    class TopUserViewHolder(val binding: UsersListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()
        }
    }

    private companion object DiffUtilCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUserViewHolder {
        return TopUserViewHolder(
            UsersListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TopUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.binding.userPlace.text = (position + 1).toString()
        holder.binding.userProfilePic
    }
}