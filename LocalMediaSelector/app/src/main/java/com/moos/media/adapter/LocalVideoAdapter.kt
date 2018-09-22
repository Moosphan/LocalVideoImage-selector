package com.ucard.timeory.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.moos.media.R
import com.moos.media.entity.impl.VideoMediaEntity
import org.jetbrains.anko.find
import java.io.File

/**
 * <pre>
 *    author: moosphon
 *    date:   2018/09/16
 *    desc:   本地视频的适配器
 * <pre/>
 */
class LocalVideoAdapter: RecyclerView.Adapter<LocalVideoAdapter.LocalVideoViewHolder>() {
    lateinit var context: Context
    private var mSelectedPosition: Int = -1
    var listener: OnLocalVideoSelectListener? = null
    private lateinit var data: List<VideoMediaEntity>
    private var checkState: HashSet<Int> = HashSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalVideoViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_local_video_layout, parent, false)
        return LocalVideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LocalVideoViewHolder, position: Int) {
        val thumbnailImage: ImageView = holder.view.find(R.id.local_video_item_thumbnail)
        val checkBox: CheckBox = holder.view.find(R.id.local_video_item_cb)
        checkBox.isChecked = checkState.contains(position)
        val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)


        Glide.with(context)
                .asBitmap()
                .load(data[position].path)
                .apply(options)
                .thumbnail(0.2f)
                .into(thumbnailImage)
        checkBox.setOnClickListener {

            if (mSelectedPosition!=position){
                //先取消上个item的勾选状态
                checkState.remove(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                //设置新Item的勾选状态
                mSelectedPosition = position
                checkState.add(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
            }else if(checkBox.isChecked){
                checkState.add(position)

            }else if(!checkBox.isChecked){

                checkState.remove(position)
            }
            if (listener != null){
                listener!!.onVideoSelect(holder.view, position)

            }
        }
    }

    fun setData(data: List<VideoMediaEntity>){
        this.data = data
        for (i in 0 until data.size) {
            if (data[i].isSelected) {
                mSelectedPosition = i
            }
        }
    }





    class LocalVideoViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    /** 自定义的本地视频选择监听器 */
    interface OnLocalVideoSelectListener{
        fun onVideoSelect(view:View, position:Int)
    }

}