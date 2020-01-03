package xyz.ismailnurudeen.clipnote.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.vansuita.materialabout.builder.AboutBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_clip_note.view.*
import xyz.ismailnurudeen.clipnote.MainActivity
import xyz.ismailnurudeen.clipnote.R
import xyz.ismailnurudeen.clipnote.models.ClipNote
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AppUtils(private val context: Context) {
    private val prefsManager: PrefsManager = PrefsManager(context)

    fun readFromClipBoard(): CharSequence {
        val cbm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (cbm.hasPrimaryClip()) {
            val cd = cbm.primaryClip
            return cd!!.getItemAt(0).text
        }
        return ""
    }

    fun copyToClipBoard(txt: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cd = ClipData.newPlainText("clip", txt)
        cm.setPrimaryClip(cd)
    }

    fun saveNote(txt: String): Boolean {
        val currentTime = formatDate(Calendar.getInstance().time)
        val notesList = prefsManager.clipNotes ?: ArrayList()
        notesList.add(ClipNote(UUID.randomUUID().toString(), txt, currentTime, currentTime))
        prefsManager.clipNotes = notesList
        return true
    }

    fun updateNote(index: Int, txt: String): Boolean {
        val currentTime = formatDate(Calendar.getInstance().time)
        val notesList = prefsManager.clipNotes ?: ArrayList()
        val clipNote = notesList[index]
        val note = ClipNote(clipNote.id, txt, clipNote.createdAt, currentTime)
        notesList.removeAt(index)
        notesList.add(index, note)
        prefsManager.clipNotes = notesList
        return true
    }

    fun deleteNote(index: Int): Boolean {
        val notesList = prefsManager.clipNotes ?: ArrayList()
        notesList.remove(notesList[index])
        prefsManager.clipNotes = notesList
        return true
    }

    fun deleteAllNotes(): Boolean {
        val notesList = prefsManager.clipNotes ?: ArrayList()
        notesList.clear()
        prefsManager.clipNotes = notesList
        return true
    }

    fun formatDate(date: Date?, format: String = "dd/MM/yyyy hh:MM a"): String {
        return if (date != null) SimpleDateFormat(format, Locale.getDefault()).format(date)
        else ""

    }

    @Throws(ParseException::class)
    fun stringToDate(dateString: String, format: String = "dd/MM/yyyy"): Date? =
        SimpleDateFormat(format, Locale.getDefault()).parse(dateString)

    fun shareNote(txt: String) {
        val otherIntent = Intent(Intent.ACTION_SEND)
        otherIntent.type = "text/plain"
        otherIntent.putExtra(Intent.EXTRA_TEXT, txt)
        context.startActivity(Intent.createChooser(otherIntent, "Share Note With..."))
    }

    private fun getRvFirstItem(rv: RecyclerView): View? {
        var view: View? = null
        val lm = rv.layoutManager
        if (lm is LinearLayoutManager) {
            val index = lm.findFirstCompletelyVisibleItemPosition()
            view = lm.findViewByPosition(index)
        } else if (lm is GridLayoutManager) {
            val index = lm.findFirstCompletelyVisibleItemPosition()
            view = lm.findViewByPosition(index)
        }
        return view
    }

    fun showHelpTapTargetForRv(activity: Activity) {
        val helpSequence = TapTargetSequence(activity)
        val itemView = getRvFirstItem(activity.main_rv) ?: return

        helpSequence.target(
            TapTarget.forView(
                itemView, "View Note",
                "Click to view note and slide left to view note actions"
            ).transparentTarget(true)
                .tintTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(1)
        )
        itemView.swipe_layout.open(true)
        helpSequence.target(
            TapTarget.forView(
                itemView.item_note_copy, "Copy Note",
                "Use this to copy note"
            ).transparentTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(2)
        )
        helpSequence.target(
            TapTarget.forView(
                itemView.item_note_delete, "Delete Note",
                "Use this to delete note"
            ).transparentTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(3)
        ).listener(object : TapTargetSequence.Listener {
            override fun onSequenceCanceled(lastTarget: TapTarget?) {}
            override fun onSequenceFinish() {
                Toast.makeText(context, "Fast and Easy :)", Toast.LENGTH_LONG).show()
                itemView.swipe_layout.open(true)
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {}

        }).continueOnCancel(true).start()
    }

    fun showHelpTapTarget(activity: Activity) {
        val helpSequence = TapTargetSequence(activity)
        helpSequence.target(
            TapTarget.forView(
                activity.home_add_note, "Create Custom Notes",
                "Click here to write your own notes"
            ).transparentTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(1)
        )
        helpSequence.target(
            TapTarget.forView(
                activity.home_options, "More Options and Customization",
                "Click this icon to see options like Delete All and Change Layout"
            ).tintTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(2)
        )
        helpSequence.target(
            TapTarget.forView(
                activity.home_info, "App Info and Help",
                "Click here to see more info about app and send feedback"
            ).tintTarget(true)
                .descriptionTextColorInt(Color.WHITE)
                .id(3)
        ).listener(object : TapTargetSequence.Listener {
            override fun onSequenceCanceled(lastTarget: TapTarget?) {}
            override fun onSequenceFinish() {
                Toast.makeText(context, "I hope that was helpful :)", Toast.LENGTH_LONG).show()
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {}
        }).continueOnCancel(true).start()
    }

    fun loadAbout(activity: Activity, theme: Int) {
        activity.setTheme(theme)
        val flHolder: FrameLayout = activity.findViewById(R.id.about_frame)

        val builder: AboutBuilder = AboutBuilder.with(activity)
            .addWhatsappLink(
                activity.getString(R.string.my_name),
                activity.getString(R.string.my_phone_num)
            )
            .addWebsiteLink(R.string.website)
            .addFacebookLink(activity.getString(R.string.my_fb_id))
            .addTwitterLink(activity.getString(R.string.my_twitter_id))
            .addFiveStarsAction()
            .addUpdateAction()
            .addGooglePlayStoreLink(activity.getString(R.string.my_play_id))
            .addMoreFromMeAction("Deep Syntax")
            .addShareAction("Share App")
            .addFeedbackAction(R.string.my_email)
            .addLinkedInLink(activity.getString(R.string.my_linkedin_id))
            .setVersionNameAsAppSubTitle()
            .setLinksAnimated(false)
            .setDividerDashGap(13)
            .setName(activity.getString(R.string.my_name))
            .setSubTitle("Mobile Developer")
            .setLinksColumnsCount(4)
            .setBrief(activity.getString(R.string.about_me))
            .setPhoto(R.drawable.profile_image)
            .setCover(R.drawable.about_cover)
            .setAppName(R.string.app_name)
            .setVersionNameAsAppSubTitle()
            .setActionsColumnsCount(2)
            .addIntroduceAction {
                PrefsManager(activity).hasShownHomeTapTarget = false
                val mainIntent=Intent(activity, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity.startActivity(mainIntent)
            }
            .addHelpAction(null as Intent?)
            .setWrapScrollView(true)
            .setShowAsCard(false)

        val view = builder.build()
        flHolder.addView(view)
    }
}