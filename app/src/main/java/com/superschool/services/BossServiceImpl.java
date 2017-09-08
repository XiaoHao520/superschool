package com.superschool.services;

import com.superschool.entity.BossInfo;
import com.superschool.http.HttpUrlConnectionUtil;

import java.util.HashMap;

/**
 * Created by XIAOHAO on 2017/5/26.
 */

public class BossServiceImpl implements BossServices {
    @Override
    public boolean applyBoss(BossInfo bossInfo) {
        //申请成为boss
        String url = "http://ganxiaohao.gicp.net/bossRegisterAction.action";
        HashMap<String, String> bossMap = new HashMap<String, String>();
        bossMap.put("bossUserID", bossInfo.getBossUserID());
        bossMap.put("storeName", bossInfo.getStoreName());
        bossMap.put("bossIdType", bossInfo.getBossIdType());
        bossMap.put("bossId", bossInfo.getBossId());
        bossMap.put("school", bossInfo.getSchool());
        bossMap.put("storeType", bossInfo.getStoreType());
        bossMap.put("storeInfo", bossInfo.getStoreInfo());
        bossMap.put("storeID", bossInfo.getStoreID());
        bossMap.put("storeLatitude",bossInfo.getStoreLatitude());
        bossMap.put("storeLongitude",bossInfo.getStoreLongitude());
        HttpUrlConnectionUtil.request(url, null,bossMap);
        return false;
    }
}
