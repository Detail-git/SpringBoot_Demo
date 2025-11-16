package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    private long total; //总记录数

    //这里的写法其实不安全，写成List<T> 更安全更规范，否则在实现类调用它接收数据时直接写 List<实际数据类型> records = ***时相当于是进行强制类型转换
    //这种 “跳过泛型、直接强转” 的写法(见DishServiceImpl)，相当于放弃了编译期的类型检查，把 “类型安全” 的保证交给了 “运行时运气”：
    //若数据逻辑严谨（列表确实只有 DishVO），代码能跑通；
    //若数据有污染（比如误存了 Dish 或其他对象），运行时会崩溃。
    private List records; //当前页数据集合

}
