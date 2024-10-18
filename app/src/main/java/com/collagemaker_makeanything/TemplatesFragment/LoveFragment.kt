package com.collagemaker_makeanything.TemplatesFragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collagemaker_makeanything.Activity.TemplatesActivity
import com.collagemaker_makeanything.Adapter.CollageTemplateAdapter
import com.collagemaker_makeanything.Api.Group
import com.collagemaker_makeanything.Api.OkHttpHelper
import com.collagemaker_makeanything.databinding.FragmentLoveBinding

class LoveFragment : Fragment(){

    private lateinit var binding: FragmentLoveBinding
    lateinit var adapter: CollageTemplateAdapter
//    lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoveBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = GridLayoutManager(TemplatesActivity.contexts,2)

        val url = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/templates.json"

        requireActivity().runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }

        OkHttpHelper.fetchTemplates(url) { categoryApi ->
            requireActivity().runOnUiThread {
                binding.progressBar.visibility = View.GONE
                categoryApi?.let {

                    val groupsList = mutableListOf<Group>()

                    it.templates.forEach { template ->

                        template?.love?.let { love ->
                            love.group1?.let { groupsList.add(it) }
                            love.group2?.let { groupsList.add(it) }
                            love.group3?.let { groupsList.add(it) }
                            love.group4?.let { groupsList.add(it) }
                            love.group5?.let { groupsList.add(it) }
                            love.group6?.let { groupsList.add(it) }
                            love.group7?.let { groupsList.add(it) }
                            love.group8?.let { groupsList.add(it) }
                            love.group9?.let { groupsList.add(it) }
                            love.group10?.let { groupsList.add(it) }
                            love.group11?.let { groupsList.add(it) }
                            love.group12?.let { groupsList.add(it) }
                            love.group13?.let { groupsList.add(it) }
                            love.group14?.let { groupsList.add(it) }
                            love.group15?.let { groupsList.add(it) }
                            love.group16?.let { groupsList.add(it) }
                            love.group17?.let { groupsList.add(it) }
                            love.group18?.let { groupsList.add(it) }
                            love.group19?.let { groupsList.add(it) }
                            love.group20?.let { groupsList.add(it) }
                            // Repeat for all groups in the love category
                        }
                    }

                    requireActivity().runOnUiThread {
                        adapter = CollageTemplateAdapter(
                            requireContext(),
                            groupsList
                        ) /*{ selectedGroup ->
                        showBottomSheetForImageEditurl()
                    }*/
                        binding.recyclerView.adapter = adapter
                    }
                }
            }
        }
    }

}