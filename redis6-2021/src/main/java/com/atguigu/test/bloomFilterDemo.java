package com.atguigu.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * @author: liqi
 * @create 2023-02-28 19:22
 */

public class bloomFilterDemo {

    public static void main(String[] args) {
        bloomFilter();
    }

    public static void bloomFilter()    {
        // 创建布隆过滤器对象
        BloomFilter filter = BloomFilter.create(Funnels.integerFunnel(), 100);
        // 判断指定元素是否存在
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));
        // 将元素添加进布隆过滤器
        filter.put(1);
        filter.put(2);
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));    }
}
