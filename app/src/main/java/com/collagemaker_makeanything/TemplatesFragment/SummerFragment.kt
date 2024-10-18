package com.collagemaker_makeanything.TemplatesFragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
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
import com.collagemaker_makeanything.databinding.FragmentSummerBinding


class SummerFragment : Fragment() {
    private lateinit var binding: FragmentSummerBinding
    lateinit var adapter: CollageTemplateAdapter
//    lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = GridLayoutManager(TemplatesActivity.contexts, 2)

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

                        template?.summer?.let { summer ->
                            summer.group1?.let { groupsList.add(it) }
                            summer.group2?.let { groupsList.add(it) }
                            summer.group3?.let { groupsList.add(it) }
                            summer.group4?.let { groupsList.add(it) }
                            summer.group5?.let { groupsList.add(it) }
                            summer.group6?.let { groupsList.add(it) }
                            summer.group7?.let { groupsList.add(it) }
                            summer.group8?.let { groupsList.add(it) }
                            summer.group9?.let { groupsList.add(it) }
                            summer.group10?.let { groupsList.add(it) }
                            summer.group11?.let { groupsList.add(it) }
                            summer.group12?.let { groupsList.add(it) }
                            summer.group13?.let { groupsList.add(it) }
                            summer.group14?.let { groupsList.add(it) }
                            summer.group15?.let { groupsList.add(it) }
                            summer.group16?.let { groupsList.add(it) }
                            summer.group17?.let { groupsList.add(it) }
                            summer.group18?.let { groupsList.add(it) }
                            summer.group19?.let { groupsList.add(it) }
//                        summer.group20?.let { groupsList.add(it) }
                            // Repeat for all groups in the summer categ-+ory
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