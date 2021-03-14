package uz.codearn.codearnapp.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.codearn.codearnapp.databinding.PathsListItemBinding
import uz.codearn.codearnapp.model.CodingPath

class CodingPathsAdapter(private val clickListener: CodingPathClickListener) :
    ListAdapter<CodingPath, CodingPathsAdapter.CodingPathViewHolder>(DiffUtilCallback) {

    class CodingPathViewHolder(private val binding: PathsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CodingPathClickListener, codingPath: CodingPath) {
            binding.codingPath = codingPath
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodingPathViewHolder {
        return CodingPathViewHolder(
            PathsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CodingPathViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }

    private companion object DiffUtilCallback : DiffUtil.ItemCallback<CodingPath>() {
        override fun areItemsTheSame(oldItem: CodingPath, newItem: CodingPath): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CodingPath, newItem: CodingPath): Boolean {
            return oldItem.name == newItem.name
        }

    }
}

class CodingPathClickListener(val clickListener: (CodingPathName: String) -> Unit) {
    fun onClick(codingPath: CodingPath) = if (codingPath.available) {
        clickListener(codingPath.name)
    } else {
        clickListener("Tez kunda...")
    }
}