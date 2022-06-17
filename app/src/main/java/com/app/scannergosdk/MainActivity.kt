package com.app.scannergosdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnGeneratePdf : Button
    private lateinit var btnSelectImages : Button
    private lateinit var editTextPassword : EditText
    private lateinit var listOfFiles : MutableList<String>
    private val PICK_IMAGE = 10001
    val PERMISSION_EXTERNAL = 0x111111


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PDFBoxResourceLoader.init(applicationContext)
        if (PermissionUtils.isPermission(PERMISSION_EXTERNAL, this)) {
            Log.d("Permission","Granted")
            Utils.copy(
                this.assets.open("watermark.pdf"),
                FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "watermark.pdf"))
            )
        }
        btnGeneratePdf = findViewById(R.id.generatePdf)
        btnSelectImages = findViewById(R.id.selectImages)
        editTextPassword = findViewById(R.id.edit_text_enter_password)
        btnGeneratePdf.setOnClickListener(this)
        btnSelectImages.setOnClickListener(this)
        listOfFiles = ArrayList()




    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
            val intent = result.data
            if (intent != null) {
                val uri = intent.data
                val filePath = SelectedFilePath.getPath(this,uri)
                if(filePath.isNotEmpty()){
                    listOfFiles.add(filePath)
                }
            }
    }

    override fun onClick(p0: View?) {
        if(p0 == this.btnSelectImages){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            setResult(PICK_IMAGE,intent)
            startForResult.launch(Intent.createChooser(intent,"Select Picture"))
        }
        else if(p0 == this.btnGeneratePdf){
            val password = editTextPassword.text.toString()
            val imageToPdfHelper = ImageToPdfHelper(this,listOfFiles,password)
            imageToPdfHelper.convertImageToPdf()
            listOfFiles.clear()
            val  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            Toast.makeText(this,"Saved PDF at: $path",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_EXTERNAL) {
            if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    PermissionUtils.isPermission(PERMISSION_EXTERNAL, this)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PERMISSION_EXTERNAL){
            Utils.copy(
                this.assets.open("watermark.pdf"),
                FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "watermark.pdf"))
            )
        }
    }
}