package joey.perfmonitor.interceptor;

import joey.perfmonitor.config.ElapsedTime;
import joey.perfmonitor.config.PerfMonitorProperties;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 性能监控拦截器
 *
 * 参考：DruidSpringAopConfiguration
 *
 * @author Joey
 * @date 2019/12/30
 */
@Slf4j
public class PerfMonitorInterceptor implements MethodInterceptor, InitializingBean, DisposableBean {
    private final PerfMonitorProperties properties;

    public PerfMonitorInterceptor(PerfMonitorProperties properties) {
        this.properties = properties;

        log.info("PerfMonitor-stats init. props={}", properties);
    }

    /**
     * 拦截方法调用 记录耗时
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return invocation.proceed();

        } finally {
            MethodInfo methodInfo = getMethodInfo(invocation);

            long minCost = getMinTimeCost(methodInfo);
            long end = System.currentTimeMillis();
            long cost = end - start;

            if (cost >= minCost) {
                StringBuilder sb = new StringBuilder();
                sb.append("PerfMonitor-stats ");
                sb.append("invokeTime=[" + formatTime(start)+ "&" + formatTime(end) + "] ");
                sb.append("elapsedTime=[" + cost + "] ");
                sb.append("expectedTime=[" + minCost + "] ");

                sb.append("class=[" + methodInfo.getClazzName() + "] ");
                sb.append("method=[" + methodInfo.getMethodName() + "] ");

                Object[] arguments = invocation.getArguments();
                if (null != arguments && arguments.length >0) {
                    sb.append("args=[");

                    for (Object argument : arguments) {
                        if (null != argument) {
                            sb.append(argument + ",");
                        }
                    }

                    sb.deleteCharAt(sb.length()-1);
                    sb.append("] ");
                }

                log.warn(sb.toString());
            }
        }
    }

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private String formatTime(long millis) {
        return new SimpleDateFormat(DATE_PATTERN).format(new Date(millis));
    }

    private long getMinTimeCost(MethodInfo methodInfo) {
        Method method = methodInfo.getMethod();

        //方法上的注解值
        ElapsedTime perfMethodAnnotation = AnnotationUtils.getAnnotation(method, ElapsedTime.class);
        if (null != perfMethodAnnotation) {
            return perfMethodAnnotation.value();
        }

        //类上的注解值
        Class clazz = methodInfo.getClazz();
        Annotation clazzAnnotation = clazz.getAnnotation(ElapsedTime.class);
        if (null != clazzAnnotation) {
            ElapsedTime perfClazzAnnotation = (ElapsedTime) clazzAnnotation;
            return perfClazzAnnotation.value();
        }

        //配置文件中的值
        return properties.getElapsedTime();
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * 获取方法信息
     *
     * @param invocation
     * @return
     */
    private MethodInfo getMethodInfo(MethodInvocation invocation) {
        Object thisObject = invocation.getThis();
        Method method = invocation.getMethod();

        if (thisObject == null) {
            return new MethodInfo(method.getDeclaringClass(), method);
        }

        if (method.getDeclaringClass() == thisObject.getClass()) {
            return new MethodInfo(method.getDeclaringClass(), method);
        }

        Class<?> clazz = thisObject.getClass();
        boolean isCglibProxy = false;
        boolean isJavassistProxy = false;

        for (Class<?> item : clazz.getInterfaces()) {
            if (item.getName().equals("net.sf.cglib.proxy.Factory")) {
                isCglibProxy = true;
                break;
            } else if (item.getName().equals("javassist.util.proxy.ProxyObject")) {
                isJavassistProxy = true;
                break;
            }
        }

        if (isCglibProxy || isJavassistProxy) {
            Class<?> superClazz = clazz.getSuperclass();
            return new MethodInfo(superClazz, method);
        }

        clazz = null;
        try {
            // 最多支持10层代理
            for (int i = 0; i < 10; ++i) {
                if (thisObject instanceof org.springframework.aop.framework.Advised) {
                    TargetSource targetSource = ((org.springframework.aop.framework.Advised) thisObject).getTargetSource();

                    if (targetSource == null) {
                        break;
                    }

                    Object target = targetSource.getTarget();
                    if (target != null) {
                        thisObject = target;
                    } else {
                        clazz = targetSource.getTargetClass();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("getMethodInfo error", ex);
        }

        if (clazz == null) {
            return new MethodInfo(method.getDeclaringClass(), method);
        }

        return new MethodInfo(clazz, method);
    }

    /**
     * 方法信息
     */
    private static class MethodInfo {
        private Class<?> clazz;
        private String clazzName;

        private Method method;
        private String methodName;

        public MethodInfo(Class<?> instanceClass, Method method) {
            this.clazz = instanceClass;
            this.clazzName = instanceClass.getName();

            this.method = method;
            this.methodName = method.getName();
        }

        public Class getClazz() {
            return clazz;
        }

        public String getClazzName() {
            return clazzName;
        }

        public Method getMethod() {
            return method;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}