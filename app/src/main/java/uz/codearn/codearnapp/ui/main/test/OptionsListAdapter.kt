package uz.codearn.codearnapp.ui.main.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.codearn.codearnapp.databinding.OptionItemViewBinding

class OptionsListAdapter(private val data: List<String>) :
    RecyclerView.Adapter<OptionsListAdapter.OptionViewHolder>() {
    private var optionsList = data
    var onItemClick: ((Int) -> Unit)? = null

    inner class OptionViewHolder(val binding: OptionItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: String, position: Int) {
            binding.optionText.text = option
            binding.optionLetter.text = when (position) {
                0 -> "A"
                1 -> "B"
                else -> "C"
            }
            binding.executePendingBindings()
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        return OptionViewHolder(
            OptionItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val item = optionsList[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = optionsList.size
}