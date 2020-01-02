package xyz.ismailnurudeen.clipnote

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aboutPage = AboutPage(this)
            .isRTL(false)
            .setDescription(getString(R.string.about_me))
            .setImage(R.drawable.nurudeen_text)
            .addItem(Element().setTitle("Version ${BuildConfig.VERSION_NAME}"))
            .addGroup("Show your support")
            .addPlayStore(packageName, "Rate us on Play Store ")
            .addGroup("Connect with us")
            .addEmail(getString(R.string.my_email))
            .addWebsite(getString(R.string.my_website))
            .addFacebook(getString(R.string.my_fb_id))
            .addTwitter(getString(R.string.my_twitter_id))
            .addItem(getCopyRightsElement())
            .create()
        setContentView(aboutPage)
        setupToolbar()
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About Us"
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.color.colorPrimary
            )
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    }

    private fun getCopyRightsElement(): Element {
        val copyRightsElement = Element()
        val copyrights =
            "${getString(R.string.copy_right)} ${Calendar.getInstance().get(Calendar.YEAR)}"
        copyRightsElement.title = copyrights
        copyRightsElement.iconDrawable = R.drawable.ic_copyright_black_24dp
        copyRightsElement.iconTint = R.color.about_item_icon_color
        copyRightsElement.iconNightTint = android.R.color.white
        copyRightsElement.gravity = Gravity.CENTER
        copyRightsElement.setOnClickListener {
            Toast.makeText(this@AboutActivity, copyrights, Toast.LENGTH_SHORT).show()
        }
        return copyRightsElement
    }
}
