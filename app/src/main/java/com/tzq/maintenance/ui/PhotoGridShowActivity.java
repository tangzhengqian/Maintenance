package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.ImageUtil;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.R;
import com.tzq.maintenance.utis.MyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tzq.maintenance.Config.photoDirPath;

/**
 * Created by Administrator on 2016/11/2.
 */

public class PhotoGridShowActivity extends BaseActivity {
    private final static int REQUEST_ADD_PIC = 11;
    private final static int REQUEST_TAKE_PHOTO = 12;
    private GridView mGridView;
    PhotoAdapter mAdapter;
    ArrayList<String> mUris = new ArrayList<>();
    List<Integer> mSelectPostion = new ArrayList<>();
    boolean mEditable = true;
    boolean mTuzhiSys = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("图片展示");
        setContentView(R.layout.photo_grid_show_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGridView = (GridView) findViewById(R.id.photo_gv);
        mAdapter = new PhotoAdapter(mAct);

        mGridView.setAdapter(mAdapter);

        mUris = getIntent().getStringArrayListExtra("uris");
        mEditable = getIntent().getBooleanExtra("editable", true);
        mTuzhiSys = getIntent().getBooleanExtra("tuzhiSys", false);
        mAdapter.setDataList(mUris);
    }

    @Override
    public void onViewClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mEditable) {
            return true;
        }

        getMenuInflater().inflate(R.menu.photo_grid_show_activity, menu);
        MenuItem takePhoto = menu.findItem(R.id.action_take_photo);
        takePhoto.setVisible(!mTuzhiSys);
        MenuItem delete = menu.findItem(R.id.action_delete);
        if (mSelectPostion.size() > 0) {
            delete.setEnabled(true);
            delete.setTitle("删除(" + mSelectPostion.size() + ")");
        } else {
            delete.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog.Builder(mAct).setMessage("确定要删除所选图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<String> newUris = new ArrayList<String>();
                    for (int i = 0; i < mUris.size(); i++) {
                        if (!mSelectPostion.contains(Integer.valueOf(i))) {
                            newUris.add(mUris.get(i));
                        }
                    }
                    mSelectPostion.clear();
                    mUris.clear();
                    mUris.addAll(newUris);
                    mAdapter.setDataList(mUris);
                    invalidateOptionsMenu();
                }
            }).setNegativeButton("取消", null).show();
        } else if (item.getItemId() == R.id.action_add_pic) {
            startActivityForResult(new Intent(mAct, PhotoSelectLocalActivity.class).putExtra("tuzhiSys", mTuzhiSys), REQUEST_ADD_PIC);
        } else if (item.getItemId() == R.id.action_take_photo) {
            openTakePhoto();
        } else if (item.getItemId() == R.id.action_complete) {
            setResult(RESULT_OK, new Intent().putStringArrayListExtra("uris", mUris));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PIC) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                LogUtil.i("---path=" + path);
                if (path.startsWith("/")) {
                    path = "file://" + path;
                }
                mUris.add(path);
                mAdapter.setDataList(mUris);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (data != null) { //防止没有返回结果
                Uri uri = data.getData();
                Bitmap photo = null;
                if (uri != null) {
                    photo = BitmapFactory.decodeFile(uri.getPath()); //拿到图片
                }
                if (photo == null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        photo = (Bitmap) bundle.get("data");
                    } else {
                        Toast.makeText(getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
                    }
                }
                if (photo != null) {
                    File dir = new File(photoDirPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String path = dir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                    ImageUtil.saveBitmapToLocal(photo, path, Bitmap.CompressFormat.JPEG);
                    LogUtil.i("---path=" + path);
                    if (path.startsWith("/")) {
                        path = "file://" + path;
                    }
                    mUris.add(path);
                    mAdapter.setDataList(mUris);
                }

            }
        }
    }

    private void openTakePhoto() {
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) {   //如果可用
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            MyUtil.toast("sdcard不可用");
        }
    }

    class PhotoAdapter extends CBaseAdapter<String> {

        public PhotoAdapter(Context c) {
            super(c);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            VH vh = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.photo_grid_item, null);
                vh = new VH();
                vh.photoIv = (ImageView) convertView.findViewById(R.id.photo_iv);
                vh.photoCover = convertView.findViewById(R.id.photo_cover_v);
                vh.photoCb = (CheckBox) convertView.findViewById(R.id.photo_selected_cb);
                convertView.setTag(vh);
            } else {
                vh = (VH) convertView.getTag();
            }

            String uri = getItem(position);
            MyUtil.displayPic(mAct, vh.photoIv, uri);
            if (mSelectPostion.contains(position)) {
                vh.photoCover.setVisibility(View.VISIBLE);
                vh.photoCb.setChecked(true);
            } else {
                vh.photoCover.setVisibility(View.GONE);
                vh.photoCb.setChecked(false);
            }
            vh.photoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mAct, PhotoViewPagerActivity.class).putExtra("cur", position).putStringArrayListExtra("urls", mUris));
                }
            });
            vh.photoCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && !mSelectPostion.contains(position)) {
                        mSelectPostion.add(position);
                    } else {
                        mSelectPostion.remove(Integer.valueOf(position));
                    }
                    notifyDataSetChanged();
                    invalidateOptionsMenu();
                }
            });
            return convertView;
        }

        class VH {
            public ImageView photoIv;
            public View photoCover;
            public CheckBox photoCb;
        }
    }

}
