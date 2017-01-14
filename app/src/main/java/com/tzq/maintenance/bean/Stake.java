package com.tzq.maintenance.bean;

import com.tzq.common.utils.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/1/5.
 */

public class Stake implements Serializable {
    public int id;
    public String check_id;
    public String stake_ud;//桩号
    public String stake_num1;//
    public String stake_num2;//

    public String before_pic;
    public String construction_pic;
    public String after_pic;
    public ArrayList<String> check_before_pic;

    private List<String> beforePicUris;//just for cache
    private ArrayList<String> newBeforePicUris;//just offline save
    private List<String> constructionPics;//just for cache
    private ArrayList<String> newConstructionPics;//just offline save
    private List<String> afterPics;//just for cache
    private ArrayList<String> newAfterPics;//just offline save

    public String uuid = UUID.randomUUID().toString();

    public Stake() {
    }

    public List<String> getBeforePics() {
        if (!Util.isEmpty(beforePicUris)) {
            return beforePicUris;
        }

        if (beforePicUris == null) {
            beforePicUris = new ArrayList<>();
        }
        if (!Util.isEmpty(before_pic)) {
            String[] urls = before_pic.split(",");
            for (String s : urls) {
                beforePicUris.add(s);
            }
        }
        return beforePicUris;
    }

    public ArrayList<String> getBeforeNewPics() {
        if (newBeforePicUris != null) {
            return newBeforePicUris;
        }
        newBeforePicUris = new ArrayList<>();
        if (Util.isEmpty(getBeforePics())) {
            if (!Util.isEmpty(check_before_pic)) {
                newBeforePicUris.addAll(check_before_pic);
            }
        } else {
            newBeforePicUris.addAll(getBeforePics());
        }
        return newBeforePicUris;
    }

    public List<String> getConstructionPics() {
        if (!Util.isEmpty(constructionPics)) {
            return constructionPics;
        }

        if (constructionPics == null) {
            constructionPics = new ArrayList<>();
        }
        if (!Util.isEmpty(construction_pic)) {
            String[] urls = construction_pic.split(",");
            for (String s : urls) {
                constructionPics.add(s);
            }
        }
        return constructionPics;
    }

    public ArrayList<String> getConstructionNewPics() {
        if (newConstructionPics != null) {
            return newConstructionPics;
        }

        newConstructionPics = new ArrayList<>();
        newConstructionPics.addAll(getConstructionPics());
        return newConstructionPics;
    }

    public List<String> getAfterPics() {
        if (!Util.isEmpty(afterPics)) {
            return afterPics;
        }

        if (afterPics == null) {
            afterPics = new ArrayList<>();
        }
        if (!Util.isEmpty(after_pic)) {
            String[] urls = after_pic.split(",");
            for (String s : urls) {
                afterPics.add(s);
            }
        }

        return afterPics;
    }

    public ArrayList<String> getAfterNewPics() {
        if (newAfterPics != null) {
            return newAfterPics;
        }

        newAfterPics = new ArrayList<>();
        newAfterPics.addAll(getAfterPics());
        return newAfterPics;
    }
}
