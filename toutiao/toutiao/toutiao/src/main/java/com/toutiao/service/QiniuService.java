package com.toutiao.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.toutiao.util.ToutiaoUtil;

@Service
public class QiniuService {
		   private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
		   //以下三行根据七牛云账号要改变
		   private static final  String QINIU_IMAGE_DOMAIN = "http://7xsetu.com1.z0.glb.clouddn.com/";
			//设置好账号的ACCESS_KEY和SECRET_KEY
		    String ACCESS_KEY = "abNXnXBIlI6viRaOeRY6Hk-zc3V-NpjLcGfYz5kD";
		    String SECRET_KEY = "QP7Xja3FmP1Zyl-oxwQDCb7T6wCoEFKoO-0vht_5";   
		    //要上传的空间
		    String bucketname = "nowcoder";
		    //密钥配置
		    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		    //创建上传对象
		    UploadManager uploadManager = new UploadManager();
		    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
		    public String getUpToken() {
		        return auth.uploadToken(bucketname);
		    }

		    public String uploadImage(MultipartFile image) throws IOException {
		        try {
		            int dotPos = image.getOriginalFilename().lastIndexOf(".");
		            if (dotPos < 0) {
		                return null;
		            }
		            String suffix = image.getOriginalFilename().substring(dotPos + 1).toLowerCase();
		            if (!ToutiaoUtil.isImage(suffix)) {
		                return null;
		            }
		            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
		            //调用put方法上传
		            Response res = uploadManager.put(image.getBytes(), fileName, getUpToken());
		            //打印返回的信息
		            if (res.isOK() && res.isJson()) {
		                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
		            } else {
		                logger.error("七牛异常:" + res.bodyString());
		                return null;
		            }
		        } catch (QiniuException e) {
		            // 请求失败时打印的异常的信息
		            logger.error("七牛异常:" + e.getMessage());
		            return null;
		        }
		    }

}
