package com.linghong.my.repository;

import com.linghong.my.pojo.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CollectionRepository extends JpaRepository<Collection,Long> {
    Collection findByUser_UserId(@Param("userId") Long userId);
}
