package org.codedream.epaper.service;

import javafx.util.Pair;
import org.codedream.epaper.component.auth.EPPasswordEncoder;
import org.codedream.epaper.component.auth.EPUsernameEncoder;
import org.codedream.epaper.exception.badrequest.UsernameAlreadyExistException;
import org.codedream.epaper.exception.notfound.UserNotFoundException;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.repository.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService implements IUserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private EPPasswordEncoder passwordEncoder;

    @Resource
    private EPUsernameEncoder usernameEncoder;

    /**
     * 获得列出所有用户
     * @return 用户对象列表
     */
    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    /**
     * 通过用户ID号（数据库）查找用户对象
     * @param id 用户ID号
     * @return 用户对象
     */
    @Override
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    /**
     * 通过openid查找用户对象
     * @param openid 用户openid
     * @return
     */
    @Override
    public Optional<User> findUserByOpenid(String openid) {
        Optional<User> user = userRepository.findByUsername(openid);
        return user;
    }

    /**
     * 查询用户是否存在
     * @param openid 用户openid
     * @return 用户存在状态标记与openid对应的用户对象（如果存在）
     */
    @Override
    public Pair<Boolean, User> checkIfUserExists(String openid){
        Optional<User> user = userRepository.findByUsername(openid);
        return user.map(value -> new Pair<>(true, value)).orElseGet(() -> new Pair<>(false, null));
    }

    /**
     * 获得用户所有的权限角色
     * @param user 用户对象
     * @return 用户权限列表
     */
    @Override
    public Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
        return new ArrayList<>();
    }

    /**
     * 更新用户的密码（过程中自动计算密码散列值）
     * @param user 用户对象
     * @param password 用户密码
     * @return 用户对象（更新后）
     */
    @Override
    public User updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return update(user);
    }

    /**
     * 封禁用户
     * @param user 用户对象
     * @return 用户对象（更新后）
     */
    @Override
    public User disableUser(User user){
        user.setEnabled(false);
        return update(user);
    }

    /**
     * 通过用户ID号列表（数据库）获得用户对象列表
     * @param usersId 用户ID号列表
     * @return 用户对象列表
     */
    @Override
    public Set<User> findUsersById(Set<Integer> usersId) {
        Set<User> userSet = new  HashSet<>();
        for(Integer id : usersId){
            Optional<User> user = findUserById(id);
            if(!user.isPresent()) throw new UserNotFoundException(String.format("ID: %d", id));
            userSet.add(user.get());
        }
        return userSet;
    }

    /**
     * 随机生成一个用户名
     * @param user 用户对象
     */
    @Override
    public void generateRandomUsername(User user) {
        user.setUsername(usernameEncoder.encode(UUID.randomUUID().toString()));
    }

    /**
     * 在数据库中保存一个新的用户对象
     * @param user 用户对象
     * @return 用户对象（更新后）
     */
    @Override
    public User save(User user) {
        // 查找用户名是否已经被注册
        if(userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UsernameAlreadyExistException(user.getUsername());

        // 强制以哈希值(sha256)保存密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 更新一个已有的用户对象在数据库中的信息
     * @param user 用户对象
     * @return 用户对象（更新后）
     */
    @Override
    public User update(User user) {
        // 执行前检查
        if(!userRepository.findById(user.getId()).isPresent())
            throw new UserNotFoundException(user.getId(), user.getUsername());
        return userRepository.save(user);

    }

    /**
     * 删除用一个在数据库中已有的用户
     * @param user 用户对象
     */
    @Override
    public void delete(User user) {
        // 执行前检查
        if(!userRepository.findById(user.getId()).isPresent())
            throw new UserNotFoundException(user.getId(), user.getUsername());
        userRepository.delete(user);
    }

    /**
     * 获得一个空的默认用户（已激活）
     * @return 用户对象
     */
    @Override
    public User getDefaultUser() {
        return new User();
    }


}
