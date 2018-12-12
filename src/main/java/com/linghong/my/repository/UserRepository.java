package com.linghong.my.repository;

import com.linghong.my.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByMobilePhone(@Param("mobilePhone") String mobilePhone);
}
