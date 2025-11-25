package com.kotla.anifloat.data.model

import com.google.gson.annotations.SerializedName

data class GitHubRelease(
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("body")
    val body: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("assets")
    val assets: List<GitHubAsset>,
    @SerializedName("prerelease")
    val prerelease: Boolean,
    @SerializedName("draft")
    val draft: Boolean
)

data class GitHubAsset(
    @SerializedName("name")
    val name: String,
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("content_type")
    val contentType: String
)
