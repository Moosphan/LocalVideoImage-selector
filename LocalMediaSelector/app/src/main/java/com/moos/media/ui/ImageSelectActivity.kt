package com.moos.media.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.moos.media.R
import com.moos.media.entity.impl.ImageMediaEntity
import com.ucard.timeory.adapter.LocalImageAdapter
import com.ucard.timeory.utils.MediaUtils
import kotlinx.android.synthetic.main.activity_image_select.*
import java.lang.ref.WeakReference

class ImageSelectActivity : AppCompatActivity(), LocalImageAdapter.OnLocalImageSelectListener {


    companion object {
        const val GET_LOCAL_IMAGES: Int = 100
        /**
         * by moosphon on 2018/09/16
         * desc: 解决handler内存泄漏的问题，消息的处理需要放在内部类的{@link #Handler.handleMessage}
         */
        private class WithoutLeakHandler( mActivity: ImageSelectActivity) : Handler(){
            private var weakReference: WeakReference<ImageSelectActivity> = WeakReference(mActivity)

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    GET_LOCAL_IMAGES -> {
                        val activity = weakReference.get()

                        if (activity != null){
                            activity.adapter.setData(activity.imageData!!)
                            activity.rv_image.adapter = activity.adapter

                        }

                    }
                }
            }
        }
    }
    private var imageData: List<ImageMediaEntity>? = ArrayList()
    private var handler: Handler = WithoutLeakHandler(this)
    private val adapter = LocalImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)
        initView()
    }

    private fun initView() {
        setSupportActionBar(tb_image)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tb_image.setNavigationOnClickListener {
            onBackPressed()
        }
        getPermission()

        rv_image.layoutManager = GridLayoutManager(this, 3)
        rv_image.addItemDecoration(MediaItemDecoration(8, 3))
        adapter.listener = this


    }

    /** 获取存储权限 */
    private fun getPermission() {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        arrayOf( Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
            }else {
                //已经获取到存储权限了
                searchForLocalImages()
            }
        }else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
            searchForLocalImages()
        }

    }

    /**
     * by moosphon on 2018/09/15
     * desc: 搜索系统本地所有图片
     * use ContentResolver in {@link #MediaStore.Video} <br/>
     */
    private fun searchForLocalImages(){
        Thread(Runnable {
            imageData = MediaUtils.getLocalPictures(this)
            Log.e("ImageSelectActivity", "扫描本地图片的数量为->"+imageData?.size)
            val message= Message()
            message.what = GET_LOCAL_IMAGES
            handler.sendMessage(message)
        }).start()
    }


    override fun onDestroy() {
        super.onDestroy()
        /** 消除内存泄漏隐患 */
        handler.removeCallbacksAndMessages(null)
    }

    override fun onImageSelect(view: View, position: Int, images: List<String>) {
        Log.e("ImageSelectActivity", "当前选中的图片为->"+images.toString())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111){
            getPermission()
        }
    }


}
