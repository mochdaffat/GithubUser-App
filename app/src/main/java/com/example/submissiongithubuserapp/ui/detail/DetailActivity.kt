package com.example.submissiongithubuserapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissiongithubuserapp.R
import com.example.submissiongithubuserapp.data.model.entity.UserEntity
import com.example.submissiongithubuserapp.databinding.ActivityDetailBinding
import com.example.submissiongithubuserapp.helper.ViewModelFactory
import com.example.submissiongithubuserapp.ui.favorite.FavoriteViewModel
import com.example.submissiongithubuserapp.ui.follow.FollowFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var factory: ViewModelFactory
    private val favoriteViewModel: FavoriteViewModel by viewModels { factory }
    private var username: String? = null
    private var url: String? = null
    private var avatar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            setDisplayHomeAsUpEnabled(true)
        }

        val username = intent.getStringExtra(EXTRA_USER)
        detailViewModel.getDetailUser(username)

        detailViewModel.userData.observe(this) { userData ->
            detailBinding.apply {
                tvUsername.text = userData.username
                tvName.text = userData?.name ?: "-"
                tvLocation.text = userData?.location ?: "-"
                tvCompany.text = userData?.company ?: "-"

                val shortFollowers = userData.followers
                if (shortFollowers > 10000) {
                    "${shortFollowers / 1000}.${(shortFollowers % 1000) / 100}K".also {
                        tvFollowers.text = it
                    }
                } else {
                    tvFollowers.text = userData.followers.toString()
                }

                val shortFollowing = userData.following
                if (shortFollowing > 10000) {
                    "${shortFollowing / 1000}.${(shortFollowing % 1000) / 100}K".also {
                        tvFollowing.text = it
                    }
                } else {
                    tvFollowing.text = userData.following.toString()
                }

                val shortRepository = userData.repository
                if (shortRepository > 10000) {
                    "${shortRepository / 1000}.${(shortRepository % 1000) / 100}K".also {
                        tvRepository.text = it
                    }
                } else {
                    tvRepository.text = userData.repository.toString()
                }

                Glide.with(this@DetailActivity)
                    .load(userData.avatar)
                    .apply(
                        RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_base_loading)
                            .error(R.drawable.ic_baseline_error_24)
                    )
                    .into(ivAvatar)

                val fragment = mutableListOf<Fragment>(
                    FollowFragment.newInstance(FollowFragment.FOLLOWING),
                    FollowFragment.newInstance(FollowFragment.FOLLOWERS)
                )

                val fragmentTitle = mutableListOf(
                    getString(R.string.following),
                    getString(R.string.followers)
                )

                val detailAdapter = DetailAdapter(this@DetailActivity, fragment)
                viewPager.adapter = detailAdapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = fragmentTitle[position]
                }.attach()

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab?.position == FollowFragment.FOLLOWERS) {
                            detailViewModel.getFollowers(userData.username)
                        } else {
                            detailViewModel.getFollowing(userData.username)
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
                detailViewModel.getFollowing(userData.username)
            }
            this.username = userData.username.toString()
            this.url = userData.url.toString()
            this.avatar = userData.avatar.toString()
        }

        detailViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        factory = ViewModelFactory.getInstance(this)

        favoriteViewModel.getFavoritedUser().observe(this) { favList ->
            val isFavorited = favList.any {
                it.username == username
            }
            setIconFavorite(isFavorited)

            detailBinding.fabFav.setOnClickListener {
                val entity = username?.let { UserEntity(it, avatar, false) }

                try {
                    if (entity != null) favoriteViewModel.saveDeleteUser(entity, favList.any {
                        it.username == username
                    })
                } catch (e: Exception) {
                    Toast.makeText(
                        this, e.toString(), Toast.LENGTH_SHORT
                    ).show()
                }

                if (isFavorited) {
                    Toast.makeText(
                        this, "Remove $username as favorite", Toast.LENGTH_SHORT
                    ).show()
                    setIconFavorite(isFavorited)
                } else {
                    Toast.makeText(
                        this, "Mark $username as favorite", Toast.LENGTH_SHORT
                    ).show()
                    setIconFavorite(isFavorited)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        detailBinding.pbDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setIconFavorite(isFavorited: Boolean) {
        detailBinding.fabFav.apply {
            if (isFavorited) {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.ic_baseline_favorite_24
                    )
                )
            } else {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.ic_baseline_favorite_border_24
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.shareButton -> {
                val intent = Intent(Intent.ACTION_SEND)
                val shareUser = "Let's Connect $username on github! \n$url"
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, shareUser)
                startActivity(Intent.createChooser(intent, "Share info with..."))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}