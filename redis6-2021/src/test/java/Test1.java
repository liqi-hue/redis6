import com.atguigu.App;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2022-12-15 7:29 PM
 */
@SpringBootTest(classes = App.class)
public class Test1 {

    @Resource
    private RedisTemplate redisTemplate;
    @Test
    public void test(){
        redisTemplate.opsForValue().set("key1","value1");
    }
}
