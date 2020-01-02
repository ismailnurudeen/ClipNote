package xyz.ismailnurudeen.clipnote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import xyz.ismailnurudeen.clipnote.events.EditEvent
import xyz.ismailnurudeen.clipnote.utils.AppUtils
import xyz.ismailnurudeen.clipnote.utils.PrefsManager

class ViewNoteActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var prefsManager: PrefsManager
    var clipId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)
        appUtils = AppUtils(this)
        prefsManager = PrefsManager(this)
        clipId = intent.getIntExtra("EXTRA_NOTE_ID", -1)

        if (clipId != -1) {
            content_text.text = prefsManager.clipNotes!![clipId].text
        } else {
            Toast.makeText(this, "No text found!", Toast.LENGTH_SHORT).show()
        }

        edit_note_btn.setOnClickListener {
            val editIntent = Intent(this, EditActivity::class.java)
            editIntent.putExtra("EXTRA_NOTE_ID", clipId)
            startActivity(editIntent)
        }

        copy_note_btn.setOnClickListener {
            appUtils.copyToClipBoard(content_text.text.toString())
            Toast.makeText(this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show()
        }

        delete_note_btn.setOnClickListener {
            showDeleteConfirmationDialog(clipId)
        }

        viewing_nav_back.setOnClickListener {
            finish()
        }

        viewing_share.setOnClickListener {
            appUtils.shareNote(content_text.text.toString())
        }
    }

    private fun showDeleteConfirmationDialog(index: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Note")
        builder.setMessage("Are you sure you want to delete this note?")
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Yes") { dialog, which ->
            if (appUtils.deleteNote(index)) {
                EventBus.getDefault().post(EditEvent())
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        builder.create().show()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public fun onEvent(event: EditEvent) {
        if (clipId != -1) content_text.text = prefsManager.clipNotes!![clipId].text
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
