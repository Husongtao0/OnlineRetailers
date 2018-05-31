package search.test;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package search.test
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-*.xml")
public class SearchTest {
    @Autowired
    private ItemSearchService itemSearchService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void test(){
        Map map = new HashMap();
        map.put("keywords","三星");
       // map.put("price","0-500");


        Map search = itemSearchService.search(map);
        List<TbItem> rows = (List<TbItem>)search.get("rows");

        for (TbItem row : rows) {
            System.out.println(row.getTitle());
            System.out.println(row.getPrice());
        }

    }
}
