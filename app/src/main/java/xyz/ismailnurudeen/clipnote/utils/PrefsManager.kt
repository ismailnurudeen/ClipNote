package xyz.ismailnurudeen.clipnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.ismailnurudeen.clipnote.models.ClipNote

@SuppressLint("CommitPrefEdits")
class PrefsManager(context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    private var gson: Gson

    // shared pref mode
    private val PRIVATE_MODE = 0

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }
    var hasShownHomeTapTarget: Boolean
        get() = pref.getBoolean(SHOWN_HOME_TT, false)
        set(shownTapTarget) {
            editor.putBoolean(SHOWN_HOME_TT, shownTapTarget)
            editor.commit()
        }
    var hasShownNoteTapTarget: Boolean
        get() = pref.getBoolean(SHOWN_NOTE_TT, false)
        set(shownTapTarget) {
            editor.putBoolean(SHOWN_NOTE_TT, shownTapTarget)
            editor.commit()
        }

    var clipNotes: ArrayList<ClipNote>?
        get() {
            Log.i("GSON_VALUE", pref.getString(PREF_CLIP_NOTES, ""))
            val type = object : TypeToken<ArrayList<ClipNote>>() {}.type

            return gson.fromJson(pref.getString(PREF_CLIP_NOTES, ""), type)
        }
        set(notes) {
            val noteString = gson.toJson(notes)
            editor.putString(PREF_CLIP_NOTES, noteString)
            editor.commit()
        }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        gson = Gson()
    }

    companion object {
        // Shared preferences keys
        private const val PREF_NAME = "clip_note_pref"
        private const val IS_FIRST_TIME_LAUNCH = "is_first_launch"
        private const val PREF_CLIP_NOTES = "clip_notes"
        private const val SHOWN_HOME_TT = "shownHomeTapTarget"
        private const val SHOWN_NOTE_TT = "shownNoteTapTarget"
    }
}