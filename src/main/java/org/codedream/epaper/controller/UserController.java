package org.codedream.epaper.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.codedream.epaper.component.json.model.JsonableUser;
import org.codedream.epaper.exception.badrequest.AlreadyExistException;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.user.User;
import org.codedream.epaper.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("user")
@Api("用户验证类接口")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("用户注册接口")
    public JsonableUser createUser(@RequestBody JsonableUser jsonableUser){
        if(jsonableUser.getOpenid() == null) throw new IllegalAccessError("Null Value Openid");
        if(userService.findUserByOpenid(jsonableUser.getOpenid()).isPresent())
            throw new AlreadyExistException(jsonableUser.getOpenid());

        User user = userService.getDefaultUser();
        return new JsonableUser(userService.save(jsonableUser.parseObject(user)));
    }

    @PostMapping("cs")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("子服务器注册接口")
    public JsonableUser createChildServerUser(@RequestParam(value = "clientCode") String clientCode,
                                              @RequestBody JsonableUser jsonableUser){
        if(jsonableUser.getOpenid() == null) throw new IllegalAccessError("Null Value Openid");
        if(userService.findUserByOpenid(jsonableUser.getOpenid()).isPresent())
            throw new AlreadyExistException(jsonableUser.getOpenid());

        if(!clientCode.equals("dc9fbb4f4f0b84fa903058991af60e73556494af8a02ef69fb6a93217729f04b"))
            throw new IllegalException("Illegal Child Server");

        User user = userService.getDefaultUser();
        user.getUserAuth().setRole("ChildServer");
        return new JsonableUser(userService.save(jsonableUser.parseObject(user)));
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("检查用户是否存在接口")
    public JsonableUser getUser(@RequestParam(value = "openid") String openid){
        Optional<User> user = userService.findUserByOpenid(openid);
        if(!user.isPresent())
            throw new NotFoundException(openid);

        JsonableUser jsonableUser = new JsonableUser();
        jsonableUser.setId(user.get().getId());
        jsonableUser.setOpenid(user.get().getUsername());
        return jsonableUser;
    }


}
