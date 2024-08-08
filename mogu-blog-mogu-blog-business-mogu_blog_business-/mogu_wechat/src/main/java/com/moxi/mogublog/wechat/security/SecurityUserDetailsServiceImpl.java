package com.moxi.mogublog.wechat.security;

import com.alibaba.fastjson.JSONArray;
import com.meetc.mogublog.security.SecurityUserFactory;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.CategoryMenu;
import com.moxi.mogublog.commons.feign.AdminFeignClient;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mougblog.base.global.BaseSysConf;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 将SpringSecurity中的用户管理和数据库的管理员对应起来
 *
 * @author 陌溪
 * @date 2020/9/14 10:43
 */
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private AdminFeignClient adminFeignClient;

    /**
     * @param username 浏览器输入的用户名【需要保证用户名的唯一性】
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过用户名加载SpringSecurity用户
        Admin admin = new Admin();
        String categoryMenuListJson = adminFeignClient.getCategoryMenuByUserName(username);
        List<String> categoryMenuUrlList = new ArrayList<>();
        Map<String, Object> dataMap = (Map<String, Object>) JsonUtils.jsonToObject(categoryMenuListJson, Map.class);
        if (BaseSysConf.SUCCESS.equals(dataMap.get(BaseSysConf.CODE))) {
            String jsonList = JsonUtils.objectToJson(dataMap.get(BaseSysConf.DATA));
            List<CategoryMenu> list = JSONArray.parseArray(jsonList, CategoryMenu.class);
            list.forEach(item -> {
                categoryMenuUrlList.add(item.getUrl());
            });
        }
        admin.setUid(BaseSysConf.DEFAULT_UID);
        admin.setUserName(username);
        admin.setPermissionList(categoryMenuUrlList);
        UserDetails userDetails = SecurityUserFactory.create(admin, "admin");
        return userDetails;
    }
}
