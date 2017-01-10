package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.tzq.common.bean.PhotoAibum;
import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.ImageUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Drawing;
import com.tzq.maintenance.utis.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */

public class PhotoSelectLocalActivity extends BaseActivity {
    private GridView mGridView;
    PhotoAdapter mAdapter;
    ArrayList<String> mPaths = new ArrayList<>();
    boolean mTuzhiSys = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("选择图片");
        setContentView(R.layout.photo_grid_show_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTuzhiSys = getIntent().getBooleanExtra("tuzhiSys", false);

        mGridView = (GridView) findViewById(R.id.photo_gv);
        mAdapter = new PhotoAdapter(mAct);

        mGridView.setAdapter(mAdapter);

        if (mTuzhiSys) {
            List<Drawing> drawList = new Select().from(Drawing.class).where("management_id = " + App.getInstance().getUser().management_id).execute();
            if (!Util.isEmpty(drawList)) {
                for (Drawing drawing : drawList) {
                    mPaths.add(drawing.image_url);
                }
            }
        } else {
            List<PhotoAibum> l = ImageUtil.getPhotoAlbum(mAct);
            for (PhotoAibum pa : l) {
                if (Util.isEmpty(pa.bitList)) {
                    continue;
                }
                for (PhotoAibum.PhotoItem it : pa.bitList) {
                    mPaths.add(it.path);
                }
            }
        }

        mAdapter.setDataList(mPaths);
    }

    @Override
    public void onViewClick(View view) {

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

            vh.photoCb.setVisibility(View.INVISIBLE);
            vh.photoCover.setVisibility(View.INVISIBLE);
            final String path = getItem(position);
            if (mTuzhiSys) {
                MyUtil.displayPic(vh.photoIv, path);
            } else {
                MyUtil.displayPic(vh.photoIv, "file://" + path);
            }

            vh.photoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK, new Intent().putExtra("path", path));
                    finish();
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
