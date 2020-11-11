package com.github.taoroot.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.taoroot.common.core.datascope.DataScopeContextHolder;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.security.AuthJwtDecoder;
import com.github.taoroot.common.security.AuthUserDetailsService;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.common.security.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@AllArgsConstructor
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    private final Map<String, AuthUserDetailsService> authUserDetailsServiceMap;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 密码登录扩展, 支持多表登录
        auth.userDetailsService(username -> {
            if (!username.contains(":")) {
                throw new UsernameNotFoundException("格式错误: type@username");
            }
            String[] split = username.split(":");
            return authUserDetailsServiceMap.get(split[0]).loadUserByUsername(split[1]);
        });
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 开放接口
        permitAllUrl(securityProperties.getPermitAllUrls(), httpSecurity);

        // JWT登录
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        bearerTokenResolver.setAllowFormEncodedBodyParameter(true);
        bearerTokenResolver.setAllowUriQueryParameter(true);
        HashMap<String, String> hashMap = new HashMap<>();
        for (String iss : securityProperties.getToken().keySet()) {
            SecurityProperties.TokenConfig tokenConfig = securityProperties.getToken().get(iss);
            hashMap.put(iss, tokenConfig.getSecret());
        }
        AuthJwtDecoder authJwtDecoder = new AuthJwtDecoder(hashMap);
        httpSecurity
                .oauth2ResourceServer()
                .authenticationEntryPoint((req, res, e) -> errMsg(1, "无权访问", res))
                .bearerTokenResolver(bearerTokenResolver)
                .jwt()
                .decoder(authJwtDecoder) // JWT解析
                .jwtAuthenticationConverter(source -> { // JWT 转为 Authorization
                    String iss = (String) source.getClaims().get("iss");
                    String sub = (String) source.getClaims().get("sub");
                    UserDetails userDetails = authUserDetailsServiceMap.get(iss).loadUserById(sub);
                    List<String> authorities = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    authorities.add("ROLE_" + iss);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    AuthorityUtils.createAuthorityList(authorities.toArray(new String[0])));

                    DataScopeContextHolder.set(SecurityUtils.getDataScope(authenticationToken));

                    return authenticationToken;
                });

        // 默认设置
        httpSecurity
                .cors().and()
                .headers().frameOptions().disable().and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().accessDeniedHandler((req, res, e) -> errMsg(1, "无权访问", res)).and()
                .exceptionHandling().authenticationEntryPoint((req, res, e) -> errMsg(401, "请先登录", res));


        // 其余接口都需鉴权
        httpSecurity.authorizeRequests().anyRequest().authenticated();
    }

    private void errMsg(Integer status, String msg, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(new ObjectMapper().writeValueAsString(R.instance(status, status, msg)));
        printWriter.flush();
    }

    @SneakyThrows
    public static void permitAllUrl(List<String> args, HttpSecurity httpSecurity) {
        for (String item : args) {
            if (item.contains(":")) {
                String[] split = item.split(":");
                httpSecurity.authorizeRequests().antMatchers(HttpMethod.resolve(split[1]), split[0]).permitAll();
            } else {
                httpSecurity.authorizeRequests().antMatchers(item).permitAll();
            }
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
