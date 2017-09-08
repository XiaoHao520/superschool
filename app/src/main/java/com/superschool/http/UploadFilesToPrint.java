package com.superschool.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/5/8.
 */

public class UploadFilesToPrint {
   static String url;
    Map<String, String> customerInfo;

    public UploadFilesToPrint(Map<String, String> customerInfo, String url) {
        this.customerInfo = customerInfo;
        this.url = url;
    }

    public String send() {
        HashMap<String, String> textMap = new HashMap<String, String>();
        return HttpUrlConnectionUtil2.request(url, null, (HashMap<String, String>) customerInfo);
    }
}
