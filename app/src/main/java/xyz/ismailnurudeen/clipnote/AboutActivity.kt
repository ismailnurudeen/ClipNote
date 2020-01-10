package xyz.ismailnurudeen.clipnote

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_about.*
import xyz.ismailnurudeen.clipnote.utils.AppUtils

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        AppUtils(this).loadAbout(this, R.style.AppThemeLight)
        about_nav_back.setOnClickListener{
            finish()
        }
    }
}
