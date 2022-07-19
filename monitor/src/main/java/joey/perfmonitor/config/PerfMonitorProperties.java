package joey.perfmonitor.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 性能监控配置属性
 *
 * @author Joey
 * @date 2019/12/30
 */
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties("joey.perfmonitor")
public class PerfMonitorProperties {
    /**
     * 是否开启监控
     */
    private Boolean enabled = false;

    /**
     * 拦截aop匹配
     */
    private String aopExpression;

    /**
     * 单位毫秒, 默认接口耗时超过1秒打印日志
     */
    private Long elapsedTime = 1000L;
}
