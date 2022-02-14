package com.tulasimultispecialityhospital

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.tulasimultispecialityhospital.adapters.AddReportsAdapter
import com.tulasimultispecialityhospital.models.ReportsModel
import com.tulasimultispecialityhospital.util.GridItemDecoration
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlinx.android.synthetic.main.activity_add_report_pages.*
import kotlinx.android.synthetic.main.activity_add_report_pages.toolbar
import kotlinx.android.synthetic.main.dialog_add_photo.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddReportsActivity : AppCompatActivity() {

    var addReportsAdapter: AddReportsAdapter? = null
    var addReportsRecycleView: RecyclerView? = null
    var reportsArrayList: ArrayList<ReportsModel>? = null
    private var myDoctorUtility: MyDoctorUtility? = null
    var PERMISSIONS_REQUEST_CAMERA = 5
    var PERMISSIONS_REQUEST_GALLERY = 6
    var REQUEST_TAKE_PHOTO = 500
    var REQUEST_GALLERY = 600
    var isImageFromCamera = false
    var currentPhotoPath: String = ""
    var currentPhotoBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report_pages)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        myDoctorUtility = MyDoctorUtility.getInstance(this)
        addReportsRecycleView = findViewById<RecyclerView>(R.id.addReportsRecycleView)
        addReportsRecycleView!!.layoutManager = GridLayoutManager(
            this,
            3
        ) as RecyclerView.LayoutManager?
        addReportsRecycleView!!.addItemDecoration(GridItemDecoration(3, 3))
        reportsArrayList = ArrayList()
        addReportsAdapter = AddReportsAdapter(reportsArrayList!!, this as Context)
        addReportsRecycleView!!.adapter = addReportsAdapter

        reportsArrayList!!.add(ReportsModel())
        addReportsAdapter!!.notifyDataSetChanged()
        uploadReportsButton.setOnClickListener {
            when {
                reportsArrayList!!.size < 2 -> {
                    Toast.makeText(this, "No reports found", Toast.LENGTH_LONG).show()
                }
                reportsArrayList!!.size > 2 -> {
                    myDoctorUtility!!.showProgressDialog(this)
                    createPdf(reportsArrayList)
                    myDoctorUtility!!.dismissProgressDialog(this)
                    Toast.makeText(this, "Reports uploaded successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Reports uploaded successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

    }

    fun addReportImages() {
        addReportImageDialog()
    }

    private fun createPdf(reportsArrayList: ArrayList<ReportsModel>?) {

        var pdfDocument: PdfDocument = PdfDocument()
        for ((index, medicineRemainderModel: ReportsModel) in reportsArrayList!!.withIndex()) {
            if (index < (reportsArrayList.size - 1)) {
                var options: BitmapFactory.Options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                var bitmap: Bitmap? = null
                try {
                    bitmap = BitmapFactory.decodeFile(reportsArrayList!![index].reportLocalPath, options)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var pageInfo: PdfDocument.PageInfo =
                    PdfDocument.PageInfo.Builder(options.outWidth / 2, options.outHeight / 2, index + 1).create()
                val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                drawCanvasFromBitmap(canvas, reportsArrayList!![index].reportLocalPath)
                pdfDocument.finishPage(page)

                if (!TextUtils.isEmpty(medicineRemainderModel.reportLocalPath)) {
                    var file = File(medicineRemainderModel.reportLocalPath)
                    if (file.exists()) {
                        file.delete()
                        if (file.exists()) {
                            file.canonicalFile.delete()
                            if (file.exists()) {
                                applicationContext.deleteFile(file.name)
                            }
                        }
                    }
                }
            }
        }
        val targetPdf: String = myDoctorUtility!!.getFilesPath(this) + System.currentTimeMillis() + ".pdf"
        val filePath = File(targetPdf)
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            var reader = PdfReader(FileInputStream(filePath))
            var writer = PdfStamper(reader, FileOutputStream(filePath))
            writer.setFullCompression()
            writer.close()

        } catch (e: IOException) {
            Log.e("main", "error $e")
            //   Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }
        // close the document
        pdfDocument.close()
    }

    private fun checkCameraPermissions(caller: Activity): Boolean {
        var permissionCheck = ContextCompat.checkSelfPermission(
            caller,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(
                caller,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                permissionCheck = ContextCompat.checkSelfPermission(
                    caller,
                    Manifest.permission.CAMERA
                )
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    return true
                }

            }
        }

        return false
    }

    private fun checkGalleryPermissions(caller: Activity): Boolean {
        var permissionCheck = ContextCompat.checkSelfPermission(
            caller,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(
                caller,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }

        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(currentPhotoPath)) {
                isImageFromCamera = true
                CompressSelectedImage(this).execute(currentPhotoPath)
            }

        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            currentPhotoPath = getRealPathFromURI(data?.data!!)
            if (!TextUtils.isEmpty(currentPhotoPath)) {
                if (File(currentPhotoPath).exists()) {
                    isImageFromCamera=false
                    CompressSelectedImage(this).execute(File(currentPhotoPath).absolutePath)
                }
            }

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (checkCameraPermissions(this)) {
                    takePictureIntent()
                } else {
                    Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show()
                }
                return
            }
            PERMISSIONS_REQUEST_GALLERY -> {
                // If request is cancelled, the result arrays are empty.
                if (checkGalleryPermissions(this)) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun addReportImageDialog() {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_add_photo)

            dialog.dialog_camera_image.setOnClickListener {
                dialog.dismiss()
                if (checkCameraPermissions(this)) {
                    currentPhotoPath = ""
                    takePictureIntent()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PERMISSIONS_REQUEST_CAMERA
                    )
                }

            }
            dialog.dialog_gallery_image.setOnClickListener {
                dialog.dismiss()
                if (checkGalleryPermissions(this)) {
                    currentPhotoPath = ""
                    pickImageFromGallery()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PERMISSIONS_REQUEST_GALLERY
                    )
                }

            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun popAlert(msg: String?) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_no_internet)
            val titleTextView = dialog.findViewById(R.id.titleTextView) as AppCompatTextView
            val agePositiveButton = dialog.findViewById<AppCompatButton>(R.id.agePositiveButton)
            titleTextView.text = msg
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorErrorRed))
            agePositiveButton.setText("Okay")
            agePositiveButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.tulasimultispecialityhospital.fileProvider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    class CompressSelectedImage(private var activity: AddReportsActivity) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {

            return activity.compressImage(params[0])
        }


        override fun onPreExecute() {
            super.onPreExecute()
            activity.myDoctorUtility!!.showProgressDialog(activity)

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(activity.isImageFromCamera){
                var file = File(activity.currentPhotoPath)
                if (file.exists()) {
                    file.delete()
                    if (file.exists()) {
                        file.canonicalFile.delete()
                        if (file.exists()) {
                            activity.applicationContext.deleteFile(file.name)
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(result)) {
                activity.addImageFileToList(result)
            } else {
                activity.popAlert("Error while reading image,Try again")
            }
        }
    }

    private fun addImageFileToList(result: String?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        var reportsModel = ReportsModel(
            reportLocalPath = result,
            imageBitmap = currentPhotoBitmap
        )
        reportsArrayList!!.add(reportsArrayList!!.size - 1, reportsModel)
        addReportsAdapter!!.notifyDataSetChanged()
    }


    private fun getRealPathFromURI(contentUri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            return cursor.getString(index)
        }
    }

    private fun compressImage(filePath: String?): String {
        var scaledBitmap: Bitmap? = null
        var options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var actualHeight: Int = options.outHeight
        var actualWidth: Int = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        var maxHeight: Float = 800.0f
        var maxWidth: Float = 460.0f
        var imgRatio: Float = actualWidth / actualHeight.toFloat()
        var maxRatio: Float = maxWidth / maxHeight.toFloat()
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight.toFloat()
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (actualHeight * imgRatio).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix: Matrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        var canvas: Canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap!!.getWidth() / 2,
            middleY - bitmap.getHeight() / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        var exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)

            var orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            var matrix: Matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90.toFloat())
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180.toFloat())
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270.toFloat())
                Log.d("EXIF", "Exif: $orientation")
            }
            if (scaledBitmap != null) {
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val resultFilePath: String = myDoctorUtility!!.getFilename(this)
        try {
            out = FileOutputStream(resultFilePath)

//          write the compressed bitmap at the destination specified by filename.
            if (scaledBitmap != null) {
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
            }

            currentPhotoBitmap = scaledBitmap
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return resultFilePath


    }


    private fun drawCanvasFromBitmap(canvas: Canvas, filePath: String?): Canvas {

        var options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var actualHeight: Int = options.outHeight / 2
        var actualWidth: Int = options.outWidth / 2

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix: Matrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        canvas.matrix = scaleMatrix
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap!!.getWidth() / 2,
            middleY - bitmap.getHeight() / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        return canvas


    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        val totalPixels: Float = width * height.toFloat()
        val totalReqPixelsCap: Float = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    override fun onBackPressed() {
        super.onBackPressed()
        deleteCatchFiles()
    }

    override fun onDestroy() {
        super.onDestroy()
        // deleteCatchFiles()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun deleteCatchFiles() {
        for ((index, medicineRemainderModel: ReportsModel) in reportsArrayList!!.withIndex()) {
            if (!TextUtils.isEmpty(medicineRemainderModel.reportLocalPath)) {
                var file = File(medicineRemainderModel.reportLocalPath)
                if (file.exists()) {
                    file.delete()
                    if (file.exists()) {
                        file.canonicalFile.delete()
                        if (file.exists()) {
                            applicationContext.deleteFile(file.name)
                        }
                    }
                }
            }
        }
    }
}