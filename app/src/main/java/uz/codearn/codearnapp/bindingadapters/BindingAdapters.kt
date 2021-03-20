package uz.codearn.codearnapp.bindingadapters

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.constants.Constants
import uz.codearn.codearnapp.model.CodingPath
import uz.codearn.codearnapp.model.User
import uz.codearn.codearnapp.ui.main.home.CodingPathsAdapter
import uz.codearn.codearnapp.ui.main.leaderboard.TopUsersAdapter

@BindingAdapter("android:codingPathlistData")
fun bindCodingPathListData(recyclerView: RecyclerView, listData: List<CodingPath>?) {
    val adapter = recyclerView.adapter as CodingPathsAdapter
    adapter.submitList(listData)
}

@BindingAdapter("android:userslistData")
fun bindTopUsersListData(recyclerView: RecyclerView, listData: List<User>?) {
    val adapter = recyclerView.adapter as TopUsersAdapter
    adapter.submitList(listData)
}

@BindingAdapter("imgUrl")
fun setCodingPathImage(imageView: ImageView,url:String?){
    url?.let {
        val uri = url.toUri().buildUpon().scheme("https").build()
        Picasso.get().load(uri).placeholder(R.drawable.ic_coding_placeholder).error(R.drawable.ic_coding_placeholder).into(imageView)
    }
}

@BindingAdapter("android:profilePhotoIndex")
fun setUserProfilePic(imageView: ImageView,profileIndex:Int?){
    profileIndex?.let {
        imageView.setImageResource(Constants.getAllProfilePics()[profileIndex])
    }
}

@BindingAdapter("android:userLevel")
fun setUserLevel(textView: TextView,score:Int?){
    score?.let {
        textView.text = when(score){
            in 0..400 -> "Yangi\no'rganuvchi"
            in 401..700 -> "O'rganuvchi"
            else -> "Master"
        }
    }
}