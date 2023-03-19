import com.atguigu.App;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2023-02-28 19:42
 */
@SpringBootTest(classes = App.class)
public class RedissionBloomDemo {

    public static final int _1W = 10000;
    //布隆过滤器里预计要插入多少数据
    public static int size = 100 * _1W;
    //误判率,它越小误判的个数也就越少(思考，是不是可以设置的无限小，没有误判岂不更好)
    public static double fpp = 0.03;

    static RedissonClient redissonClient = null;//Jedis
    static RBloomFilter rBloomFilter = null;//redis版内置的布隆过滤器
    @Resource
    private RedisTemplate redisTemplate;


    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.220.140:6379").setDatabase(0);
        //构造redisson
        redissonClient = Redisson.create(config);
        //通过redisson构造rBloomFilter
        rBloomFilter = redissonClient.getBloomFilter("phoneListBloomFilter",new StringCodec());
        rBloomFilter.tryInit(size,fpp);

        //布隆过滤器添加一条数据
        rBloomFilter.add("10086");
        //添加进redis
        redissonClient.getBucket("10086",new StringCodec()).set("chinamobile10086");
    }

}
