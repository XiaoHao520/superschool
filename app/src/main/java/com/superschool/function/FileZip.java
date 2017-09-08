package com.superschool.function;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by XIAOHAO on 2017/3/13.
 */
public class FileZip {
    String zipFileFullName;
    ZipOutputStream zipOutputStream;
    BufferedOutputStream bos;
    ArrayList<String> files;

    public FileZip(ArrayList<String> files, String zipFileFullName) {
        this.files = files;
        this.zipFileFullName = zipFileFullName;
    }


    public void zip() throws IOException {
        bos = new BufferedOutputStream(new FileOutputStream(zipFileFullName));
        zipOutputStream = new ZipOutputStream(bos);
        byte[] buf=new byte[1024*files.size()];
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            if (!file.isFile()) continue;
            ZipEntry ze = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(ze);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            int len;
            while ((len = bis.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, len);
            }
            zipOutputStream.closeEntry();
        }
        zipOutputStream.closeEntry();

        zipOutputStream.close();
        bos.close();
    }
}
