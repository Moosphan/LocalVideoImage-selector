package com.moos.media.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.moos.media.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_image_select.setOnClickListener {
            startActivity(intentFor<ImageSelectActivity>())
        }

        btn_video_select.setOnClickListener {
            startActivity(intentFor<VideoSelectActivity>())
        }
    }
}
