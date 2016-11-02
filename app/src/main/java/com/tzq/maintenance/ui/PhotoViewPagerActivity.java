package com.tzq.maintenance.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzq.maintenance.R;
import com.tzq.maintenance.utis.MyUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/11/2.
 */

public class PhotoViewPagerActivity extends BaseActivity {
    private static final int BASE_SYSTEM_UI_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    ViewPager mViewPager;
    ViewPagerPhotoAdapter mAdapter;
    List<String> mUrlList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("图片浏览");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.photo_viewpager_activity);
        mViewPager = (ViewPager) findViewById(R.id.photo_vp);

        mUrlList = getIntent().getStringArrayListExtra("urls");
        int curPostion = getIntent().getIntExtra("cur", 0);
        mAdapter = new ViewPagerPhotoAdapter(mAct);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(curPostion);
    }

    @Override
    public void onViewClick(View view) {

    }


    private PhotoViewAttacher.OnViewTapListener mPhotoTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }
            int showFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            if (actionBar.isShowing()) {
                getWindow().getDecorView().setSystemUiVisibility(showFlag | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN);
//                actionBar.hide();

            } else {
                getWindow().getDecorView().setSystemUiVisibility(showFlag);
//                actionBar.show();

            }
        }
    };

    private class ViewPagerPhotoAdapter extends PagerAdapter {
        private Context mContext;

        public ViewPagerPhotoAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photo = new PhotoView(mAct);
            photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photo.setOnViewTapListener(mPhotoTapListener);
            container.addView(photo);

            MyUtil.displayLargePic(photo, mUrlList.get(position));
            return photo;
        }

        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getItemId(int position) {
            return position;
        }
    }
}
