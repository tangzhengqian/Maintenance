package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;

import com.tzq.common.ui.CBaseAdapter;
import com.tzq.maintenance.R;
import com.tzq.maintenance.utis.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */

public class PhotoGridShowActivity extends BaseActivity {
    private GridView mGridView;
    PhotoAdapter mAdapter;
    ArrayList<String> mUrls = new ArrayList<>();
    List<Integer> mSelectPostion = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("图片展示");
        setContentView(R.layout.photo_grid_show_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGridView = (GridView) findViewById(R.id.photo_gv);
        mAdapter = new PhotoAdapter(mAct);

        mGridView.setAdapter(mAdapter);

        mUrls = getIntent().getStringArrayListExtra("urls");
        mAdapter.setDataList(mUrls);
    }

    @Override
    public void onViewClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.photo_grid_show_activity, menu);
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
        if (item.getItemId() == 0) {

        }
        return super.onOptionsItemSelected(item);
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

            String url = getItem(position);
            MyUtil.displayPic(vh.photoIv, url);
            if (mSelectPostion.contains(position)) {
                vh.photoCover.setVisibility(View.VISIBLE);
            } else {
                vh.photoCover.setVisibility(View.GONE);
            }
            vh.photoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mAct, PhotoViewPagerActivity.class).putExtra("cur", position).putStringArrayListExtra("urls", mUrls));
                }
            });
            vh.photoCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
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
