package com.collagemaker_makeanything.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {
    lateinit var binding: ActivitySettingsBinding

    private lateinit var context: Context
    private lateinit var dialogLanguage: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        with(binding){
                    binding.imgPro.setOnClickListener {
                        val i = Intent(this@SettingsActivity, PremiumActivity::class.java)
                        startActivity(i)
                    }

                    binding.imgBack.setOnClickListener {
                        onBackPressed()
                    }

                    binding.lnrFeedback.setOnClickListener {
                        showFeedbackDialog()
                    }

                    binding.lnrResolution.setOnClickListener {
                        showResolutionSelectionDialog()
                    }

                    binding.lnrLanguage.setOnClickListener {
                      //  showLanguageSelectDialog()
                    }

                    binding.lnrManageSubscription.setOnClickListener {
                        val i = Intent(this@SettingsActivity, ManageSubscriptionActivity::class.java)
                        startActivity(i)
                    }

                    binding.llSavePath.setOnClickListener {
                        val i = Intent(this@SettingsActivity, SavePathActivity::class.java)
                        startActivity(i)
                    }


                }
    }


    private fun showFeedbackDialog() {
        runOnUiThread {
            if (isFinishing || isDestroyed) {
                Log.e("ShowFeedbackDialog", "Activity is not in a valid state")
                return@runOnUiThread
            }

            val context = this // This is valid and available context

            val dialog = Dialog(context)
            val dialogView = layoutInflater.inflate(R.layout.feedback_layout, null)

            dialog.setContentView(dialogView)

            val textFeedback = dialogView.findViewById<TextView>(R.id.txtFeedbacks)
            val textNoThanks = dialogView.findViewById<TextView>(R.id.txtNoThanks)
            val textSendFeedback = dialogView.findViewById<TextView>(R.id.txtFeedback)

            textFeedback.text = resources.getString(R.string.FeedbackCaption)
            textNoThanks.text = resources.getString(R.string.Nothanks)
            textSendFeedback.text = resources.getString(R.string.NotReally)

            textNoThanks.setOnClickListener {
                dialog.dismiss()
            }

            textSendFeedback.setOnClickListener {
                // Implement feedback sending logic here
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun showResolutionSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.resolution_dialog, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedResolution = sharedPreferences.getString("SelectedResolution", "DefaultResolution")

        when (savedResolution) {
            "Resolution1" -> radioGroup.check(R.id.radio_high_resolution)
            "Resolution2" -> radioGroup.check(R.id.radio_regular_resolution)
            else -> radioGroup.clearCheck()
        }

        for (i in 0 until radioGroup.childCount) {
            val childView = radioGroup.getChildAt(i)
            if (childView is RadioButton) {
                val radioButton = childView
                val colorStateList = if (radioButton.isChecked) {
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.feedback_text))
                } else {
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                }
                radioButton.buttonTintList = colorStateList
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val childView = group.getChildAt(i)
                if (childView is RadioButton) {
                    val radioButton = childView
                    val colorStateList = if (radioButton.id == checkedId) {
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.feedback_text))
                    } else {
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                    }
                    radioButton.buttonTintList = colorStateList
                }
            }

            val selectedRadioButton = dialogView.findViewById<RadioButton>(checkedId)
            val selectedResolution = when (selectedRadioButton.id) {
                R.id.radio_high_resolution -> "Resolution1"
                R.id.radio_regular_resolution -> "Resolution2"
                else -> "DefaultResolution"
            }

            val editor = sharedPreferences.edit()
            editor.putString("SelectedResolution", selectedResolution)
            editor.apply()

            dialog.dismiss()
        }

        dialog.show()
    }

   /* private fun showLanguageSelectDialog() {
        val dialogView = layoutInflater.inflate(R.layout.select_language, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.selectLanguageRadioGroup)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("SelectedLanguage", "en")

        when (savedLanguage)
        {
            "en" -> radioGroup.check(R.id.rbEnglish)
            "da" -> radioGroup.check(R.id.rbDansk)
            "de" -> radioGroup.check(R.id.rbDeutsch)
            "es" -> radioGroup.check(R.id.rbEspañol)
            "fr" -> radioGroup.check(R.id.rbFrançais)
            "hi" -> radioGroup.check(R.id.rbHindi)
            "it" -> radioGroup.check(R.id.rbItaliano)
            "ja" -> radioGroup.check(R.id.rbJapanese)
            "ko" -> radioGroup.check(R.id.rbKorean)
            "ms" -> radioGroup.check(R.id.rbBahasaMelayu)
            else -> radioGroup.clearCheck()
        }

        for (i in 0 until radioGroup.childCount)
        {
            val childView = radioGroup.getChildAt(i)
            if (childView is RadioButton)
            {
                val radioButton = childView
                val colorStateList = if (radioButton.isChecked)
                {
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                }
                else
                {
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                }
                radioButton.buttonTintList = colorStateList
            }
        }

        dialogLanguage = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogLanguage.window?.setBackgroundDrawableResource(android.R.color.transparent)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount)
            {
                val childView = group.getChildAt(i)
                if (childView is RadioButton)
                {
                    val radioButton = childView
                    val colorStateList = if (radioButton.id == checkedId)
                    {
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                    }
                    else
                    {
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                    }
                    radioButton.buttonTintList = colorStateList
                }
            }

            val selectedRadioButton = dialogView.findViewById<RadioButton>(checkedId)
            val selectedLanguage = when (selectedRadioButton.id)
            {
                R.id.rbEnglish -> "en"
                R.id.rbDansk -> "da"
                R.id.rbDeutsch -> "de"
                R.id.rbEspañol -> "es"
                R.id.rbFrançais -> "fr"
                R.id.rbHindi -> "hi"
                R.id.rbItaliano -> "it"
                R.id.rbJapanese -> "ja"
                R.id.rbKorean -> "ko"
                R.id.rbBahasaMelayu -> "ms"
                else -> "en"
            }

            val editor = sharedPreferences.edit()
            editor.putString("SelectedLanguage", selectedLanguage)
            editor.apply()

            LocaleHelper.setLocale(this, selectedLanguage)

            updateUIForSelectedLanguage()

            dialogLanguage.dismiss()
            recreate()
        }

        dialogLanguage.show()
    }       */


}