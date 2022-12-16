import com.atguigu.App;
import com.atguigu.entity.User;
import com.atguigu.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2022-12-15 6:29 PM
 */
@SpringBootTest(classes = App.class)
public class Test1 {

    @Resource
    private UserService userService;

    @Test
    public void test(){
        User user = new User();
        user.setId(10001);
        user.setUsername("张三");
        user.setPassword("123456");
        user.setDeleted((byte) 1);
        user.setSex((byte)1);
        userService.save(user);
    }
}
