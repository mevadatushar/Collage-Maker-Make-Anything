package com.collagemaker_makeanything.TemplatesFragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collagemaker_makeanything.Activity.TemplatesActivity
import com.collagemaker_makeanything.Adapter.CollageTemplateAdapter
import com.collagemaker_makeanything.Api.Group
import com.collagemaker_makeanything.Api.OkHttpHelper
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.FragmentBirthdayBinding

import com.google.android.material.bottomsheet.BottomSheetDialog

class BirthdayFragment : Fragment(){

    private lateinit var binding: FragmentBirthdayBinding
    lateinit var adapter: CollageTemplateAdapter
//    lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = GridLayoutManager(TemplatesActivity.contexts, 2)

        val url = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/templates.json"


        //        testing
//        val url = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/testing.json"
//        val url = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/DemoFile.json"

        requireActivity().runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }

        OkHttpHelper.fetchTemplates(url) { categoryApi ->

            requireActivity().runOnUiThread {
                binding.progressBar.visibility = View.GONE
                categoryApi?.let {
                    val groupsList = mutableListOf<Group>()

                    it.templates.forEach { template ->
                        template?.birthday?.let { birthday ->
                            birthday.group1?.let { groupsList.add(it) }
                            birthday.group2?.let { groupsList.add(it) }
                            birthday.group3?.let { groupsList.add(it) }
                            birthday.group4?.let { groupsList.add(it) }
                            birthday.group5?.let { groupsList.add(it) }
                            birthday.group6?.let { groupsList.add(it) }
                            birthday.group7?.let { groupsList.add(it) }
                            birthday.group8?.let { groupsList.add(it) }
                            birthday.group9?.let { groupsList.add(it) }
                            birthday.group10?.let { groupsList.add(it) }
                            birthday.group11?.let { groupsList.add(it) }
                            birthday.group12?.let { groupsList.add(it) }
                            birthday.group13?.let { groupsList.add(it) }
                            birthday.group14?.let { groupsList.add(it) }
                            birthday.group15?.let { groupsList.add(it) }
                            birthday.group16?.let { groupsList.add(it) }
                            birthday.group17?.let { groupsList.add(it) }
                            birthday.group18?.let { groupsList.add(it) }
                            birthday.group19?.let { groupsList.add(it) }
                            birthday.group20?.let { groupsList.add(it) }
                            // Repeat for all groups in the birthday category
                        }
                    }

                    requireActivity().runOnUiThread {
                        adapter = CollageTemplateAdapter(requireContext(), groupsList) /*{ selectedGroup ->
                        showBottomSheetForImageEditurl()
                    }*/
                        binding.recyclerView.adapter = adapter
                    }
                }
            }
        }
    }



}