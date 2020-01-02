package joey.perfmonitor.demo.service;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Joey
 * @date 2019/12/31
 */
@Service
public class Test1Service {
    public Object t1(String param) {
        stopSeconds(3);
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
