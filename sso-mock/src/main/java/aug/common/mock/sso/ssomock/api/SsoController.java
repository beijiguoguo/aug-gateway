package aug.common.mock.sso.ssomock.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by guoxiaoyong on 2020/9/8.
 */
@RestController
public class SsoController {

    @GetMapping("/mock1")
    public String mockData_1(String source) {
        System.out.println(source);
        return "{\"username\":\"aug\",\"age\":30,\"sex\":\"男\"}";
    }

    @PostMapping("/mock2")
    public String mockData_2(@RequestBody String body) {
        System.out.println(body);
        return "{\"username\":\"aug\",\"age\":30,\"sex\":\"男\"}";
    }
}
