package com.example.submissiongithubuserapp.data.model.response

import com.google.gson.annotations.SerializedName

data class FollowResponseItem(

    @field:SerializedName("following_url")
    val followingUrl: String,

    @field:SerializedName("login")
    val username: String,

    @field:SerializedName("followers_url")
    val followersUrl: String,

    @field:SerializedName("avatar_url")
    val avatar: String,

    @field:SerializedName("html_url")
    val url: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("id")
    val id: Int
)