package com.jing.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Whyn
 * @date 2020/2/24 17:06
 */
public class FdfsUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        StringBuilder url = new StringBuilder("192.168.88.128");
        if (null == multipartFile)
            return String.valueOf(url);
        String file = FdfsUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(file);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient = new StorageClient(trackerServer, null);
        String originalFilename = multipartFile.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            String[] upload_file = storageClient.upload_file(multipartFile.getBytes(), extName, null);
            for (int i = 0; i < upload_file.length; i++) {
                url.append("/").append(upload_file[i]);
            }
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return String.valueOf(url);
    }
}
