package com.collagemaker_makeanything.StoreFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.Adapter.Sticker_Sub_Image_Adapter
import com.collagemaker_makeanything.Class.ImageSizeFetcher
import com.collagemaker_makeanything.R

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class StickerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imgStickers: ImageView
    private lateinit var txtName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtFreeDownload: TextView
    private lateinit var btnFreeDownload: CardView
    private lateinit var txtNumber: TextView
    private lateinit var adapter: Sticker_Sub_Image_Adapter
    private var data: List<String?>? = null
    private val imageSizeFetcher = ImageSizeFetcher()
    private lateinit var datas: String
    private lateinit var mainImageUrl: String
    var textCategory: TextView? = null
    private val downloadedFiles = mutableListOf<File>()

    private val REQUEST_WRITE_PERMISSION = 786
    private val REQUEST_MEDIA_PERMISSION = 1001 // Unique request code for media permissions

    var STORAGE_DIRECTORY = "Download/TestFolder"

    companion object {
        const val ARG_DATA = "data"
        private const val ARG_COLOR = "navigation_bar_color"
        private const val ARG_MAIN_IMAGE_URL = "main_image_url"
        private const val ARG_TEXT_CATEGORY =
            "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/sticker/"

        fun newInstance(
            data: List<String?>?,
            navigationBarColor: Int,
            mainImageUrl: String,
            textCategory: String?
        )
                : StickerBottomSheetDialogFragment {
            val fragment = StickerBottomSheetDialogFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_DATA, ArrayList(data))
            args.putInt(ARG_COLOR, navigationBarColor)
            args.putString(ARG_MAIN_IMAGE_URL, mainImageUrl)
            args.putString(ARG_TEXT_CATEGORY, textCategory)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getStringArrayList(ARG_DATA)
        datas = arguments?.getString(ARG_TEXT_CATEGORY)!!
        mainImageUrl = arguments?.getString(ARG_MAIN_IMAGE_URL)!!


    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sticker_bottom_sheet_dialog, container, false)
        recyclerView = view.findViewById(R.id.recyclerViews)
        btnFreeDownload = view.findViewById(R.id.btnFreeDownload)
        imgStickers = view.findViewById(R.id.imgStickers)
        progressBar = view.findViewById(R.id.progressBar)
        txtName = view.findViewById(R.id.txtName)
        txtNumber = view.findViewById(R.id.txtNumbers)
        Glide.with(requireContext()).load(ARG_TEXT_CATEGORY + mainImageUrl).into(imgStickers)

        btnFreeDownload.setOnClickListener {
            checkPermissions()
        }

//        setRecyclerViewHeight(recyclerView)
        Log.e("imgStickers", "onCreateView: " + ARG_TEXT_CATEGORY + mainImageUrl)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        data = arguments?.getStringArrayList(ARG_DATA)
        adapter = Sticker_Sub_Image_Adapter(data ?: emptyList())
        recyclerView.adapter = adapter

        txtName.text = datas

        CoroutineScope(Dispatchers.IO).launch {
            val sizeText = imageSizeFetcher.fetchImageSizes(ARG_TEXT_CATEGORY, data)
            withContext(Dispatchers.Main) {
                txtNumber.text = "Size :- $sizeText"
            }
        }

        return view
    }


//    private fun downloadData() {
//        btnFreeDownload = requireView().findViewById(R.id.btnFreeDownload)
//        txtFreeDownload = requireView().findViewById(R.id.txtFreeDownload)
//        val dataToDownload = data
//
//        txtFreeDownload.text = "Downloading... 0%"
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val client = OkHttpClient()
//            dataToDownload!!.forEachIndexed { index, url ->
//                val encodedUrl = ARG_TEXT_CATEGORY + url!!.replace(" ", "%20")
//                Log.d("DownloadURL", "Downloading from: $encodedUrl")
//                val request = Request.Builder().url(encodedUrl).build()
//                client.newCall(request).execute().use { response ->
//                    if (!response.isSuccessful) {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(context, "Download failed: $url, Response code: ${response.code}", Toast.LENGTH_SHORT).show()
//                        }
//                        return@forEachIndexed
//                    }
//
//                    // Save the file using MediaStore
//                    val inputStream: InputStream? = response.body?.byteStream()
//                    val contentValues = ContentValues().apply {
//                        put(MediaStore.Images.Media.DISPLAY_NAME, "image_${index + 1}.jpg")
//                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Collage-Maker-Make-Anything") // Adjust as necessary
//                    }
//                    val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//                    uri?.let {
//                        requireContext().contentResolver.openOutputStream(it)?.use { outputStream ->
//                            inputStream?.copyTo(outputStream)
//                        }
//                    }
//
//                    // Update progress
//                    withContext(Dispatchers.Main) {
//                        val progress = ((index + 1) * 100) / dataToDownload.size
//                        txtFreeDownload.text = "Downloading... $progress%"
//                    }
//                }
//            }
//
//            // Update button text when download is complete
//            withContext(Dispatchers.Main) {
//                txtFreeDownload.text = "Download Complete"
//            }
//        }
//    }\



    private fun downloadData(thumbnailName: String) {
        btnFreeDownload = requireView().findViewById(R.id.btnFreeDownload)
        txtFreeDownload = requireView().findViewById(R.id.txtFreeDownload)
        val dataToDownload = data ?: return  // Guard against null data

        txtFreeDownload.text = "Downloading... 0%"
        Log.d("Download", "Starting download process...")

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val totalImages = dataToDownload.size
            var completedImages = 0

            // Create the folder if it doesn't exist
            val directoryPath = File(requireContext().getExternalFilesDir(null), "Stickers/$thumbnailName")
            if (!directoryPath.exists()) {
                directoryPath.mkdirs()  // Create the directory
            }

            dataToDownload.forEachIndexed { index, url ->
                val encodedUrl = ARG_TEXT_CATEGORY + url!!.replace(" ", "%20")
                Log.d("Download", "Downloading URL: $encodedUrl")

                val request = Request.Builder().url(encodedUrl).build()
                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            Log.e("Download", "Failed to download $encodedUrl, response code: ${response.code}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed: $url", Toast.LENGTH_SHORT).show()
                            }
                            return@forEachIndexed  // Skip to the next URL if download fails
                        }

                        // Check if response body is not null
                        response.body?.byteStream()?.use { inputStream ->
                            // Create the file name and path
                            val fileName = "sticker_${System.currentTimeMillis()}_${index + 1}.jpg"
                            val file = File(directoryPath, fileName)

                            // Save the image using FileOutputStream
                            FileOutputStream(file).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }

                            completedImages++  // Increment completed images
                        } ?: Log.e("Download", "Input stream is null for URL: $url")

                        val overallProgress = ((completedImages * 100) / totalImages)
                        withContext(Dispatchers.Main) {
                            txtFreeDownload.text = "Downloading... $overallProgress%"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Download Error", "Error downloading $url: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                txtFreeDownload.text = "Download Complete"
                Toast.makeText(requireContext(), "Stickers Downloaded", Toast.LENGTH_SHORT).show()
            }
        }
    }






    private fun saveDownloadedImagePath(imagePath: String) {
        val sharedPref =
            requireContext().getSharedPreferences("DownloadedImages", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Retrieve existing paths
        val existingPaths = sharedPref.getStringSet("imagePaths", mutableSetOf()) ?: mutableSetOf()

        // Add new image path to the set
        existingPaths.add(imagePath)

        // Save updated set of paths
        editor.putStringSet("imagePaths", existingPaths)
        editor.apply()
    }




    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above (API 33+): Use media permissions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above (API 29+): Scoped storage, no need for WRITE_EXTERNAL_STORAGE
            // You may need MANAGE_EXTERNAL_STORAGE if accessing all files
        } else {
            // Android 9 and below (API < 29): Use traditional storage permissions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsNeeded.toTypedArray(),
                REQUEST_MEDIA_PERMISSION
            )
        } else {
            downloadData(datas)  // If permissions are granted, proceed with download
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                downloadData(datas)
            } else {
                Toast.makeText(requireContext(), "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            // Disable swipe-to-dismiss
            behavior.isDraggable = false

            // Ensure the bottom sheet cannot be hidden
            behavior.isHideable = false

            // Start in the expanded state
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Set the bottom sheet to take up the full height
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet.requestLayout()



            // Set status bar color to white and text/icons to black
            dialog.window?.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
            dialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR



        }

        return dialog
    }



    //    override fun getTheme(): Int {
//        return R.style.CustomBottomSheetDialogTheme
//    }
}



