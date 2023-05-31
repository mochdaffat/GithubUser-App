package com.example.submissiongithubuserapp.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissiongithubuserapp.R
import com.example.submissiongithubuserapp.databinding.ItemRowUserBinding
import com.example.submissiongithubuserapp.data.model.response.GithubUser
import com.example.submissiongithubuserapp.ui.detail.DetailActivity
import com.example.submissiongithubuserapp.ui.detail.DetailActivity.Companion.EXTRA_USER

class ListUserAdapter(private val listUser: List<GithubUser>) :
    RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    inner class ListViewHolder(private val userBinding: ItemRowUserBinding) :
        RecyclerView.ViewHolder(userBinding.root) {

        fun bind(user: GithubUser) {
            userBinding.apply {
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
                intent.putExtra(EXTRA_USER, user.username)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val userBinding =
            ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(userBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = listUser.size
}