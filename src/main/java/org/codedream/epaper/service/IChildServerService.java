package org.codedream.epaper.service;

import org.codedream.epaper.model.server.ChildServerPassport;
import org.codedream.epaper.model.user.User;

/**
 * 子服务器服务接口
 */
public interface IChildServerService {

    /**
     * 创建子服务器护照
     * @param user 用户对象
     * @return 子服务器护照对象
     */
    ChildServerPassport createCSP(User user);

    /**
     * 更新子服务器签证
     * @param idcode 子服务器护照份认证码
     * @return 子服务器护照对象
     */
    ChildServerPassport updateCSP(String idcode);

    /**
     * 检查子服务器对象是否合法
     * @param idcode 子服务器护照身份认证码
     * @return 布尔值
     */
    boolean checkCSP(String idcode);

    /**
     * 检查子服务器护照是否过期
     * @param idcode 子服务器护照身份认证码
     * @return 布尔值
     */
    boolean checkCSPExpired(String idcode);

    /**
     * 获得子服务器护照详细信息
     * @param idcode 子服务器护照身份认证码
     * @return 子服务器护照对象
     */
    ChildServerPassport getCSPInfo(String idcode);

    /**
     * 同步子服务器护照与数据库中的值
     * @param csp 子服务器护照对象
     * @return 子服务器护照对象（更新后）
     */
    ChildServerPassport update(ChildServerPassport csp);
}
