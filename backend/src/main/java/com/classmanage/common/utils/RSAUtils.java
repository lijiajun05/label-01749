package com.classmanage.common.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * RSA 加解密工具类
 */
@Slf4j
@Component
public class RSAUtils {
    
    @Value("${rsa.private-key:}")
    private String privateKeyConfig;
    
    @Value("${rsa.public-key:}")
    private String publicKeyConfig;
    
    private RSA rsa;
    private String publicKey;
    private String privateKey;
    
    @PostConstruct
    public void init() {
        // 如果配置了密钥则使用配置的，否则生成新的
        if (privateKeyConfig != null && !privateKeyConfig.isEmpty() 
            && publicKeyConfig != null && !publicKeyConfig.isEmpty()) {
            this.privateKey = privateKeyConfig;
            this.publicKey = publicKeyConfig;
            this.rsa = new RSA(privateKey, publicKey);
        } else {
            // 生成新的密钥对
            this.rsa = new RSA();
            this.privateKey = rsa.getPrivateKeyBase64();
            this.publicKey = rsa.getPublicKeyBase64();
            log.info("Generated new RSA key pair");
            log.info("Public Key: {}", publicKey);
        }
    }
    
    /**
     * 获取公钥
     */
    public String getPublicKey() {
        return publicKey;
    }
    
    /**
     * 使用私钥解密
     */
    public String decrypt(String encryptedText) {
        try {
            return rsa.decryptStr(encryptedText, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error("RSA解密失败", e);
            throw new RuntimeException("密码解密失败，请刷新页面重试");
        }
    }
    
    /**
     * 使用公钥加密
     */
    public String encrypt(String plainText) {
        return rsa.encryptBase64(plainText, KeyType.PublicKey);
    }
}
