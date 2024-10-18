package com.collagemaker_makeanything.Activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.Adapter.StickerActivityAdapter
import com.collagemaker_makeanything.Adapter.Sticker_Sub_Image_Adapter
import com.collagemaker_makeanything.Api.Dataas
import com.collagemaker_makeanything.Api.Groupas
import com.collagemaker_makeanything.Api.OkHttpHelpers
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.StoreFragment.StickerBottomSheetDialogFragment
import com.collagemaker_makeanything.databinding.ActivityStickerBinding
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class StickerActivity : BaseActivity() {

    lateinit var binding: ActivityStickerBinding
    private lateinit var imageUri: String
    private var croppedImageUri: String? = null



    //    private lateinit var viewModel: StickerViewModel
    private lateinit var adapter: StickerActivityAdapter
    private lateinit var adapters: Sticker_Sub_Image_Adapter
    private val groupsList = mutableListOf<Groupas>()
    private var data: List<String?>? = null
    private lateinit var datas: String
    private lateinit var mainImageUrl: String

    companion object {
        private const val ARG_DATA = "data"
        private const val ARG_COLOR = "navigation_bar_color"
        private const val ARG_MAIN_IMAGE_URL = "main_image_url"
        private const val ARG_TEXT_CATEGORY =
            "https://s3.eu-north-1.amazonaws.com/photoeditorbeautycamera.com/photoeditor/sticker/"

        fun newInstance(
            data: List<String?>?,
            navigationBarColor: Int,
            mainImageUrl: String,
            textCategory: String?
        ): StickerBottomSheetDialogFragment {
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
        binding = ActivityStickerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()
        initLoadApi()
    }

    private fun initView() {
        // Retrieve the imageUri from the intent
        imageUri = intent.getStringExtra("imageUri") ?: ""

        // Load the image using a function
        if (imageUri.isNotEmpty()) {
            loadSelectedImage() // Call the function to load the image
        } else {
            Log.d("StickerActivity", "No image URI received.")
        }
    }

    private fun initLoadApi() {


        val url =
            "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/stickers.json"
        var baseurl =
            "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/sticker/"


        OkHttpHelpers.fetchSticker(url) { stickerApi ->
            runOnUiThread {

                if (stickerApi != null) {
                    Log.e("StoreFragment", "Fetched data: ${stickerApi.data}")

                    stickerApi.data?.let {
                        populateGroupsList(it)
                        adapter.updateData(groupsList)
                    } ?: Log.e("StoreFragment", "Data is null")
                } else {
                    Log.e("StoreFragment", "Failed to fetch data")
                }
            }
        }

        adapter = StickerActivityAdapter(this, groupsList)
        binding.rcvStiker.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        data = intent.getStringArrayListExtra(StickerBottomSheetDialogFragment.ARG_DATA)
        adapters = Sticker_Sub_Image_Adapter(data ?: emptyList())
        binding.rcvStiker.adapter = adapter


        val imageUrls = intent.getStringArrayListExtra("imageUrls") ?: arrayListOf()

        // Set up the RecyclerView
        binding.rcvStikers.layoutManager = GridLayoutManager(this,4)
        adapters = Sticker_Sub_Image_Adapter(imageUrls) // Make sure this adapter is set to handle the images
        binding.rcvStikers.adapter = adapter


//        adapter = Sticker_Activity_Adapter(this,groupsList)
//        binding.rcvStikers.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)
//        data = intent.getStringArrayListExtra(StickerBottomSheetDialogFragment.ARG_DATA)
//        adapters = Sticker_Sub_Image_Adapter(data ?: emptyList())
//        binding.rcvStikers.adapter = adapter
    }


    private fun populateGroupsList(data: Dataas) {
        val items = mutableListOf<Groupas>()

        // Use Kotlin reflection to iterate over the properties of the Dataas class
        data::class.memberProperties.forEach { property ->
            val prop = property as? KProperty1<Dataas, *>
            val value = prop?.get(data)

            // Check if the value is an instance of a class containing a Groupas
            if (value != null) {
                // Use reflection to find the Groupas property within the nested class
                val groupProperty = value::class.memberProperties
                    .firstOrNull { it.returnType.classifier == Groupas::class }
                        as? KProperty1<Any, Groupas>

                val nestedGroup = groupProperty?.get(value)

                if (nestedGroup != null) {
                    val categoryName = property.name.replace("_", " ").capitalizeWords()

                    nestedGroup.let {
                        if (it.subImageUrl != null || it.mainImageUrl != null) {
                            items.add(
                                Groupas(
                                    subImageUrl = it.subImageUrl,
                                    mainImageUrl = it.mainImageUrl,
                                    textCategory = categoryName
                                )
                            )
                        }
                    }
                }
            }
        }

        groupsList.clear()
        groupsList.addAll(items)
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

    private fun loadSelectedImage() {
        val uriToLoad = croppedImageUri ?: imageUri
        if (uriToLoad.isNotEmpty()) {
            Log.d("EditActivity", "Loading image URI: $uriToLoad")
            Glide.with(this)
                .load(uriToLoad)
                .into(binding.imgContainer)
        } else {
            Log.d("EditActivity", "No image URI found.")
            Toast.makeText(this, "No image found to load", Toast.LENGTH_SHORT).show()
        }
    }
}