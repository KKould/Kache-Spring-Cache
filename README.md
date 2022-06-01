# Kache-Spring

源框架：[Kache: 持久化缓存代理](https://gitee.com/Kould/kache)

### 使用 | Use

#### **1、Kache依赖引入**

#### 2、Dao层写入注解

##### 示例：

**1**.pom文件引入:

```xml
<dependency>
  <groupId>io.gitee.kroup</groupId>
  <artifactId>Kache-Spring</artifactId>
</dependency>
```

**2**.其对应的**Dao层**的Mapper以及Dao方法添加注释：

- 搜索方法：@DaoSelect
  
  - 其对应的@DaoSelect的status默认为Status.BY_Field：
    - status = Status.BY_FIELD : 非ID查询方法
    - status = Status.BY_ID : ID查询方法

- 插入方法：@DaoInsert

- 更新方法：@DaoUpdate

- 删除方法：@DaoDelete

```java
@Repository
// 自动注册代理注解
@CacheEntity(Article.class)
public interface TagMapper extends BaseMapper<Tag> {

    @Select("select t.* from klog_article_tag at "
            + "right join klog_tag t on t.id = at.tag_id "
            + "where t.deleted = 0 AND at.deleted = 0 "
            + "group by t.id order by count(at.tag_id) desc limit #{limit}")
    @DaoSelect(status = Status.BY_FIELD)
    // 通过条件查询获取数据
    List<Tag> listHotTagsByArticleUse(@Param("limit") int limit);

    @DaoInsert
    // 批量新增方法（会导致数据变动）
    Integer insertBatch(Collection<T> entityList);
}
```

自定义配置或组件：

```java
// 以接口类型作为键值替换默认配置或增加额外配置
// 用于无额外参数的配置或组件加载
load(Class<?> interfaceClass, Object bean);

// 以接口类型作为键值替换默认配置组件
// 用于类似Kache中Strategy这样实例化时需要额外参数的组件
replace(Class<?> interfaceClass, Class<?> accomplishClass, Class<?>[] argsClass);

// 示例:使用Kache默认提供的额外AMQP异步删改策略实现
@Bean
public Kache kache() {
    return Kache.builder()
            // 新增Connection接口配置,并提供接口实例
            .load(Connection.class, factory.newConnection())
            // 替换默认Strategy接口,提供实现类的class类信息与对应的构造方法参数类信息
            .replace(Strategy.class, AmqpStrategy.class, new Class[]{IBaseCacheManager.class, Connection.class})
            .build();
}
```

application.yml参考配置

```yaml
#Kache各属性可修改值
kache:
   dao:
       base-time: 86400 // 缓存基本存活时间
       random-time: 600 // 缓存随机延长时间
       poolMaxTotal: 20 // Redis连接池最大连接数
       poolMaxIdle: 5   // Redis连接池最大Idle状态连接数
       casKeepTime: 1   // 幂等cas凭证删除时间
   interprocess-cache:
       enable: true // 进程间缓存是否开启
       size: 50     // 进程间缓存数量
   data-field:
       id: id       // 主键属性名
       name: records // 分页包装类等包装类对持久类的数据集属性名：如MyBatis-Plus中Page的records属性
   listener:
       enable: true  // 监听器是否开启
```
