package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codedream.epaper.model.user.User;

@Data
@ApiModel("用户验证信息")
@NoArgsConstructor
public class JsonableUser {

    // 用户的ID号（数据库）
    private Integer id;

    // 用户openid
    private String openid;

    // 密码（哈希值）
    private String password;

    public JsonableUser(User user){
        this.openid = user.getUsername();
        this.password = user.getPassword();
        this.id = user.getId();
    }

    public User parseObject(User user){
        user.setUsername(this.getOpenid());
        user.setPassword(this.getPassword());
        return user;
    }
}
