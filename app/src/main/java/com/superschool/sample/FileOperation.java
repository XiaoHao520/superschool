package com.superschool.sample;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.superschool.entity.FileInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by XIAOHAO on 2017/6/9.
 */

public class FileOperation {
    private OSS oss;

    // 运行sample前需要配置以下字段为有效的值
    private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String accessKeyId = "LTAIFkFByfa88IwM";
    private static final String accessKeySecret = "MrP2UonJIfsCer5c7IKUaqX2jmpuzV";
    private static String uploadFilePath;
    private static final String bucket = "xiaohaobucket";
    private static String uploadFileName = null;
    private static final String downloadFileName = null;
    private static FileInfo fileInfo;
    private static List<FileInfo> fileInfoList;
    Context context;

    public FileOperation(Context context) {
        this.context = context;
    }

    public FileOperation(Context context, List<FileInfo> fileInfoList) {
        this.context = context;
        this.fileInfoList = fileInfoList;
    }

    public FileOperation(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
    }


    public boolean filesUpload() throws ClientException, ServiceException {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
        for (int i = 0; i < fileInfoList.size(); i++) {
            FileInfo fileInfo = fileInfoList.get(i);
            uploadFileName = fileInfo.getFilename();
            uploadFilePath = fileInfo.getFilePath();
            OSSAsyncTask task = new PutObjectSamples(oss, bucket, uploadFileName, uploadFilePath).asyncPutObjectFromLocalFile();
            if (task.getResult().getStatusCode() == 200) {
                System.out.println(i + "个成功");
                //  return true;
            }
        }
        return true;
    }

    public boolean fileUpload() throws ClientException, ServiceException {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
        uploadFileName = fileInfo.getFilename();
        uploadFilePath = fileInfo.getFilePath();
        OSSAsyncTask task = new PutObjectSamples(oss, bucket, uploadFileName, uploadFilePath).asyncPutObjectFromLocalFile();
        if (task.getResult().getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    public boolean filesDownload(String url) throws FileNotFoundException, ClientException, ServiceException {

        String[] files = url.split("/");
        String filePath = "/" + files[3] + "/" + files[4];
        //创建文件夹
        String downloadPath = context.getExternalCacheDir() + "/" + files[3];
        File file = new File(downloadPath);
        if (!file.exists()) {
            file.mkdir();
        }
        final FileOutputStream fos = new FileOutputStream(context.getExternalCacheDir() + filePath);

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
        GetObjectRequest get = new GetObjectRequest(bucket, files[3]+"/"+files[4]);

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("asyncGetObjectSample", "read length: " + len);
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    Log.d("asyncGetObjectSample", "download success.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        if(task.getResult().getStatusCode()==200){
            return true;
        }

        return false;

    }


}
