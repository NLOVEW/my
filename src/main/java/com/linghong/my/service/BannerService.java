package com.linghong.my.service;

import com.linghong.my.pojo.Banner;
import com.linghong.my.repository.BannerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/23 11:00
 * @Version 1.0
 * @Description:
 */
@Service
public class BannerService {
    @Resource
    private BannerRepository bannerRepository;


    public List<Banner> getBanners() {
        List<Banner> bannerList = bannerRepository.findAll();
        return bannerList;
    }
}
