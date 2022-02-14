package com.tulasimultispecialityhospital

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.tulasimultispecialityhospital.models.AlbumModel
import java.util.ArrayList

class ZoomImageViewActivity : AppCompatActivity() {

    private var position: Int = 0
    private var imagesViewPager: ViewPager? = null
    private var mPagerAdapter: ZoomPagerAdapter? = null
    private var imagesArrayList: ArrayList<AlbumModel>? = null
    private var imagesCountTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_image_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        if (intent.extras != null) {
            // imageUrl = intent.extras.getString("image_url")
            position = intent.extras.getInt("image_pos")
            imagesArrayList = intent.getSerializableExtra("images_array") as ArrayList<AlbumModel>
        }
        imagesViewPager = findViewById(R.id.images_viewpager) as ViewPager
        imagesCountTextView = findViewById(R.id.images_count_text) as TextView
        //   imagesArrayList = new ArrayList<String>();
        mPagerAdapter = imagesArrayList?.let { ZoomPagerAdapter(supportFragmentManager, it) }
        imagesViewPager!!.setAdapter(mPagerAdapter)

        imagesViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (imagesArrayList!!.size > 0)
                    imagesCountTextView!!.setText((position + 1).toString() + "/" + imagesArrayList!!.size)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {


            }
        })
        imagesViewPager!!.setCurrentItem(position)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private inner class ZoomPagerAdapter(
        fm: FragmentManager,
        private val pramotionsArrayList: ArrayList<AlbumModel>
    ) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return this.pramotionsArrayList.size
        }

        override fun getItem(position: Int): Fragment {
            val fragment = ZoomImagesFragment()
            val args = Bundle()
            args.putString("image_url", this.pramotionsArrayList[position].orginal_image)
            args.putInt("image_pos", position)
            fragment.setArguments(args)
            return fragment
        }
    }

}