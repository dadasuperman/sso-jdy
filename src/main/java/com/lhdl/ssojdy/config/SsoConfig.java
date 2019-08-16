package com.lhdl.ssojdy.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author zhouda
 * @ClassName: SsoConfig
 * @Description: TODO
 * @date 2019/8/14 9:50
 */
@Configuration
@ConfigurationProperties(prefix = "sso")
@PropertySource("classpath:myproperties.properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class SsoConfig {

    @NotBlank private String acs;
    @NotBlank private String issuer;
    //@NotBlank private String username;
    @NotBlank private String secret;

}
