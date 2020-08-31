package org.codedream.epaper.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.api.QuickJSONRespond;
import org.codedream.epaper.component.json.respond.UserLoginCheckerJSONRespond;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.service.IAuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

// 认证成功返回
@Slf4j
@Component
public class EPAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Resource
    private QuickJSONRespond quickJSONRespond;

    @Resource
    private IAuthService authService;

    /**
     *
     * @param request HTTP请求
     * @param response HTTP返回
     * @param authentication 认证柄
     * @throws IOException I/O异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {

        UserLoginCheckerJSONRespond respond = new UserLoginCheckerJSONRespond();
        respond.setUserExist(authentication.isAuthenticated());
        respond.setLoginStatus(authentication.isAuthenticated());
        respond.setPvc(authService.preValidationCodeGetter());

        // 获得 JSONTokenAuthenticationToken
        JSONTokenAuthenticationToken authenticationToken = (JSONTokenAuthenticationToken) authentication;

        User user = (User) authenticationToken.getPrincipal();

        Optional<String> tokenOptional = authService.userNewTokenGetter(
                user.getUsername(), authenticationToken.getClientCode());

        if(tokenOptional.isPresent()){
            respond.setToken(tokenOptional.get());
        }
        else respond.setToken("");

        // 认证成功返回200
        response.getWriter().write(quickJSONRespond.getRespond200("Authentication Success", respond));
        response.setStatus(200);
    }
}
