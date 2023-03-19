package com.atguigu.test;

/**
 * @author: liqi
 * @create 2023-02-28 19:27
 */

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther zzyy * @create 2020-11-04 16:53
 */
public class BloomFilterTest {
    public static final int _1W = 10000;
    //布隆过滤器里预计要插入多少数据
    public static int size = 100 * _1W;
    //误判率,它越小误判的个数也就越少(思考，是不是可以设置的无限小，没有误判岂不更好)
    public static double fpp = 0.03;
    // 构建布隆过滤器
    private static BloomFilter bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, fpp);


    /*
    *  Guava 提供的布隆过滤器的实现还是很不错的 （想要详细了解的可以看一下它的源码实现），
    *  但是它有一个重大的缺陷就是只能单机使用 ，而现在互联网一般都是分布式的场景。
    *  为了解决这个问题，我们就需要用到 Redis 中的布隆过滤器了
    * */
    public static void main(String[] args) {
        //1 先往布隆过滤器里面插入100万的样本数据
        for (int i = 0; i < size; i++) {
            bloomFilter.put(i);
        }
        //故意取10万个不在过滤器里的值，看看有多少个会被认为在过滤器里
        List list = new ArrayList<>(10 * _1W);
        for (int i = size + 1; i < size + 100000; i++) {
            if (bloomFilter.mightContain(i)) {
                System.out.println(i + "\t" + "被误判了.");
                list.add(i);
            }
        }
        System.out.println("误判的数量：" + list.size());
    }
}