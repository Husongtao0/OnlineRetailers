import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package PACKAGE_NAME
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class JedisClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){

        redisTemplate.boundValueOps("key3").set("123");
        System.out.println(redisTemplate.boundValueOps("key3").get());
    }

    @Test
    public void testRedisClusterAdd() throws Exception{

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.25.153",7001));
        nodes.add(new HostAndPort("192.168.25.153",7002));
        nodes.add(new HostAndPort("192.168.25.153",7003));
        nodes.add(new HostAndPort("192.168.25.153",7004));
        nodes.add(new HostAndPort("192.168.25.153",7005));
        nodes.add(new HostAndPort("192.168.25.153",7006));
        JedisCluster cluster = new JedisCluster(nodes);//本身封装了连接池

        String set = cluster.set("key456", "ceshi");
        System.out.println(cluster.get("key456"));
        //3.释放  在项目中一定是系统停用了之后才释放。
        cluster.close();
    }
}
