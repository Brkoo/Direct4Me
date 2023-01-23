package com.example.aplikacijazaprojekt

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikacijazaprojekt.TSP.MapsActivity
import com.example.aplikacijazaprojekt.sharedPref.sharedPrefrences
import com.example.aplikacijazaprojekt.userLogin.LoginActivity
import com.example.aplikacijazaprojekt.utils.constans
import com.example.aplikacijazaprojekt.utils.constans.Companion.API_KEY
import com.example.aplikacijazaprojekt.utils.constans.Companion.BASE_URL_GET_PREMMISION
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {
    var responseCodeCheck: Int = 0
    lateinit var buttonScan: Button
    lateinit var buttonMaps: Button
    lateinit var responseCode: String
    lateinit var buttonPlayToken: Button
    lateinit var textViewAddress: TextView
    lateinit var fileTokenName: String
    lateinit var textViewName: TextView
    lateinit var buttonLogout: Button
    lateinit var qrScan: IntentIntegrator

    //val buttonPlayToken = findViewById<Button>(R.id.buttonPlayToken)
    var boxID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(false) {


            /*
        if (sharedPrefrences.getUserName(this)?.length == 0) {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

    */
        } else {

            setContentView(R.layout.activity_main)
            buttonScan = findViewById<Button>(R.id.buttonScan)
            buttonPlayToken = findViewById<Button>(R.id.buttonPlayToken)
            buttonPlayToken.visibility = View.GONE
            buttonLogout = findViewById<Button>(R.id.ButtonLogout)
            buttonMaps = findViewById<Button>(R.id.mapsButton)
            qrScan = IntentIntegrator(this)
            qrScan.setOrientationLocked(false)


            /*
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val myPost = Post(512, 2)
        val MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        val payload = """{"boxId": 352, "tokenFormat" : 2}"""
        viewModel.pushPost(myPost)
        viewModel.myResponse.observe(this, Observer { response ->
            if(response.isSuccessful){
                Log.d("Main", response.body().toString())
                Log.d("Main", response.code().toString())
                Log.d("Main", response.message())
            }else{
                Toast.makeText(this, response.code().toString(), Toast.LENGTH_SHORT).show()
                Log.d("Main", response.code().toString())
            }
        })

*/






            buttonScan.setOnClickListener {
                qrScan.initiateScan()

            }

            buttonPlayToken.setOnClickListener {

                playToken()
            }
            buttonLogout.setOnClickListener {
                sharedPrefrences.clearUserName(this)
                val intent2 = Intent(this, LoginActivity::class.java)
                startActivity(intent2)
            }
            buttonMaps.setOnClickListener {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun checkIfMine() {

        val exec = Executors.newFixedThreadPool(2)
        var responseNekaj: String = ""

        Thread(Runnable {
            val uri = Uri.parse(BASE_URL_GET_PREMMISION)
                .buildUpon()
                .appendQueryParameter("iden",boxID)
                //.appendQueryParameter("uporabnik", sharedPrefrences.getUserName(this))
                .appendQueryParameter("uporabnik", sharedPrefrences.getUserName(this))
                //.appendPath(path)
                .build()



            val client = OkHttpClient()

            val request = Request.Builder()
                .url(uri.toString())
                .get()
                .build()

            try {
                client.newCall(request).execute().use { response ->

                    Log.d("Main", "Response koda tam pa tam: ${response.code}")
                    if (!response.isSuccessful) {

                        responseCodeCheck = response.code
                        showToast("Error not yours")
                        Log.d("Main", "To se izvede, koda: $responseCodeCheck")
                        throw IOException("Unexpected code $response")
                    }



                    val responseData = response.body!!.string()
                    Log.d("Main","Koda: " + responseData)
                    //Toast.makeText(this, response.code.toString(), Toast.LENGTH_SHORT).show()
                    //responseCodeCheck = response.code
                    Log.d("Main","response pri checku je:" + response.code.toString())
                    //showToast("response code: ${response.code.toString()}")
                    Log.d("Main", "Nekaj nekaj samo response: $response" )
                    responseNekaj = response.code.toString()
                    Log.d("Main", "Response ki je not vstavljen = : $responseNekaj")
                    //Popravi doma
                    getToken()
                }
                //responseNekaj = response.code.toString()
            } catch (e: Exception) {

                Log.d("Main", "Error at connecting: $e")
            }

        }).start()
        Log.d("Main", "koda pred returnom :  $responseCodeCheck")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            //if qrcode has nothing in it
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()
            } else {
                //if qr contains data
                try {

                    //converting the data to json
                    //val obj = JSONObject(result.contents)
                    //setting values to textviews
                    Log.d("Main", result.contents.toString())
                    // Regex to match "ll" in a string
                    if(result.contents.length > 3){
                    val pattern1 = Regex("(?<=\\/)(.*?)(?=\\/)")
                    val boxID2 : MatchResult? = pattern1.find(result.contents.toString(), 0)

                        boxID = boxID2?.value.toString()
                        boxID = boxID.replace("000", "")
                    //println(boxID ?.value)
                    showToast("Id paketnika je: " + boxID)

                    }
                    else {
                        boxID = result.contents.toString()
                        showToast("Id paketnika je: $boxID")
                    }


                    //textViewName.text = boxID.toString()
                    checkIfMine()
                    /*Log.d("Main", "Check if mine koda: $responseCodeCheck")
                    if (responseCodeCheck == 200)
                    {
                        getToken()

                    }else{
                        showToast("Error")

                    }

                     */
                    //textViewAddress.setText(obj.getString("address"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun writeBytesAsZip(bytes: ByteArray): String {
        //POPRAVI PATHNAME
        val path = File("/data/data/com.example.aplikacijazaprojekt/token")
        if (!path.isDirectory()) {
            path.mkdir()
        }
        var file = File.createTempFile("token", ".zip", path)
        var os = FileOutputStream(file)
        os.write(bytes)
        os.close()
        return file.name
    }

    fun getToken() {

        Thread(Runnable {

            val strPattern = "^0+"

            boxID.trimStart()
            val client = OkHttpClient()
            val token = API_KEY
            val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

            val payload = """{"boxId" : $boxID, "tokenFormat" : 2}"""
            val request = Request.Builder()
                .url("https://api-ms-stage.direct4.me/sandbox/v1/Access/openbox")
                .addHeader("Authorization", "Bearer " + token)
                .post(payload.toRequestBody(MEDIA_TYPE_JSON))
                .build()
            /*
            var responses: Response? = null
            try {
                responses = client.newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Main", responses!!.code.toString())
            val jsonData = responses!!.body!!.string()
            val Jobject = JSONObject(jsonData)
            val Jarray = Jobject.getJSONArray("data")

            for (i in 0 until Jarray.length()) {
                val `object` = Jarray.getJSONObject(i)
            }*/
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body!!.string()
                    //Toast.makeText(this, response.code.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("Main", response.code.toString())
                    //Log.d("Main", response.message)
                    val jsonObject = JSONObject(responseData)
                    val boxToken = jsonObject.getString("data")


                    val boxTokenBytes: ByteArray = Base64.decode(boxToken, Base64.DEFAULT)
                    var fileNameZip = writeBytesAsZip(boxTokenBytes)
                    fileTokenName = unzip(
                        "data/data/com.example.aplikacijazaprojekt/token/" + fileNameZip,
                        "/data/data/com.example.aplikacijazaprojekt/token/wav"
                    )
                    Log.d("Main", fileTokenName.toString())


                }
            } catch (e: Exception) {
                Log.d("Main", "Error at connecting: $e")

            }

        }).start()
        Log.d("Main", "to se ni zezelo izvest")

        runOnUiThread {
            buttonPlayToken.visibility = View.VISIBLE
        }


    }

    fun unzip(zipFileName: String?, targetLocation: String?): String {
        val f = File(targetLocation)
        if (!f.isDirectory()) {
            f.mkdir()
        }
        try {
            val fin = FileInputStream(zipFileName)
            val zin = ZipInputStream(fin)
            var ze: ZipEntry? = null
            val path = File("/data/data/com.example.aplikacijazaprojekt/token")
            val file = File.createTempFile("token", ".wav", path)

            while (zin.nextEntry.also { ze = it } != null) {
                val fout = FileOutputStream(file)
                var c = zin.read()
                while (c != -1) {
                    fout.write(c)
                    c = zin.read()
                }
                zin.closeEntry()
                fout.close()
            }
            zin.close()
            return file.name
        } catch (e: Exception) {
            Log.d("Main", "Error" + e)
        }
        return ""

    }

    fun playToken() {
        try {

            val myUri: Uri =
                Uri.parse("/data/data/com.example.aplikacijazaprojekt/token/$fileTokenName") // initialize Uri here
            val mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(applicationContext, myUri)
                prepare()
                start()
            }
            sendOpenPostRequest()
        } catch (e: Exception) {
            Log.d("Main", "Error at playing token: $e")
        }
        /*
        val filePath: String = Environment.getExternalStorageDirectory()+"/yourfolderNAme/yopurfile.mp3";
        val mediaPlayer =  MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.start()

         */
    }

    fun sendOpenPostRequest() {
        val username = sharedPrefrences.getUserName(this).toString()
        Thread(Runnable {
            val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
            val client = OkHttpClient()
            val jsonObject = JSONObject()
            jsonObject.put("username", sharedPrefrences.getUserName(this).toString())
            Log.d("main", "username je: " + username)
            Log.d("Main", "BoxId je = " + boxID)
            jsonObject.put("paketnikId", boxID)


            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(constans.BASE_URL_OPEN)
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val responseData = response.body!!.string()

                    Log.d("Main", response.code.toString())
                    Log.d("Main", responseData)


                }
            } catch (e: Exception) {
                showToast("Napaka pri odklepu")
                Log.d("Main", "Error with registration $e")
            }

        }).start()
    }
    fun showToast(toast: String?) {
        runOnUiThread {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        }
    }
}




