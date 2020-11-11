package com.github.taoroot.web.sys;

import com.github.taoroot.common.core.constant.SecurityConstants;
import com.github.taoroot.common.core.utils.CaptchaCacheService;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.AuthUser;
import com.github.taoroot.common.security.AuthJwtEncoder;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.common.security.annotation.Log;
import com.github.taoroot.common.security.annotation.PermitAll;
import com.github.taoroot.common.security.properties.SecurityProperties;
import com.github.taoroot.service.sys.SysAuthService;
import com.github.taoroot.service.component.SysAuthUserService;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.extra.servlet.ServletUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Api(tags = "系统登录")
@RestController
@AllArgsConstructor
public class SysAuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityProperties securityProperties;
    private final HttpServletRequest request;
    private final SysAuthService sysAuthService;
    private final CaptchaCacheService captchaCacheService;

    @Log("系统用户详情")
    @ApiOperation("系统用户信息")
    @PreAuthorize("hasAuthority('sys:auth:user_info')")
    @GetMapping(value = "/sys/auth/user_info")
    public R userInfo() {
        return sysAuthService.userInfo();
    }


    @Log("修改密码")
    @ApiOperation("修改密码")
    @PreAuthorize("hasAuthority('sys:auth:password')")
    @PutMapping(value = "/sys/auth/password")
    public R updatePass(String oldPass, String newPass) {
        return sysAuthService.updatePass(oldPass, newPass);
    }


    @PermitAll
    @Log("图形验证码")
    @GetMapping(value = "/sys/auth/captcha/image")
    public void getImage(HttpServletResponse response, @RequestParam("key") String key) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("cache-Control", "no-cache, must-revalidate");
        response.addHeader("cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        try (ServletOutputStream out = response.getOutputStream()) {
            RandomGenerator randomGenerator = new RandomGenerator("023456789", 4);
            LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100);
            captcha.setGenerator(randomGenerator);
            captcha.createCode();
            captcha.write(out);
            out.flush();
            captchaCacheService.set(key, captcha.getCode(), 120);
        }
    }


    @PermitAll
    @SneakyThrows
    @ApiOperation("账号密码登录")
    @PostMapping("/sys/auth/token")
    public Object login(@RequestParam Map<String, String> parameters) {
        AuthUser userDetail = null;
        String grantType = parameters.get("grant_type");
        if ("password".equals(grantType)) {
            String username = parameters.get("username");
            String password = parameters.get("password");
            String key = SecurityUtils.request().getParameter(SecurityConstants.IMAGE_KEY_PARAM);
            String value = SecurityUtils.request().getParameter(SecurityConstants.IMAGE_VALUE_PARAM);
            if (securityProperties.getCaptcha() && !captchaCacheService.check(key, value)) {
                return R.errMsg("验证码错误");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(SysAuthUserService.NAME + ":" + username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userDetail = (AuthUser) authentication.getPrincipal();
        }
        Assert.notNull(userDetail, "登录失败");

        log.info("[DING] 系统用户[{}]:[{}] 登录成功, IP: {} ",
                userDetail.getUsername(),
                userDetail.getNickname(),
                ServletUtil.getClientIP(request));

        HashMap<String, Object> hashMap = new HashMap<>();
        String accessToken = AuthJwtEncoder.encode(userDetail.getUsername(),
                SysAuthUserService.NAME,
                securityProperties.getToken().get(SysAuthUserService.NAME).getSecret(),
                securityProperties.getToken().get(SysAuthUserService.NAME).getExpire());
        hashMap.put("access_token", accessToken);
        return hashMap;
    }
}
