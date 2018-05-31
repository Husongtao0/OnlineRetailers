import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package PACKAGE_NAME
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 简单的值操作
     */
    @Test
    public void testValue(){
//        Jedis jedis = new Jedis();
//
//        jedis.set("key1","1");
//        jedis.close();

        //set   get
        // hset key  field value
        // hget key field

        redisTemplate.boundValueOps("100").set("ehheh");
        System.out.println(redisTemplate.boundValueOps("100").get());
    }
}
