package joey.perfmonitor.config;

import joey.perfmonitor.interceptor.PerfMonitorInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Joey
 * @date 2019/12/30
 */
@Configuration
@ConditionalOnProperty(prefix="joey.perfmonitor", name = "enabled", havingValue = "true")
public class PerfMonitorConfig {
    /**
     * 默认拦截带有注解ElapsedTime的方法和类
     */
    private static final String DEFAULT_AOP_PATTERN = "@within(" + ElapsedTime.class.getName() + ") " +
            "|| @annotation(" + ElapsedTime.class.getName() + ")";

    @Autowired
    private PerfMonitorProperties properties;

    @Bean
    public Advice perfMonitorAdvice() {
        return new PerfMonitorInterceptor(properties);
    }

    @Bean
    public Advisor perfMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        String finalException = DEFAULT_AOP_PATTERN;

        String aopExpression = properties.getAopExpression();
        if (!StringUtils.isEmpty(aopExpression)) {
            finalException = finalException + " || " + aopExpression;
        }

        pointcut.setExpression(finalException);
        return new DefaultPointcutAdvisor(pointcut, perfMonitorAdvice());
    }
}
