package xyz.ismailnurudeen.clipnote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import org.greenrobot.eventbus.EventBus
import xyz.ismailnurudeen.clipnote.events.EditEvent
import xyz.ismailnurudeen.clipnote.utils.AppUtils
import xyz.ismailnurudeen.clipnote.utils.PrefsManager

class EditActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var prefManager: PrefsManager
    private var clipId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        appUtils = AppUtils(this)
        prefManager = PrefsManager(this)

        clipId = intent.getIntExtra("EXTRA_NOTE_ID", -1)
        if (clipId != -1) {
            val noteTxt = prefManager.clipNotes!![clipId].text
            content_edit.setText(noteTxt)
            content_edit.setSelection(noteTxt.length)
        }

        editing_save_btn.setOnClickListener {
            if (content_edit.text.isNotEmpty()) {
                val txt = content_edit.text.toString()
                if (clipId != -1) {
                    if (appUtils.updateNote(clipId, txt)) {
                        EventBus.getDefault().postSticky(EditEvent())
                        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    if (appUtils.saveNote(txt)) {
                        EventBus.getDefault().postSticky(EditEvent())
                        Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Can't save empty note!", Toast.LENGTH_SHORT).show()
            }
        }

        editing_cancel_btn.setOnClickListener {
            if (clipId != -1 || content_edit.text.isNotEmpty()) {
                showExitConfirmationDialog()
            } else {
                finish()
            }
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard Note?")
        builder.setMessage(
            "Are you sure you want to cancel current note?" +
                    "\n\n** Unsaved changes would be lost!"
        )
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }
        builder.create().show()
    }

    override fun onBackPressed() {
        if (clipId != -1 || content_edit.text.isNotEmpty()) {
            showExitConfirmationDialog()
        } else {
            super.onBackPressed()
        }
    }
}
