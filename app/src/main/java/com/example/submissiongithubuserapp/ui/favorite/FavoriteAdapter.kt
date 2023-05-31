package com.example.submissiongithubuserapp.ui.favorite

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissiongithubuserapp.R
import com.example.submissiongithubuserapp.data.model.entity.UserEntity
import com.example.submissiongithubuserapp.databinding.ItemRowUserBinding
import com.example.submissiongithubuserapp.helper.UserDiffUtils
import com.example.submissiongithubuserapp.ui.detail.DetailActivity

class FavoriteAdapter :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private var userList = emptyList<UserEntity>()

    fun updateUserList(newList: List<UserEntity>) {
        val diff = DiffUtil.calculateDiff(UserDiffUtils(userList, newList))
        this.userList = newList

        diff.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemRowUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity) {
            binding.apply {
                tvItemUsername.text = user.username
                Glide.with(itemView.context)
                    .load(user.avatar)
                    .apply(
                        RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_base_loading)
                            .error(R.drawable.ic_baseline_error_24)
                    ).into(ivItemAvatar)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_USER, user.username)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val userBinding =
            ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(userBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size
}