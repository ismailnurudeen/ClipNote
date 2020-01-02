package xyz.ismailnurudeen.clipnote

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import xyz.ismailnurudeen.clipnote.utils.AppUtils

class AboutActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
//        setupToolbar()
        AppUtils(this).loadAbout(this, R.style.AppThemeLight)
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.color.colorPrimary
            )
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    }
}
