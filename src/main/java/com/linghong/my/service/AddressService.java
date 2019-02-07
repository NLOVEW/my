package com.linghong.my.service;

import com.linghong.my.pojo.Address;
import com.linghong.my.pojo.User;
import com.linghong.my.repository.AddressRepository;
import com.linghong.my.repository.UserRepository;
import com.linghong.my.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 11:04
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class AddressService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private UserRepository userRepository;

    public boolean addAddress(Address address, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        List<Address> addressList = addressRepository.findAllByUser_UserId(userId);
        if (addressList == null || addressList.size() <= 0){
            address.setDef(true);
        }else {
            address.setDef(false);
        }
        address.setUser(user);
        address.setCreateTime(new Date());
        address.setUserful(true);
        addressRepository.save(address);
        logger.info("添加地址：{}",address);

        return true;
    }

    public Map<String, List<Address>> getAllAddressByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllByUser_UserId(userId);
        addresses = addresses.stream()
                             .filter(address -> address.getUserful().equals(true))
                             .collect(Collectors.toList());
        Map<String, List<Address>> collect = addresses.stream().collect(Collectors.groupingBy(address -> {
            if (address.getDef() != null && address.getDef()) {
                return "默认地址";
            }
            return "非默认地址";
        }));
        return collect;
    }

    public Address findAddressByAddressId(Long addressId) {
        return addressRepository.findById(addressId).get();
    }

    public boolean deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).get();
        address.setUserful(false);
        return true;
    }

    public boolean setDefault(Long addressId, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        List<Address> addressList = addressRepository.findAllByUser_UserId(userId);
        addressList.stream().forEach(address -> {
            address.setDef(false);
            if (address.getAddressId().equals(addressId)){
                address.setDef(true);
            }
        });
        return true;
    }
}
