package com.circlefriend2.xz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.circlefriend2.xz.R;
import com.circlefriend2.xz.util.Bimp;
import com.circlefriend2.xz.util.FileUtils;
import com.circlefriend2.xz.util.ImageItem;
import com.circlefriend2.xz.util.PublicWay;
import com.circlefriend2.xz.util.Res;



/**
 * 首页面activity
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:34
 */
public class MainActivity extends Activity {


	/***
	 * Note:
	 * 1.这个demo有个问题就是，照的照片在缩略图中点开来还是压缩过的图片，但是如果是直接从相册中获取的得到的图片，然后点开
	 * 就是高清没有压缩过的图片！！
	 *
	 * 2.还有个bug就是当如果第一张照片是拍的，然后点击进入大图模式之后再次点击会直接崩溃
	 *
	 * 3.这个demo拍的照片在相册中找不到
	 *
	 */

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap ;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Res.init(this);
//		bimap = BitmapFactory.decodeResource(
//				getResources(),
//				R.drawable.icon_addpic_unfocused);

		//这个是存放Activity的地址的
		PublicWay.activityList.add(this);
		//这个是吧外面的布局添加进来
		parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
		setContentView(parentView);
		Init();
	}

	public void Init() {

		//显示弹窗
		pop = new PopupWindow(MainActivity.this);
		
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
		//getLayoutInflater().inflate(com.circlefriend2.xz.R.layout.)

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);


		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);

		//这个是点击pop
		parent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});


		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				//开始照相
				photo();

				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});


		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				//这个是从相册中获取


				//这里的相册是某个相册详情界面的相册（就是许多个相册分类里面点开了某个具体的相册）
				Intent intent = new Intent(MainActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				//这个是Activity的进入动画
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});


		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);	
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);

//		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//			}
//		});


		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			//从0的位置开始点
			public void onItemClick(AdapterView<?> adapterView, View view, int i,
					long l) {
				if (i == Bimp.tempSelectBitmap.size()) {
					Log.e("grideview点击的位置", "----------"+i);
					ll_popup.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.activity_translate_in));
					//布局传进来
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {

                    //这个不是打开相册那个类，而是小图点击打开变成大图的那个类。11.11
					Intent intent = new Intent(MainActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", i);
					startActivity(intent);
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if(Bimp.tempSelectBitmap.size() == 9){
				return 9;
			}
			//因为另一张图占了一个位置（就是哪个加号的图片，加号的图是在gridview容器里面的）
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

//		public void setSelectedPosition(int position) {
//			selectedPosition = position;
//		}
//
//		public int getSelectedPosition() {
//			return selectedPosition;
//		}

		public View getView(int position, View convertView, ViewGroup parent) {
			int imagesize=Bimp.tempSelectBitmap.size();
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position ==Bimp.tempSelectBitmap.size()) {
                Log.e("==展示图片的位置",position+" ");
				Log.e("图片集合的大小",imagesize+" ");
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						//这个是那个加号
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				Log.e("我执行了么==展示图片的位置",position+" ");
				Log.e("我执行了么+图片集合的大小",imagesize+" ");
				Log.e("haha", Environment.getExternalStorageDirectory().getAbsolutePath());

				//这里展示图片（无论是相册还是相机最后都是会走这个方法）
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}


		//这里是接受消息的
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};


		//如果把handler去掉了，那么拍照的时候是不会更新界面的！！

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;


	//开启摄像头
	public void photo() {

		//这里拍的是缩略图
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			//开启摄像头
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
				
				String fileName = String.valueOf(System.currentTimeMillis());
				//这里获得的是缩略图
				Bitmap bm = (Bitmap) data.getExtras().get("data");

				//保存图片文件11.11，就是制定了一个文件名，
				FileUtils.saveBitmap(bm, fileName);

				//这个是序列化对象，但是是为什么啊？？
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				//然后把序列化对象的内容放到这里来
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for(int i=0;i<PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			System.exit(0);
		}
		return true;
	}

	//最后要把handler结束掉，不然的化会demo卡死

}

