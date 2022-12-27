package com.security.support.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
public class KeyPairConfig {

    @Bean
    public KeyPair keyPair() {
        ClassPathResource ksFile = new ClassPathResource("shy_debug.jks");//文件名
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "123456".toCharArray());  //第二个参数就是生成时候的密码
        return ksFactory.getKeyPair("shy_debug.jks");
    }

}

