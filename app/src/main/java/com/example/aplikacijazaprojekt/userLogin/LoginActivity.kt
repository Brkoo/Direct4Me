package com.example.aplikacijazaprojekt.userLogin

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.aplikacijazaprojekt.MainActivity
import com.example.aplikacijazaprojekt.R
import com.example.aplikacijazaprojekt.compression.CompressImageClass
import com.example.aplikacijazaprojekt.compression.CompressImageClass.compressImage
import com.example.aplikacijazaprojekt.compression.CompressionClass


import com.fasterxml.jackson.module.kotlin.*


import com.example.aplikacijazaprojekt.sharedPref.sharedPrefrences
import com.example.aplikacijazaprojekt.model.User
import com.example.aplikacijazaprojekt.utils.constans.Companion.BASE_URL
import com.example.aplikacijazaprojekt.utils.constans.Companion.BASE_URL_FACE_LOGIN
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.material.textfield.TextInputEditText
import com.squareup.moshi.Moshi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity() {
    private lateinit var imageBitmap: Bitmap
    var activity = this;
    var dialog: ProgressDialog? = null
    lateinit var imageUri: Uri
    var serverURL: String = "https://handyopinion.com/tutorials/UploadToServer.php"
    var serverUploadDirectoryPath: String = "https://handyopinion.com/tutorials/uploads/"
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imageFace: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonFaceLogin: ImageButton= findViewById(R.id.buttonFaceLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener{
            val intent = Intent(this, RegsterActivity::class.java)
                    startActivity(intent)
        }

        buttonLogin.setOnClickListener{
            getLogin("","")
        }

        buttonFaceLogin.setOnClickListener{
            dispatchTakePictureIntent()


        }
    }
    fun getLogin(username1: String?, password1: String?){
        val editTextEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val editTexPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val username = editTextEmail.text.toString()
        val password = editTexPassword.text.toString()
        Thread(Runnable {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()

            Log.d("Main",username)
            Log.d("Main", password)
            //val payload = """{"username" : ${email.toString()}, "password" : ${password.toString()}}"""
            /*val formBody: RequestBody = FormBody.Builder()
                .add("username", "b")
                .add("password", "b")

                .build()
*/
            //val json = "{\"username\":e,\"password\":\"e\"}"
            val jsonObject = JSONObject()
            if(username1 != ""){

                jsonObject.put("username", username1)
                jsonObject.put("password", password1)
                Log.d("Main","izvedlo se je username1")
            }
            else {

                jsonObject.put("username", username)
                jsonObject.put("password", password)
            }
            val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
            .url(BASE_URL)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    showToast("Wrong username or password")
                    throw IOException("Unexpected code $response")
                }


                val responseData = response.body!!.string()
                //Toast.makeText(this, response.code.toString(), Toast.LENGTH_SHORT).show()
                Log.d("Main", response.code.toString())
                Log.d("Main", responseData)
                //Log.d("Main", response.message)
                //val jsonObject = JSONObject(responseData)

                //val boxToken = jsonObject.getString("data")
                sharedPrefrences.setUserName(this, username)
                val intent2 = Intent(this, MainActivity::class.java)
                startActivity(intent2)
            }
        }catch (e:Exception){
            //Toast.makeText(this, "Unable to login", Toast.LENGTH_SHORT).show()
            showToast("Unable to login try again or check your connection")
                Log.d("Main", "Problem at logging in: $e")
        }

    }).start()

    }

    var currentPhotoPath : String = ""
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {

            currentPhotoPath = absolutePath
            Log.d("Main", "Pot: " + currentPhotoPath)
            galleryAddPic()
        }
    }
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }



    //zajem slike
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    showToast("Error occurred while creating file")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    Log.d("Main", "pot pri tistemu: " + photoURI.toString())
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }



    //intent vrne sliko polovimo z onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //imageUri = intent.getData()!!
            //moram ven pobrat uri od slike:
            //val bitmap: Bitmap
            try {

                //val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    //tukaj pridobimo bitmap zajete slike
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)

                //klicemo metodo ki bo kompresirala sliko in jo shranila nazaj
                var byteArray = CompressionClass.compressImage(bitmap);
                //NOW WE call saveByteArray to this device
                saveByteArrayToBatFile(byteArray, "C:\\FERI 5.semester\\RAC VECPREDSTAVNOST\\Slika.bin");
                //uploadFile(currentPhotoPath)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            } catch (e: IOException) {
                e.printStackTrace();
            }

        }
            //imageUri = data?.extras?.get(MediaStore.EXTRA_OUTPUT) as Uri
            //Log.d("Main", "Uri od slike je: " + imageUri)

            //imageBitmap = data?.extras?.get("data") as Bitmap

            //val faceLogin = faceLogin()
            //faceLogin.faceLogin(imageBitmap, this)
        }
    fun saveByteArrayToBatFile(byteArray: ByteArray, filePath: String) {

        val dir = File(this.activity.getExternalFilesDir(null), "bin")
        if (!dir.exists()) {
            dir.mkdir()
        }

        val file = File(dir, "Slika.bin")
        val fos = FileOutputStream(file)
        fos.write(byteArray)
        fos.flush()
        fos.close()
       // val path = Paths.get(filePath)
        //Files.write(path, byteArray)
        println("File Path, File saved at: ${file.absolutePath}")
    }


    fun uploadFile(sourceFilePath: String, uploadedFileName: String? = null) {
        uploadFile(File(sourceFilePath), uploadedFileName)
    }



    fun uploadFile(sourceFile: File, uploadedFileName: String? = null) {
        Thread {
            val client = OkHttpClient()
            val mimeType = getMimeType(sourceFile);
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            val fileName: String = if (uploadedFileName == null)  sourceFile.name else uploadedFileName
            toggleProgressDialog(true)
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", fileName,sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .build()

                val request: Request = Request.Builder()
                    .url(BASE_URL_FACE_LOGIN)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    Log.d("Main", response.message)
                    if (response.isSuccessful) { //dobimo response succesful

                        Log.d("File upload","success, path: $serverUploadDirectoryPath$fileName")
                        val moshi = Moshi.Builder().build()
                        val gistJsonAdapter = moshi.adapter(User::class.java)
                        val user = gistJsonAdapter.fromJson(response.body!!.source())
                        //val mapper = jacksonObjectMapper()
                        //val person: User = mapper.readValue<User>(response.body!!)
                        if (user != null) {

                            showToast("Welcome " + user.username.toString())
                            sharedPrefrences.setUserName(this, user.username)
                            val intent2 = Intent(this, MainActivity::class.java)
                            startActivity(intent2)
                        }
                        //showToast("Dela")
                    } else {
                        Log.e("File upload", "failed")
                        showToast("File uploading failed")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false)
        }.start()
    }

/*
    fun uploadByteArray(byteArray: ByteArray, uploadedFileName: String? = null) {
        Thread {
            val client = OkHttpClient()
            val mimeType = getMimeType(byteArray);
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            val fileName: String = if (uploadedFileName == null)  sourceFile.name else uploadedFileName
            toggleProgressDialog(true)
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", fileName,sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .build()

                val request: Request = Request.Builder()
                    .url(BASE_URL_FACE_LOGIN)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    Log.d("Main", response.message)
                    if (response.isSuccessful) { //dobimo response succesful

                        Log.d("File upload","success, path: $serverUploadDirectoryPath$fileName")
                        val moshi = Moshi.Builder().build()
                        val gistJsonAdapter = moshi.adapter(User::class.java)
                        val user = gistJsonAdapter.fromJson(response.body!!.source())
                        //val mapper = jacksonObjectMapper()
                        //val person: User = mapper.readValue<User>(response.body!!)
                        if (user != null) {

                            showToast("Welcome " + user.username.toString())
                            sharedPrefrences.setUserName(this, user.username)
                            val intent2 = Intent(this, MainActivity::class.java)
                            startActivity(intent2)
                        }
                        //showToast("Dela")
                    } else {
                        Log.e("File upload", "failed")
                        showToast("File uploading failed")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false)
        }.start()
    }

*/






    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(toast: String?) {
        runOnUiThread {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        }
    }
    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                dialog?.dismiss();
            }
        }
    }
}