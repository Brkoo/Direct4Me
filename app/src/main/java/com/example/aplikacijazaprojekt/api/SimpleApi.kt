package com.example.aplikacijazaprojekt.api

import com.example.aplikacijazaprojekt.model.Post
import com.example.aplikacijazaprojekt.utils.constans.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SimpleApi {
    //@Headers("Authorization " + API_KEY)
    @POST("Access/openbox")
    suspend fun pushPost(
    @Body post: Post): Response<Post>

/*
    @FormUrlEncoded
    @POST("user/login")
    fun userLogin(
        @Field("email"): email:String,
        @Field("password"): paswword:String
    )
*/

}