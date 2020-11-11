package com.github.taoroot.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * 支付配置
 */
@Data
@ConfigurationProperties(prefix = "pay")
public class PayProperties implements InitializingBean {

    /**
     * 微信支付参数
     */
    @NestedConfigurationProperty
    private WxPayApiConfig wxPay = new WxPayApiConfig();

    @Override
    public void afterPropertiesSet() throws Exception {
        assert wxPay.getAppId() != null;
        assert wxPay.getAppSecret() != null;
        assert wxPay.getJsApi() != null;
    }


    @Data
    public static class WxPayApiConfig {

        /**
         * 交易编号
         */
        public final static String TRADE_NO_KEY = "out_trade_no";

        /**
         * 支付状态
         */
        public final static String TRADE_STATUS = "trade_status";

        /**
         * 返回码
         */
        public final static String RESULT_CODE = "result_code";

        /**
         * 公众平台id
         */
        private String appId;
        /**
         * 公众平台密钥
         */
        private String appSecret;
        /**
         * 商户号
         */
        private String mchId;

        /**
         * 商户密钥
         */
        private String partnerKey;
        /**
         * 商户证书路径
         */
        private String certPath;

        /**
         * 微信支付异步通知地址
         */
        private String notifyUrl;

        /**
         * JSAPI 允许签名URL
         */
        private List<String> jsApi;

        /**
         * auth2.0 地址
         */
        private String auth2Url;
    }
}