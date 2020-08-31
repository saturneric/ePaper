package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codedream.epaper.model.server.ChildServerPassport;

import java.util.Date;

@Data
@ApiModel("子服务器护照")
@NoArgsConstructor
public class JsonableCSP {

    // 身份认证码
    private String identityCode;

    // 最后一次签证日期
    private Date lastUpdateTime;

    // 护照是否过期
    private boolean expired;

    public JsonableCSP(ChildServerPassport csp){
        this.identityCode = csp.getIdentityCode();
        this.lastUpdateTime = csp.getLastUpdateTime();
        this.expired = csp.isExpired();
    }
}
