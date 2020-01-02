package xyz.ismailnurudeen.clipnote

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_clip_note.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import xyz.ismailnurudeen.clipnote.events.EditEvent
import xyz.ismailnurudeen.clipnote.models.ClipNote
import xyz.ismailnurudeen.clipnote.utils.AppUtils
import xyz.ismailnurudeen.clipnote.utils.PrefsManager
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var prefsManager: PrefsManager
    var clipList: ArrayList<ClipNote> = ArrayList()
    lateinit var notesAdapter: MainAdapter
    var isListLayout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appUtils = AppUtils(this)
        prefsManager = PrefsManager(this)
        prefsManager.isFirstTimeLaunch = false
        if (!prefsManager.hasShownHomeTapTarget) {
            appUtils.showHelpTapTarget(this)
            prefsManager.hasShownHomeTapTarget = true
        }
        refreshNotes()

        home_add_note.setOnClickListener {
            val editIntent = Intent(this, EditActivity::class.java)
            startActivity(editIntent)
        }
        val optionsPopup = PopupMenu(this, home_options)
        optionsPopup.inflate(R.menu.options_popup)

        optionsPopup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_change_layout -> {
                    switchLayout()
                }
                R.id.menu_delete_all_notes -> {
                    showDeleteAllConfirmationDialog()
                }
                R.id.menu_how_to -> {
                    prefsManager.hasShownNoteTapTarget = true
                    appUtils.showHelpTapTarget(this)
                }
            }
            true
        }

        home_options.setOnClickListener {
            optionsPopup.show()
        }
        home_info.setOnClickListener {
            startActivity(Intent(this, AboutActivity2::class.java))
        }

    }

    private fun switchLayout() {
        if (isListLayout) {
            main_rv.layoutManager = GridLayoutManager(this, 2)
        } else {
            main_rv.layoutManager = LinearLayoutManager(this)
        }
        isListLayout = !isListLayout
        refreshNotes()
    }

    private fun refreshNotes() {
        clipList = prefsManager.clipNotes ?: ArrayList()
        if (clipList.isEmpty()) {
            no_notes_view.visibility = View.VISIBLE
            main_rv.visibility = View.GONE
        } else {
            no_notes_view.visibility = View.GONE
            main_rv.visibility = View.VISIBLE

            notesAdapter = MainAdapter(this, clipList)
            main_rv.adapter = notesAdapter

            notesAdapter.registerAdapterDataObserver(object :
                androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    if (notesAdapter.itemCount > 0) {
                        main_rv.visibility = View.VISIBLE
                        no_notes_view.visibility = View.GONE
                    } else {
                        no_notes_view.visibility = View.VISIBLE
                        main_rv.visibility = View.GONE
                    }
                    super.onChanged()
                }
            })
        }
    }

    private fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All Notes")
        builder.setMessage("Are you sure you want to delete all notes?")
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Yes") { dialog, which ->
            if (appUtils.deleteAllNotes()) {
                clipList.clear()
                Toast.makeText(this, "All Notes Deleted!", Toast.LENGTH_SHORT).show()
                main_rv.adapter?.notifyDataSetChanged()
            }
        }
        builder.create().show()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public fun onEvent(event: EditEvent) {
        refreshNotes()
        Log.i("EVENT_BUS", "Event Fired!")
    }

    inner class MainAdapter(
        private val context: Context,
        private val clipNotes: ArrayList<ClipNote>
    ) :
        androidx.recyclerview.widget.RecyclerView.Adapter<MainAdapter.ClipHolder>() {
        private val viewBinderHelper = ViewBinderHelper()

        init {
            viewBinderHelper.setOpenOnlyOne(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipHolder {
            val itemLayout = if (isListLayout) R.layout.item_clip_note else R.layout.item_clip_note
            return ClipHolder(LayoutInflater.from(context).inflate(itemLayout, parent, false))
        }

        override fun getItemCount(): Int = clipNotes.size

        override fun onBindViewHolder(holder: ClipHolder, pos: Int) {
            viewBinderHelper.bind(holder.swipeRevealLayout, UUID.randomUUID().toString())
            holder.bind(clipNotes[pos])
        }

        inner class ClipHolder(private val iv: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(iv) {
            var swipeRevealLayout: SwipeRevealLayout = iv.swipe_layout

            fun bind(clipNote: ClipNote) {
                iv.text.text = clipNote.text
                swipeRevealLayout.clip_item_card.setOnClickListener {
                    val viewIntent = Intent(context, ViewNoteActivity::class.java)
                    viewIntent.putExtra("EXTRA_NOTE_ID", adapterPosition)
                    context.startActivity(viewIntent)
                }
                iv.item_note_copy.setOnClickListener {
                    appUtils.copyToClipBoard(clipNote.text)
                    Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
                }
                iv.item_note_delete.setOnClickListener {
                    showDeleteConfirmationDialog(adapterPosition)
                }
                if (!prefsManager.hasShownNoteTapTarget) {
                    if (adapterPosition == 1) {
                        TapTargetView.showFor(this@MainActivity, TapTarget.forView(
                            swipeRevealLayout.clip_item_card, "Note Actions",
                            "Swipe card left to view note actions"
                        ).transparentTarget(true)
                            .tintTarget(true)
                            .titleTextColorInt(Color.BLACK)
                            .descriptionTextColor(R.color.about_text_color)
                            .targetRadius(85)
                            .outerCircleAlpha(0.2F),
                            object : TapTargetView.Listener() {
                                override fun onTargetClick(view: TapTargetView?) {
                                    super.onTargetClick(view)
                                    swipeRevealLayout.open(true)
                                }
                            })
                        prefsManager.hasShownNoteTapTarget = true
                    }
                }
            }

            private fun showDeleteConfirmationDialog(index: Int) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Delete Note")
                builder.setMessage("Are you sure you want to delete this note?")
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                builder.setPositiveButton("Yes") { dialog, which ->
                    if (appUtils.deleteNote(index)) {
                        clipNotes.removeAt(index)
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                }
                builder.create().show()
            }
        }
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
