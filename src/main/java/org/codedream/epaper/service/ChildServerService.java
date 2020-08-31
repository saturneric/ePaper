package org.codedream.epaper.service;

import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.server.ChildServerPassport;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.server.ChildServerPassportRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * 子服务器管理服务实例
 */
@Service
public class ChildServerService implements IChildServerService {

    @Resource
    private ChildServerPassportRepository cspRepository;

    /**
     * 获取新的子服务器护照
     * @param user 子服务器账号
     * @return 子服务器护照
     */
    @Override
    public ChildServerPassport createCSP(User user) {
        if(user == null) throw new InnerDataTransmissionException();
        ChildServerPassport csp = new ChildServerPassport();

        // 检查账号身份
        if(!user.getUserAuth().getRole().equals("ChildServer")) throw new InnerDataTransmissionException();
        csp.setUser(user);
        return cspRepository.save(csp);
    }

    /**
     * 获取新的签证
     * @param idcode 护照身份认证码
     * @return 子服务器护照（更新后）
     */
    @Override
    public ChildServerPassport updateCSP(String idcode) {
        Optional<ChildServerPassport> csp = cspRepository.findByIdentityCode(idcode);
        if(!csp.isPresent()) throw new NotFoundException(idcode);

        if(csp.get().isExpired()) return null;

        csp.get().setLastUpdateTime(new Date());

        return cspRepository.save(csp.get());
    }

    /**
     * 检查护照身份认证码是否有效
     * @param idcode 护照身份认证码
     * @return 布尔值
     */
    @Override
    public boolean checkCSP(String idcode) {
        return cspRepository.findByIdentityCode(idcode).isPresent();
    }

    /**
     * 检查护照是否过期
     * @param idcode 护照身份认证码
     * @return 布尔值
     */
    @Override
    public boolean checkCSPExpired(String idcode) {
        Optional<ChildServerPassport> csp = cspRepository.findByIdentityCode(idcode);
        if(!csp.isPresent()) throw new NotFoundException(idcode);

        return csp.get().isExpired();
    }

    /**
     * 获得子服务器护照详细信息
     * @param idcode 护照身份认证码
     * @return 子服务器护照对象
     */
    @Override
    public ChildServerPassport getCSPInfo(String idcode) {
        Optional<ChildServerPassport> csp = cspRepository.findByIdentityCode(idcode);
        if(!csp.isPresent()) throw new NotFoundException(idcode);
        return csp.get();
    }

    /**
     * 更新子服务器护照对象到数据库中
     * @param csp 子服务器护照对象
     * @return 子服务器护照对象（更新后）
     */
    @Override
    public ChildServerPassport update(ChildServerPassport csp) {
        if(!cspRepository.findById(csp.getId()).isPresent()) throw new NotFoundException(csp.getIdentityCode());
        return cspRepository.save(csp);
    }
}
