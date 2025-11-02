package com.ruoyi.manage.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS 客户端装配。
 * - 仅在 aliyun.oss.enabled=true 时创建。
 * - 依赖环境变量或配置文件提供的密钥，不将密钥写入代码库。
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssClientConfig {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true", matchIfMissing = false)
    public OSS ossClient(OssProperties props) {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        // 官方推荐：合理配置连接池与超时（保持默认即可满足大多数场景，可视需要再调参）
        conf.setSupportCname(true); // 支持自定义域名
        return new OSSClientBuilder().build(props.getEndpoint(), props.getAccessKeyId(), props.getAccessKeySecret(), conf);
    }
}
