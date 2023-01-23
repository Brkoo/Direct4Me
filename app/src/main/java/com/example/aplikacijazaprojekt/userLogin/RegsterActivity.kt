package com.example.aplikacijazaprojekt.userLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.aplikacijazaprojekt.R
import com.example.aplikacijazaprojekt.utils.constans
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class RegsterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regster)
        val buttonResgiter = findViewById<Button>(R.id.buttonRegister)
        buttonResgiter.setOnClickListener {
            Register()
        }
        val bacButton = findViewById<ImageButton>(R.id.backButton)
        bacButton.setOnClickListener {
            val intent2 = Intent(this, LoginActivity::class.java)
            startActivity(intent2)
        }
    }
    fun Register() {
        val editTextEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val editTexPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val editTextName = findViewById<TextInputEditText>(R.id.editTextName)
        val editTextSurname = findViewById<TextInputEditText>(R.id.editTextName)
        val editTextPhone = findViewById<TextInputEditText>(R.id.editTextName)
        val editTexPassword2 = findViewById<TextInputEditText>(R.id.editTextPassword2)
        val editTextUsername = findViewById<TextInputEditText>(R.id.editTextUsername)



        if (editTexPassword.text != editTexPassword2.text && editTexPassword.text.isNullOrEmpty() && editTexPassword2.text.isNullOrEmpty()) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            editTexPassword.text.contentEquals("")
            editTexPassword2.text.contentEquals("")
            editTexPassword.requestFocus()
            return
        }

        Thread(Runnable {
            val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
            val client = OkHttpClient()
            val email = editTextEmail.text.toString()
            val password = editTexPassword.text.toString()
            val name = editTextName.text.toString()
            val phone = editTextPhone.text.toString()
            val surname = editTextSurname.text.toString()
            val username = editTextUsername.text.toString()
            Log.d("Main", email)
            Log.d("Main", password)
            //val payload = """{"username" : ${email.toString()}, "password" : ${password.toString()}}"""
            /*val formBody: RequestBody = FormBody.Builder()
                .add("username", "b")
                .add("password", "b")

                .build()
*/
            //val json = "{\"username\":e,\"password\":\"e\"}"

            val jsonObject = JSONObject()
            jsonObject.put("name", name)
            jsonObject.put("surname", surname)
            jsonObject.put("email", email)
            jsonObject.put("username", username)
            jsonObject.put("password", password)
            jsonObject.put("phoneNumber", phone)

            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(constans.BASE_URL_REGISTER)
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val responseData = response.body!!.string()
                    //Toast.makeText(this, response.code.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("Main", response.code.toString())
                    Log.d("Main", responseData)
                    //Log.d("Main", response.message)
                    //val jsonObject = JSONObject(responseData)

                    //val boxToken = jsonObject.getString("data")
                    val intent2 = Intent(this, LoginActivity::class.java)
                    startActivity(intent2)
                }
            } catch (e: Exception){
                Toast.makeText(this, "Unable to register", Toast.LENGTH_SHORT).show()
                Log.d("Main", "Error with registration $e")
            }

        }).start()
    }
}