package com.example.aplikacijazaprojekt.userLogin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log

import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.aplikacijazaprojekt.MainActivity
import com.example.aplikacijazaprojekt.utils.constans
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class faceLogin() {




    fun faceLogin(image: Bitmap, context: Context){

        Thread(Runnable {
            val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
            val client = OkHttpClient()


            //val payload = """{"username" : ${email.toString()}, "password" : ${password.toString()}}"""
            /*val formBody: RequestBody = FormBody.Builder()
                .add("username", "b")
                .add("password", "b")

                .build()
*/
            //val json = "{\"username\":e,\"password\":\"e\"}"

            val jsonObject = JSONObject()
            //jsonObject.put("username", username)
            //jsonObject.put("password", password)

            val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(constans.BASE_URL)
                .post(body)
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful){
                        //showToast("Wrong username or password")
                        throw IOException("Unexpected code $response")
                    }


                    val responseData = response.body!!.string()
                    //Toast.makeText(this, response.code.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("Main", response.code.toString())
                    Log.d("Main", responseData)
                    //Log.d("Main", response.message)
                    //val jsonObject = JSONObject(responseData)

                    //val boxToken = jsonObject.getString("data")
                    //sharedPrefrences.setUserName(this, username)
                    val intent2 = Intent(context, MainActivity::class.java)
                    context.startActivity(intent2)
                }
            }catch (e:Exception){
                //Toast.makeText(this, "Unable to login", Toast.LENGTH_SHORT).show()
                Log.d("Main", "Problem at logging in: $e")
            }

        }).start()
    }
}