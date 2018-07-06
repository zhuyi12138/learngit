package com.toutiao.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.toutiao.dao.NewsDAO;
import com.toutiao.model.HostHolder;
import com.toutiao.model.News;
import com.toutiao.model.User;
import com.toutiao.util.ToutiaoUtil;

@Service
public class NewsService {

	@Autowired
	private NewsDAO newsDAO;

	
	public int InsertNews(News news){
		return newsDAO.addNews(news);
	}
    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    public News getNews(int newsId){
    	return newsDAO.selectNews(newsId);
    }
    public int updateCommentCount(int newsId,int count){
    	return newsDAO.updateCommentCount(newsId,count);
    }
    //上传图片
	public String uploadImage(MultipartFile image) throws IOException {
		// 判断上传文件是否为图片格式
		int dotPos = image.getOriginalFilename().lastIndexOf(".");
		if(dotPos < 0) return null;
		String suffix = image.getOriginalFilename().substring(dotPos+1).toLowerCase();
		if(!ToutiaoUtil.isImage(suffix)){
			return null;
		}else{
			String filename = UUID.randomUUID().toString().replaceAll("-", "")+"."+suffix;
			//读取文件用inputstream
			Files.copy(image.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR+filename).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			return ToutiaoUtil.DOMAIN+"image?name="+filename;
		}
	}
	public void updateLikeCount(int newsId, int likeCount) {
		// TODO Auto-generated method stub
		newsDAO.updateLikeCount(newsId,likeCount);
	}


}
