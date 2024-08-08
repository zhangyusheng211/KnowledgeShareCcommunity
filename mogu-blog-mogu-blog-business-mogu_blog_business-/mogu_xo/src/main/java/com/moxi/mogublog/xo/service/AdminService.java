package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Collection;
import java.util.List;

/**
 * 管理员表 服务类
 *
 * @author 陌溪
 * @date 2018-09-04
 */
public interface AdminService extends SuperService<Admin> {

    /**
     * 通过UID获取Admin
     *
     * @param uidList
     * @return
     */
    public List<Admin> getAdminListByUid(Collection<String> uidList);

    /**
     * 获取在线用户列表
     *
     * @param adminVO
     * @return
     */
    public String getOnlineAdminList(AdminVO adminVO);

    /**
     * Web端通过用户名获取一个Admin
     *
     * @param userName
     * @return
     */
    public Admin getAdminByUser(String userName);

    /**
     * 获取当前管理员
     *
     * @return
     */
    public Admin getMe();

    /**
     * 添加在线用户
     *
     * @param admin            管理员
     * @param expirationSecond 过期时间【秒】
     */
    public void addOnlineAdmin(Admin admin, Long expirationSecond);

    /**
     * 获取管理员列表
     *
     * @param adminVO
     * @return
     */
    public String getList(AdminVO adminVO);

    /**
     * 添加管理员
     *
     * @param adminVO
     * @return
     */
    public String addAdmin(AdminVO adminVO);

    /**
     * 编辑管理员
     *
     * @param adminVO
     * @return
     */
    public String editAdmin(AdminVO adminVO);

    /**
     * 编辑当前管理员信息
     *
     * @return
     */
    public String editMe(AdminVO adminVO);

    /**
     * 修改密码
     *
     * @return
     */
    public String changePwd(String oldPwd, String newPwd);

    /**
     * 重置密码
     *
     * @param adminVO
     * @return
     */
    public String resetPwd(AdminVO adminVO);

    /**
     * 批量删除管理员
     *
     * @param adminUids
     * @return
     */
    public String deleteBatchAdmin(List<String> adminUids);

    /**
     * 退出登录
     *
     * @param tokenList
     * @return
     */
    public String forceLogout(List<String> tokenList);

    /**
     * 获取发表的文章数
     *
     * @param adminUid
     * @return
     */
    Integer getBlogPublishCount(String adminUid);

    /**
     * 获取文章被访问数
     *
     * @param adminUid
     * @return
     */
    Integer getBlogVisitCount(String adminUid);

    /**
     * 获取管理员附身用户uid
     * @param adminUid
     * @return
     */
    String getAdminUserUid(String adminUid);
}