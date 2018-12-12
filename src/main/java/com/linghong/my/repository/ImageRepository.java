package com.linghong.my.repository;

import com.linghong.my.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
