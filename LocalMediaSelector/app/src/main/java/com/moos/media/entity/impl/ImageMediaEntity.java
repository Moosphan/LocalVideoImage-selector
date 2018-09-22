package com.moos.media.entity.impl;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.moos.media.entity.BaseMediaEntity;

import java.io.File;

/**
 * image entity base on {@link BaseMediaEntity}
 *
 * @author moosphon
 */
public class ImageMediaEntity extends BaseMediaEntity {

    private static final long MAX_GIF_SIZE = 1024 * 1024L;
    private static final long MAX_IMAGE_SIZE = 1024 * 1024L;

    private String mThumbnailPath;
    private String mCompressPath;
    private int mHeight;
    private int mWidth;
    private IMAGE_TYPE mImageType;
    private String mMimeType;


    public enum IMAGE_TYPE {
        PNG, JPG, GIF
    }

    public ImageMediaEntity(String id, String imagePath) {
        super(id, imagePath);
    }

    public ImageMediaEntity(@NonNull File file) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.path = file.getAbsolutePath();
        this.size = String.valueOf(file.length());
    }

    public ImageMediaEntity(Builder builder) {
        super(builder.mImagePath, builder.mId);
        this.mThumbnailPath = builder.mThumbnailPath;
        this.size = builder.mSize;
        this.mHeight = builder.mHeight;
        this.mWidth = builder.mWidth;
        this.mMimeType = builder.mMimeType;
        this.mImageType = getImageTypeByMime(builder.mMimeType);
    }

    @Override
    public TYPE getMediaType() {
        return TYPE.IMAGE;
    }


    public boolean isGifOverSize() {
        return isGif() && Long.valueOf(getSize()) > MAX_GIF_SIZE;
    }

    public boolean isGif() {
        return getImageType() == IMAGE_TYPE.GIF;
    }


    /**
     * get mime type displayed in database.
     *
     * @return "image/gif" or "image/jpeg".
     */
    public String getMimeType() {
        if (getImageType() == IMAGE_TYPE.GIF) {
            return "image/gif";
        } else if (getImageType() == IMAGE_TYPE.JPG) {
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public IMAGE_TYPE getImageType() {
        return mImageType;
    }

    private IMAGE_TYPE getImageTypeByMime(String mimeType) {
        if (!TextUtils.isEmpty(mimeType)) {
            if ("image/gif".equals(mimeType)) {
                return IMAGE_TYPE.GIF;
            } else if ("image/png".equals(mimeType)) {
                return IMAGE_TYPE.PNG;
            } else {
                return IMAGE_TYPE.JPG;
            }
        }
        return IMAGE_TYPE.PNG;
    }

    public void setImageType(IMAGE_TYPE imageType) {
        mImageType = imageType;
    }

    public String getId() {
        return id;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public String getCompressPath() {
        return mCompressPath;
    }


    public void setCompressPath(String compressPath) {
        mCompressPath = compressPath;
    }

    public void setSize(String size) {
        size = size;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    @Override
    public String toString() {
        return "ImageMedia{" +
                ", mThumbnailPath='" + mThumbnailPath + '\'' +
                ", mCompressPath='" + mCompressPath + '\'' +
                ", mSize='" + size + '\'' +
                ", mHeight=" + mHeight +
                ", mWidth=" + mWidth;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @NonNull
    public String getThumbnailPath() {
        if (isFileValid(mThumbnailPath)) {
            return mThumbnailPath;
        } else if (isFileValid(mCompressPath)) {
            return mCompressPath;
        }
        return path;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageMediaEntity other = (ImageMediaEntity) obj;
        return !(TextUtils.isEmpty(path) || TextUtils.isEmpty(other.path)) && this.path.equals(other.path);
    }

    public static class Builder {
        private String mId;
        private String mImagePath;
        private String mThumbnailPath;
        private String mSize;
        private int mHeight;
        private int mWidth;
        private String mMimeType;

        public Builder(String id, String path) {
            this.mId = id;
            this.mImagePath = path;
        }

        public Builder setThumbnailPath(String thumbnailPath) {
            mThumbnailPath = thumbnailPath;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            mMimeType = mimeType;
            return this;
        }

        public Builder setSize(String size) {
            this.mSize = size;
            return this;
        }

        public ImageMediaEntity build() {
            return new ImageMediaEntity(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mThumbnailPath);
        dest.writeString(this.mCompressPath);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mImageType == null ? -1 : this.mImageType.ordinal());
        dest.writeString(this.mMimeType);
    }

    protected ImageMediaEntity(Parcel in) {
        super(in);
        this.mThumbnailPath = in.readString();
        this.mCompressPath = in.readString();
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
        int tmpMImageType = in.readInt();
        this.mImageType = tmpMImageType == -1 ? null : IMAGE_TYPE.values()[tmpMImageType];
        this.mMimeType = in.readString();
    }

    public static final Creator<ImageMediaEntity> CREATOR = new Creator<ImageMediaEntity>() {
        @Override
        public ImageMediaEntity createFromParcel(Parcel source) {
            return new ImageMediaEntity(source);
        }

        @Override
        public ImageMediaEntity[] newArray(int size) {
            return new ImageMediaEntity[size];
        }
    };

    private boolean isFileValid(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return isFileValid(file);
    }

    boolean isFileValid(File file) {
        return file != null && file.exists() && file.isFile() && file.length() > 0 && file.canRead();
    }



}
