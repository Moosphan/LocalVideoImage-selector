package com.ucard.timeory.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.moos.media.R
import com.moos.media.entity.impl.ImageMediaEntity
import org.jetbrains.anko.find

/**
 * <pre>
 *    author: moosphon
 *    date:   2018/09/16
 *    desc:   本地视频的适配器
 * <pre/>
 */
class LocalImageAdapter: RecyclerView.Adapter<LocalImageAdapter.LocalImageViewHolder>() {
    lateinit var context: Context
    private var mSelectedPosition: Int = 0
    var listener: OnLocalImageSelectListener? = null
    private lateinit var data: List<ImageMediaEntity>
    /** 存储选中的图片 */
    private var chosenImages : HashMap<Int, String>  = HashMap()
    /** 存储选中的状态 */
    private var checkStates  : HashMap<Int, Boolean> = HashMap()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalImageViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_local_video_layout, parent, false)
        return LocalImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LocalImageViewHolder, position: Int) {
        val thumbnailImage: ImageView = holder.view.find(R.id.local_video_item_thumbnail)
        val checkBox: CheckBox = holder.view.find(R.id.local_video_item_cb)
        /** 通过map存储checkbox选中状态,放置rv复用机制导致的状态混乱状态 */
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = checkStates.containsKey(position)
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)

        Glide.with(context)
                .asBitmap()
                .load(data[position].thumbnailPath)
                .apply(options)
                .thumbnail(0.2f)
                .into(thumbnailImage)
        checkBox.setOnCheckedChangeListener{
            _, isChecked ->
            if (isChecked){
                checkStates[position] = true

                data[position].isSelected = true
                // 将当前选中的图片存入map
                chosenImages[position] = data[position].path

            }else{
                // 从选中列表中移除
                checkStates.remove(position)
                chosenImages.remove(position)
            }
            if (listener != null){
                val selectedImages  = ArrayList<String>()
                for (v in chosenImages.values){
                    selectedImages.add(v)
                }
                listener!!.onImageSelect(holder.view, position, selectedImages)

            }
        }


    }

    fun setData(data: List<ImageMediaEntity>){
        this.data = data
        for (i in 0 until data.size) {
            if (data[i].isSelected) {
                mSelectedPosition = i
            }
        }
    }



    class LocalImageViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    /** 自定义的本地视频选择监听器 */
    interface OnLocalImageSelectListener{
        fun onImageSelect(view: View, position:Int, images: List<String>)
    }

}