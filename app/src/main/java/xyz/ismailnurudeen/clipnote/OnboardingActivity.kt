package xyz.ismailnurudeen.clipnote

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_onboarding.*
import xyz.ismailnurudeen.clipnote.utils.PrefsManager

class OnboardingActivity : AppCompatActivity() {

    private var layouts: IntArray? = null
    private lateinit var prefManager: PrefsManager
    private var introPagerAdapter: IntroPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Checking for first time launch - before calling setContentView()
        prefManager = PrefsManager(this)
        if (!prefManager.isFirstTimeLaunch) {
            launchHomeScreen()
            finish()
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_onboarding)

        btn_prev!!.visibility = View.INVISIBLE
        // layouts of all welcome sliders
        layouts = intArrayOf(
            R.layout.intro_slider1,
            R.layout.intro_slider2,
            R.layout.intro_slider3
        )

        // adding bottom dots
        addBottomDots(0)

        // making notification bar transparent
        changeStatusBarColor()

        introPagerAdapter = IntroPagerAdapter()
        onboarding_view_pager!!.adapter = introPagerAdapter
        onboarding_view_pager!!.addOnPageChangeListener(viewPagerPageChangeListener)

        btn_skip!!.setOnClickListener { launchHomeScreen() }

        btn_next!!.setOnClickListener {
            // checking for last page
            val current = getItem(+1)
            if (current < layouts!!.size) {
                // move to next screen
                onboarding_view_pager!!.currentItem = current
                btn_prev!!.visibility = View.VISIBLE
            } else {
                launchHomeScreen()
            }
        }

        btn_prev!!.setOnClickListener {
            // checking for first page
            val current = getItem(-1)
            if (current > 0) {
                // move to previous screen
                onboarding_view_pager!!.currentItem = current
            } else {
                btn_prev!!.visibility = View.INVISIBLE
            }
        }
    }

    private fun getItem(i: Int): Int {
        return onboarding_view_pager!!.currentItem + i
    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts!!.size)

        val colorActive = ContextCompat.getColor(this, R.color.dot_active)
        val colorInactive = ContextCompat.getColor(this, R.color.dot_inactive)

        layoutDots!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 30f
            dots[i]?.setTextColor(colorInactive)
            layoutDots!!.addView(dots[i])
        }

        if (dots.isNotEmpty())
            dots[currentPage]!!.setTextColor(colorActive)
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
        finish()
    }

    private fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                when (position) {
                    layouts?.size?.minus(1) -> {
                        // last page. change button text to GOT IT
                        btn_next?.text = getString(R.string.got_it)
                        btn_skip?.visibility = View.GONE
                    }

                    0 -> btn_prev?.visibility = View.GONE

                    else -> {
                        // still pages are left
                        btn_next?.text = getString(R.string.next)
                        btn_next?.visibility = View.VISIBLE
                        btn_skip?.visibility = View.VISIBLE
                        btn_prev?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        }

    inner class IntroPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(view)

            return view
        }

        override fun getCount(): Int = layouts!!.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}

