package org.codedream.epaper.exception.badrequest;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证信息过期
 */
@Data
@NoArgsConstructor
public class AuthExpiredException extends IllegalException {
    public AuthExpiredException(String msg){
        super(msg);
    }
}
