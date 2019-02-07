package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Banner;
import com.linghong.my.service.BannerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/23 10:59
 * @Version 1.0
 * @Description:
 */
@RestController
public class BannerController {
    @Resource
    private BannerService bannerService;

    /**
     * 获取banner
     * @return
     */
    @GetMapping("/banner/getBanners")
    public Response getBanners(){
        List<Banner> banners = bannerService.getBanners();
        return new Response(true,200,banners,"获取的banner");
    }
}
