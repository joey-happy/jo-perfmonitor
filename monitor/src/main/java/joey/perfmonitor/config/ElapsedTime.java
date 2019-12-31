package joey.perfmonitor.config;

import java.lang.annotation.*;

/**
 * 性能监控耗时注解
 *
 * @author Joey
 * @date 2019/12/30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ElapsedTime {
    /**
     * 单位毫秒, 默认接口耗时超过1秒打印日志
     * @return
     */
    long value() default 1000L;
}
