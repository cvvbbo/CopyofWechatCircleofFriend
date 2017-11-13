package com.circlefriend2.xz.util;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;


public class ImageItem implements Serializable {
	public String imageId;

	//这个叫缩略图的地址
	public String thumbnailPath;
	public String imagePath;
	private Bitmap bitmap;
	public boolean isSelected = false;
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Bitmap getBitmap() {		
		if(bitmap == null){
			try {
				//下面这个是压缩图片的方法，属于自己写的
				bitmap = Bimp.revitionImageSize(imagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
	
}
