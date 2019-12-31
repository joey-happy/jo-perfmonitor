package joey.perfmonitor.demo.service;

import joey.perfmonitor.config.ElapsedTime;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Joey
 * @date 2019/12/31
 */
@Service
@ElapsedTime(2000)
public class TestService {
    public Object t1(String param) {
        stopSeconds(3);
        return UUID.randomUUID().toString();
    }

    public Object t2(Object param) {
        stopSeconds(1);
        //此刻t4拦截不到
        t4();
        return UUID.randomUUID().toString();
    }

    @ElapsedTime(1000)
    public Object t3(String param, Object param2) {
        stopSeconds(3);
        return UUID.randomUUID().toString();
    }

    @ElapsedTime(2500)
    public Object t4() {
        stopSeconds(3);
        return UUID.randomUUID().toString();
    }

    public Object t5() {
        stopSeconds(1);
        return UUID.randomUUID().toString();
    }

    private void stopSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
