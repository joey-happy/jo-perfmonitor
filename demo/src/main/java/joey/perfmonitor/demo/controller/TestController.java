package joey.perfmonitor.demo.controller;

import joey.perfmonitor.config.ElapsedTime;
import joey.perfmonitor.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joey
 * @date 2019/9/20
 */
@RestController
@RequestMapping("/test")
@ElapsedTime(1000)
public class TestController {
    @Autowired
    private TestService testService;

    @ElapsedTime(500)
    @RequestMapping("/t1")
    public Object t1() {
        return testService.t1(new String("t1"));
    }

    @ElapsedTime(1500)
    @RequestMapping("/t2")
    public Object t2() {
        return testService.t2(new String("t2"));
    }

    @RequestMapping("/t3")
    public Object t3() {
        return testService.t3(new String("t3"), new Object());
    }

    @RequestMapping("/t4")
    public Object t4() {
        return testService.t4();
    }

    @RequestMapping("/t5")
    public Object t5() {
        return testService.t5();
    }
}
