package xyz.ismailnurudeen.clipnote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import xyz.ismailnurudeen.clipnote.utils.AppUtils

class ProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appUtils = AppUtils(this)
        val txt = intent.extras!!.getCharSequence(Intent.EXTRA_PROCESS_TEXT)!!.toString()
        copyToClipBoard(txt)
        appUtils.saveNote(txt)
        Toast.makeText(this, "Copied & Saved", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun copyToClipBoard(txt: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cd = ClipData.newPlainText("clip", txt)
        cm.setPrimaryClip(cd)
    }
}
