package com.ucard.timeory.utils

import android.provider.MediaStore
import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.moos.media.entity.impl.ImageMediaEntity
import com.moos.media.entity.impl.VideoMediaEntity
import java.util.ArrayList





/*
 * Copyright (C) 2018 moosphon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class MediaUtils {
    /**
     * Get or deal with the media data of current phone.
     * more details visit framework document.
     */

    companion object {

        private var mThumbnailMap: Map<String, String>? = null

        /**
         * <pre>
         *     @author moosphon  (about me: <a>https://github.com/Moosphan<a/>)
         *     @date   2018/09/16
         *     @desc   get the origin path for local pictures.
         * <pre/>
         */
        fun getPicturePath(resolver: ContentResolver, uri: Uri?): String? {
            if (uri == null) {
                return null
            }

            if (SCHEME_CONTENT == uri.getScheme()) {
                var cursor: Cursor? = null
                try {
                    cursor = resolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                    return if (cursor == null || !cursor.moveToFirst()) {
                        null
                    } else cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                } finally {
                    if (cursor != null) {
                        cursor.close()
                    }
                }
            }
            return uri.getPath()
        }

        /**
         * <pre>
         *     @author moosphon  (about me: <a>https://github.com/Moosphan<a/>)
         *     @date   2018/09/16
         *     @desc   get all pictures of the phone.
         * <pre/>
         */
        fun getLocalPictures(mContext: Context?): List<ImageMediaEntity>? {
            val images = ArrayList<ImageMediaEntity>()
            val resolver = mContext?.contentResolver
            var cursor: Cursor? = null
            queryImageThumbnails(resolver!!, arrayOf(MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA))
            try {
                cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.ImageColumns.DATA,
                                MediaStore.Images.ImageColumns._ID,
                                MediaStore.Images.ImageColumns.SIZE,
                                MediaStore.Images.ImageColumns.MIME_TYPE),
                        null, null, null)
                return if (cursor == null || !cursor.moveToFirst()) {
                    null
                } else {
                    do {
                        val picPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        val id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                        val size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                        val image = ImageMediaEntity.Builder(id, picPath)
                                .setMimeType(mimeType)
                                .setSize(size)
                                .setThumbnailPath(mThumbnailMap?.get(id))
                                .build()
                        //images.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)))
                        images.add(image)
                        mThumbnailMap = null
                    }while (cursor.moveToNext())

                    return images
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }

        /**
         * search for thumbnails for local images
         *
         * @author moosphon
         */
        private fun queryImageThumbnails(cr: ContentResolver, projection: Array<String>) {
            var cur: Cursor? = null
            try {
                cur = MediaStore.Images.Thumbnails.queryMiniThumbnails(cr, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Thumbnails.MINI_KIND, projection)
                if (cur != null && cur.moveToFirst()) {
                    do {
                        val imageId = cur.getString(cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID))
                        val imagePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA))
                        mThumbnailMap = mapOf(imageId to imagePath)
                    } while (cur.moveToNext() && !cur.isLast)
                }
            } finally {
                cur?.close()
            }
        }


        /**
         * <pre>
         *     @author moosphon  (about me: <a>https://github.com/Moosphan<a/>)
         *     @date   2018/09/16
         *     @desc   get all videos of the phone.
         * <pre/>
         */
        fun getLocalVideos(mContext: Context?) : List<VideoMediaEntity>?{
            val videos = ArrayList<VideoMediaEntity>()
            val resolver = mContext?.contentResolver
            var cursor: Cursor? = null
            try {
                cursor = resolver?.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.ImageColumns.DATA,
                                MediaStore.Video.Media._ID,
                                MediaStore.Video.Media.DISPLAY_NAME,
                                MediaStore.Video.Media.RESOLUTION,
                                MediaStore.Video.Media.SIZE,
                                MediaStore.Video.Media.DURATION,
                                MediaStore.Video.Media.DATE_MODIFIED),
                        MediaStore.Video.Media.MIME_TYPE + "=?", arrayOf("video/mp4"), null)
                return if (cursor == null || !cursor.moveToFirst()) {
                    null
                } else {
                    while (cursor.moveToNext()){
                        // video path
                        val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                        // video id
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        // video display name
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                        // video resolution
                        val resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION))
                        // video size
                        val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                        // video duration
                        val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                        val date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED))

                        val video = VideoMediaEntity.Builder(id.toString(), path)
                                .setTitle(name)
                                .setDateTaken(date.toString())
                                .setDuration(duration.toString())
                                .setSize(size.toString())
                                .build()
                        videos.add(video)
                    }

                    return videos
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }

        /**
         * get video thumbnail image by using [ContentResolver]
         *
         * @param id video id
         *
         * @author moosphon
         */
        private fun getVideoThumbnailById(context: Context?, id: Long): String?{

            //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
            //MediaStore.Video.Thumbnails.getThumbnail(context!!.contentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null)
            val projection = arrayOf(MediaStore.Video.Thumbnails.DATA,
                    MediaStore.Video.Thumbnails.VIDEO_ID)
            val thumbCursor = context?.contentResolver!!.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection, MediaStore.Video.Thumbnails.VIDEO_ID
                    + "=" + id, null, null)
            var thumbnailUri = ""
            while (thumbCursor.moveToFirst()){
                thumbnailUri = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
            }
            thumbCursor.close()
            return thumbnailUri
        }


    }



}