package com.dede.android_eggs.views.settings.prefs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Outlined.language
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.basic.createLocalesContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale


class LanguagePref : SettingPref(null, getOptions(), SYSTEM) {
    companion object {

        private const val TAG = "LanguagePref"

        // For API<24 the application does not have a localeList instead it has a single locale
        // Unsupported region
        private val isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

        private const val SYSTEM = 0
        private const val MORE = -1
        private const val SIMPLIFIED_CHINESE = 2    // zh-CN
        private const val TRADITIONAL_CHINESE = 3   // zh-HK, zh-TW
        private const val ENGLISH = 4               // en
        private const val RUSSIAN = 5               // ru
        private const val ITALIAN = 6               // it
        private const val GERMANY = 7               // de
        private const val PORTUGAL = 8              // es
        private const val INDONESIA = 9             // in-ID
        private const val JAPANESE = 10             // ja-JP
        private const val KOREAN = 11               // ko
        private const val FRENCH = 12               // fr

        private fun getOptions(): List<Op> {
            val options = mutableListOf(
                Op(SYSTEM, iconUnicode = language, titleRes = R.string.summary_system_default),
                Op(MORE, titleRes = R.string.pref_title_language_more),
            )
            val value = getValueByLocale(AppCompatDelegate.getApplicationLocales())
            val languageOp = languageOptions.find { it.value == value }
            if (languageOp != null) {
                options.add(1, languageOp)
            }
            return options
        }

        private class LangOp(value: Int, val locale: Locale, titleRes: Int) :
            Op(value, titleRes = titleRes)

        private val languageOptions = listOf(
            LangOp(SIMPLIFIED_CHINESE, Locale.SIMPLIFIED_CHINESE, R.string.language_zh_SC),
            LangOp(TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE, R.string.language_zh_TC),
            LangOp(ENGLISH, Locale.ENGLISH, R.string.language_en),
            LangOp(RUSSIAN, createLocale("ru"), R.string.language_ru),
            LangOp(ITALIAN, Locale.ITALIAN, R.string.language_it),
            LangOp(GERMANY, Locale.GERMANY, R.string.language_de),
            LangOp(PORTUGAL, createLocale("es"), R.string.language_es),
            LangOp(INDONESIA, createLocale("in", "ID"), R.string.language_in_ID),
            LangOp(FRENCH, Locale.FRENCH, R.string.language_fr),
            LangOp(JAPANESE, Locale.JAPAN, R.string.language_ja_JP),
            LangOp(KOREAN, Locale.KOREAN, R.string.language_ko),
        )

        private fun getLocaleByValue(value: Int): LocaleListCompat {
            val op = languageOptions.find { it.value == value }
            if (op != null) {
                return LocaleListCompat.create(op.locale)
            }
            return LocaleListCompat.getEmptyLocaleList()
        }

        private fun getValueByLocale(localeList: LocaleListCompat): Int {
            if (localeList.isEmpty) {
                return SYSTEM
            }
            val locale = localeList.get(0)
            val op = languageOptions.find { it.locale == locale }
            if (op != null) {
                return op.value
            }
            Log.w(TAG, "Not found language value by locale: $localeList, use default!")
            return SYSTEM
        }

        fun getApplicationLocale(): Locale {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                Locale.getDefault()
            } else {
                locales.get(0) ?: Locale.getDefault()
            }
        }

        fun resetApi23Locale() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
        }

        private fun createLocale(language: String, region: String = ""): Locale {
            return Locale.Builder()
                .setLanguage(language)
                .setRegion(region)
                .build()
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_language

    override val enable: Boolean
        get() = isEnabled

    override fun setValue(context: Context, value: Int): Boolean {
        return false
    }

    override fun getValue(context: Context, default: Int): Int {
        return getValueByLocale(AppCompatDelegate.getApplicationLocales())
    }

    private fun restoreLastOption(lastValue: Int) {
        val selectedOp = options.find { it.value == lastValue }
        if (selectedOp != null) {
            selectedOption(selectedOp)
        }
    }

    override fun onPreOptionSelected(context: Context, option: Op): Boolean {
        if (option.value != MORE) {
            return false
        }
        var choiceIndex = languageOptions.indexOfFirst { it.value == selectedValue }
        val lastChoiceIndex = choiceIndex
        val languages = languageOptions.map { context.getString(it.titleRes) }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.pref_title_language)
            .setSingleChoiceItems(languages, choiceIndex) { dialogInterface, index ->
                choiceIndex = index
                // change to locale text
                val dialog = dialogInterface as Dialog
                val value = languageOptions[index].value
                val locales = getLocaleByValue(value)
                val localesCtx = context.createLocalesContext(locales)
                dialog.setTitle(localesCtx.getString(R.string.pref_title_language))
                dialog.findViewById<TextView>(android.R.id.button1)?.text =
                    localesCtx.getString(android.R.string.ok)
                dialog.findViewById<TextView>(android.R.id.button2)?.text =
                    localesCtx.getString(android.R.string.cancel)
                dialog.findViewById<TextView>(android.R.id.button3)?.text =
                    localesCtx.getString(R.string.label_translation)
            }
            .setOnDismissListener {
                if (lastChoiceIndex == choiceIndex) {
                    restoreLastOption(selectedValue)
                }
            }
            .setNeutralButton(R.string.label_translation) { _, _ ->
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_translation).toUri()
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                restoreLastOption(selectedValue)
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (choiceIndex != lastChoiceIndex) {
                    onOptionSelected(context, languageOptions[choiceIndex])
                }
            }
            .show()
        return true
    }

    override fun onOptionSelected(context: Context, option: Op) {
        AppCompatDelegate.setApplicationLocales(getLocaleByValue(option.value))
    }
}