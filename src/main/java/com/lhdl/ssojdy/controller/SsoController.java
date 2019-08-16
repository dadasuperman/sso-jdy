package com.lhdl.ssojdy.controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lhdl.ssojdy.config.HttpClientUtils;
import com.lhdl.ssojdy.config.SsoConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouda
 * @ClassName: SsoController
 * @Description: TODO
 * @date 2019/8/13 14:04
 */
@Controller
@PropertySource("classpath:myproperties.properties")
public class SsoController {

    private static Logger logger = LoggerFactory.getLogger(SsoController.class);

    @Autowired
    private SsoConfig ssoConfig;

    @Value("${anytechSecret}")//安易商sercet
    private String anytechSecret;

    @Value("${sso.apiKey}")
    private String apiKey;//简道云组织架构管理员apikey,只有在其组织架构下才能登录

    static Map<String,String> userCache = new HashMap();

    static Map<String,String> usernameCache = new HashMap(1);

    static Map<String,String> anuserIdCache = new HashMap(1);

    //安易商由此跳至登录页
    @GetMapping("/checkAnytech")
    public String checkAnytech(HttpSession session,HttpServletRequest request){

        String token =  StringUtils.isEmpty(request.getParameter("Authorization"))? request.getHeader("Authorization") : request.getParameter("Authorization");

        token = token.trim();
        token = token.replace("Bearer","");

        Claims claims = Jwts.parser()
                .setSigningKey(anytechSecret)
                .parseClaimsJws(token).getBody();
        String userId = claims.get("userId", String.class);
        for(Map.Entry<String,String> map:userCache.entrySet()){
            if(StringUtils.equals(map.getKey(),userId)){
                usernameCache.clear();
                usernameCache.put("username",map.getValue());
                return "redirect:https://www.jiandaoyun.com/sso/custom/5d5116279a946019768eb746/iss";
            }
        }

        anuserIdCache.clear();
        anuserIdCache.put("anuserId",userId);

        //String userName = claims.get("userName", String.class);
        //String snId = claims.get("snId", String.class);
        //String snTitle = claims.get("snTitle", String.class);

        return "login";

    }

    //登录验证用户
    @PostMapping("loginJdy")
    @ResponseBody
    public Map loginJdy(String username){
        usernameCache.clear();
        usernameCache.put("username",username);
        //调用简道云接口看是否在组织架构中
        Map<String, String> param = new HashMap<>();
        param.put("Authorization","Bearer "+apiKey);
        param.put("Content-Type","application/json;charset=utf-8");
        String httpPostRes = HttpClientUtils.getHttpPost("https://api.jiandaoyun.com/api/v2/user/" + username + "/user_retrieve", null, param);
        Map<String,String> resMap = (Map)JSON.parse(httpPostRes);
        logger.warn(resMap.toString());
        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String,String> map:resMap.entrySet()) {
            if(StringUtils.equals(map.getKey(),"code") ){
                logger.warn(String.format("code=%s,msg=%s",resMap.get("code"),resMap.get("msg")));
                data.clear();
                usernameCache.clear();
                data.put("err",resMap.get("msg"));
                break;
            }
            if(StringUtils.equals(map.getKey(),"user")){
                data.put("suc","200");
                //缓存
                userCache.put(anuserIdCache.get("anuserId"),username);
                logger.warn("login userCache=  "+userCache);
                break;
            }
        }

        return data;

    }

    //简易云的登录认证
    @GetMapping("go")
    public void goJdy(@RequestParam(name = "request", defaultValue = "") String request,
                         @RequestParam(name = "state", defaultValue = "") String state,
                         HttpServletResponse httpServletResponse) throws IOException {
        String response = getReponse(request);
        httpServletResponse.sendRedirect(
                String.format(
                        "%s?response=%s&state=%s",
                        this.ssoConfig.getAcs(), response, state
                )
        );
    }


    private String getReponse(String request) {
        Algorithm algorithm = Algorithm.HMAC256(ssoConfig.getSecret());

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("com.jiandaoyun")
                //.withAudience(this.ssoConfig.getIssuer())
                .build();
        DecodedJWT decoded = verifier.verify(request);
        if (!"sso_req".equals(decoded.getClaim("type").asString())) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return JWT.create()
                .withClaim("type", "sso_res")
                .withClaim("username", usernameCache.get("username"))
                //.withIssuer(this.ssoConfig.getIssuer())
                .withAudience("com.jiandaoyun")
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
}
