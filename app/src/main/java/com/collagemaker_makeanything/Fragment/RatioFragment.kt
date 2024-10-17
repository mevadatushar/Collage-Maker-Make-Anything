package com.collagemaker_makeanything.Fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.FragmentRatioBinding

class RatioFragment : Fragment() {

    private lateinit var binding: FragmentRatioBinding

    var listener: OnLayoutSelectedListener? = null

    interface OnLayoutSelectedListener {
        fun onLayoutSelected(aspectX: Int, aspectY: Int)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState == null) {
            binding = FragmentRatioBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUriString = arguments?.getString(ARG_IMAGE_URI)

        binding.lnrCustom.setOnClickListener {
            Log.d("RatioFragment", "Custom ratio selected")
            listener?.onLayoutSelected(0, 0) // Free cropping
        }

        binding.lnrLG11.setOnClickListener {
            Log.d("RatioFragment", "1:1 ratio selected")
            listener?.onLayoutSelected(1, 1)
        }

        binding.lnrLG45.setOnClickListener {
            Log.d("RatioFragment", "4:5 ratio selected")
            listener?.onLayoutSelected(4, 5)
        }

        binding.img54.setOnClickListener {
            Log.d("RatioFragment", "5:4 ratio selected")
            listener?.onLayoutSelected(5, 4)
        }

        binding.img34.setOnClickListener {
            Log.d("RatioFragment", "3:4 ratio selected")
            listener?.onLayoutSelected(3, 4)
        }

        binding.img43.setOnClickListener {
            Log.d("RatioFragment", "4:3 ratio selected")
            listener?.onLayoutSelected(4, 3)
        }

        binding.imgA4.setOnClickListener {
            Log.d("RatioFragment", "A4 ratio selected")
            listener?.onLayoutSelected(2, 3)
        }

        binding.lnrCover.setOnClickListener {
            Log.d("RatioFragment", "16:9 ratio selected")
            listener?.onLayoutSelected(16, 9)
        }

        binding.lnrDevice.setOnClickListener {
            Log.d("RatioFragment", "9:16 ratio selected")
            listener?.onLayoutSelected(9, 16)
        }

        binding.img23.setOnClickListener {
            Log.d("RatioFragment", "2:3 ratio selected")
            listener?.onLayoutSelected(2, 3)
        }

        binding.img32.setOnClickListener {
            Log.d("RatioFragment", "3:2 ratio selected")
            listener?.onLayoutSelected(3, 2)
        }

        binding.img12.setOnClickListener {
            Log.d("RatioFragment", "1:2 ratio selected")
            listener?.onLayoutSelected(1, 2)
        }

        binding.lnrPost.setOnClickListener {
            Log.d("RatioFragment", "Post ratio selected")
            listener?.onLayoutSelected(16, 9)
        }

        binding.lnrHeader.setOnClickListener {
            Log.d("RatioFragment", "Header ratio selected")
            listener?.onLayoutSelected(3, 1)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLayoutSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLayoutSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        private const val ARG_IMAGE_URI = "image_uri"

        fun newInstance(imageUris: String?): RatioFragment {
            val fragment = RatioFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URI, imageUris)
            fragment.arguments = args
            return fragment
        }
    }
}
