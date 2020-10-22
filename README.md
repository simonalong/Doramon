
# Doraemon
用于存放平常常用的一些个人工具：

* [1.一键式生成整个前后端工具](#一键式生成整个前后端工具)
* [2.一致性哈希工具](#一致性哈希工具)
* [3.zookeeper客户端使用工具](#zookeeper客户端使用工具)
* [4.高性能单机幂等工具](#高性能单机幂等工具)
* [5.对象尺寸计算工具](#对象尺寸计算工具)
* [6.BitMap工具](#BitMap工具)
* [7.时间差字符展示工具](#时间差字符展示工具)
* [8.JSON格式化展示工具](#JSON格式化展示工具)
* [10.文件读取工具](#文件读取工具)
* [11.数据非可逆压缩](#数据非可逆压缩)
* [12.身份证解析工具](#身份证解析工具)
* [13.分布式全局id](#分布式全局id)
* [14.jar包动态加载工具](#jar包动态加载工具)
* [15.布隆过滤器计算工具](#布隆过滤器计算工具)
* [16.yaml与properties和json转换工具](#yaml与properties和json转换工具)
* [17.LocalDateTimeUtil工具](#LocalDateTimeUtil工具)

<a name="Zefy4"></a>
<h2 id="一键式生成整个前后端工具">1.一键式生成整个前后端工具</h2>
<a name="YDA1Y"></a>

#### 模块：

simba和simba-web
<a name="J1Zxs"></a>
#### 技术：
模板引擎：Freemarker<br />后端:<br />Orm：[Neo框架](https://github.com/SimonAlong/Neo)<br />框架：spring-boot 2.0.4<br />前端：ant-design-pro
<a name="FfBcR"></a>
#### 说明：
该工具执行完成之后，可以直接生成一个具备这样功能的控制台，增删改查，分页查询，界面搜索字段可以配置（根据字段是普通的还是枚举还是时间，分别对应输入框、下拉框和时间范围选择框），此外，如果字段是图片，还可以配置成图片展示，如果是时间则按照时间展示。<br />该工具分为两个类，前端一个后端一个，生成时候分别配置响应的参数，启动即可生成响应的配置，前端尽量使用simba-web这个作为基础模块，后端只需要创建好完全的空项目即可生成可执行的，但是maven的pom中需要引入如下几个。前后端执行完之后，运行，即可具备增删改查分页等各种常见的功能

注意：
该工具由于功能太多，因此独立出来为
[Simba](https://github.com/SimonAlong/Simba)：脚手架工具
[Simba-base-front](https://github.com/SimonAlong/Simba-base-front)：脚手架工具中的前端模板

<a name="ELleo"></a>
<h2 id="一致性哈希工具">2.一致性哈希工具</h2>
<a name="4UlGk"></a>

#### 模块：
Ocean
<a name="4OoeL"></a>
#### 技术：
类：ConsistentHashUtil<br />算法：一致性哈希算法
<a name="aEMQP"></a>
#### 说明：
该工具提供了这么些方法供外部调用

```java
// 服务的注册
public void registerServer(String serverName) {}
// 服务的删除
public void deleteServer(String serverName){}
// 判断id是否是位于对应的服务名字下的
public boolean judgeBelongTo(String targetServerName, Long id) {}
// 判断当前节点是否是最小的那个节点
public boolean isMinNode(String serverName){}
// 重新初始化数据
public void initServerRange(Map<String, ServerNode> serverRange){}

/**
* 注册拆分的回调
* @param splitHook pair：key为新增的服务名，value为被拆分的服务名
*/
public void registerSplitHook(BiConsumer<Pair<String, String>, ControlRange> splitHook){}
/**
* 注册合并的回调
* @param mergeHook pair：key为合并后的服务名，value为被删除的服务名
*/
public void registerMergeHook(BiConsumer<Pair<String, String>, ControlRange> mergeHook){}
```

<a name="GR5g0"></a>
<h2 id="zookeeper客户端使用工具">3.zookeeper客户端使用工具</h2>
<a name="d1z74"></a>

#### 模块：
Ocean
<a name="6pIPn"></a>
#### 技术：
类：zookeeperClient
```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
</dependency>
```
<a name="7laDq"></a>
#### 说明：
提供链接和创建节点（永久节点，临时节点，有序节点）<br />提供对指定路径的监听回调

```java
// 单例模式
public static ZookeeperClient getInstance(){}
// 链接
public ZookeeperClient connect(String connectString) {}

// 添加根节点
public ZookeeperClient addRoot(String rootPath) {}
// 添加永久节点
public String addPersistentNode(String nodePath, String data){}
// 添加永久有序节点
public String addPersistentSeqNode(String nodePath, String data){}
// 添加临时节点
public String addEphemeralNode(String nodePath, String data){}
// 添加临时有序节点
public String addEphemeralSeqNode(String nodePath, String data){}

// 对指定路径添加监控
public void addWatch(String path){}
// 添加对应路径对应的子路径监控
public void addWatchChildren(String path) {}
// 添加监控路径，以及包括其子节点和子节点的子节点等等里面的所有节点都监控
public void addWatchPath(String... watchPath) {}

// 还有更多，不过还有些功能不完整，后面还要继续补充
```

<a name="Z3kVV"></a>
<h2 id="高性能单机幂等工具">4.高性能单机幂等工具</h2>
<a name="fN3AH"></a>

#### 模块：
Ocean
<a name="1bK16"></a>
#### 技术：
类：Idempotency
<a name="loVP7"></a>
#### 说明：
该工具提供的是单机的幂等性，可以用于在一定时间（自定义的过期时间）内的存储量到100万条数据，占用内存（222.2M），此外如果还有更多数据，还可以提供外部系统存储和判断。
```java
// 单例
public static Idempotency getInstance() {}

// 判断当前是否含有对应的数据，不包含则将对应的数据插入到缓存中
public boolean contain(Object... object) {}

// 设置数据的实效性，超过这个时间就会失效，失效之后，如果还有这样的消息过来，则认为新的消息是OK的
// 默认设置为20秒，向后延长20的mills
public Idempotency setExpire(Integer num, TimeUnit timeUnit) {}

// 提供可修改的最大值，默认100万
public Idempotency setMaxDataSize(Integer size){}

// 注册第三方存储的数据插入回调
public Idempotency registerInsertHook(BiConsumer<String, Long> insertHook){}
// 注册第三方存储的数据删除回调，
public Idempotency registerClearExpireHook(Runnable clearExpireHook){}
// 注册第三方存储的数据选择回调
public Idempotency registerSelectHook(Function<String, Long> selectHook){}
// 注册第三方存储的数据是否为空的回调
public Idempotency registerIsEmptyHook(Supplier<Boolean> isEmptyHook){}
```

<a name="A5iCX"></a>
<h2 id="对象尺寸计算工具">5.对象尺寸计算工具</h2>
<a name="BFJtR"></a>

#### 模块：
Ocean
<a name="US2HG"></a>
#### 技术：
类：SizeUtil
<a name="bTHnN"></a>
#### 说明：
只是利用lucence的工具包，简单封装了下
<a name="eQpEH"></a>
<h2 id="BitMap工具">6.BitMap工具</h2>
<a name="8VMRy"></a>

#### 模块：
Ocean
<a name="t1F2O"></a>
#### 技术：
类：BitMap
<a name="6D5J0"></a>
#### 说明：
该工具类算是布隆过滤器在数据为int时候的简化和优化版，内存可以更小，存储int，则可以通过128 * 2^20 个int即可存储所有的int数据，即2^32条数据，即大概40亿数据用512MB存储即可。<br />功能：<br />1.添加数据<br />2.清理数据<br />3.判断是否存在<br />4.插入数据的个数
```java
// 单例模式
public static BitMap getInstance(){}

// 设置最大值，不设置，则用默认为128*2^20个int存储
public void setMaxValue(int size){}
// 数据插入
public void insert(int data) {}
// 数据判断
public boolean contain(int data){}
// 数据删除
public void delete(int data) {}
// 个数统计
public int count() {}
```

<a name="wFchM"></a>
<h2 id="时间差字符展示工具">7.时间差字符展示工具</h2>
<a name="NDZFk"></a>

#### 模块：
Ocean
<a name="eE2hf"></a>
#### 技术：
类：TimeStrUtil
<a name="l7O6y"></a>
#### 说明：
主要用于计算两个时间之间的差值，毫秒，秒，分钟，小时，天，周之间的转换，主要是字符的展示，在一些展示型的界面用
```java
// 举例：4天 1分钟 12秒 132毫秒
public String parseDuration(Date date1, Date date2) {}
public String parseDurationWeek(Date date1, Date date2) {}
public String parseTime(long time) {}
// 举例：1周 1分钟 12秒 132毫秒
public String parseWeeks(long time) {}
```

<a name="0w4rz"></a>
<h2 id="JSON格式化展示工具">8.JSON格式化展示工具</h2>
<a name="SYJKg"></a>

#### 模块：
Ocean
<a name="avWjP"></a>
#### 技术：
类：StringTypeUtil
<a name="urMJ2"></a>
#### 说明：
主要是将json格式化的那个压缩的进行有格式化的展示

```java
public String parseJson(String str) {}
```

<a name="8PwSJ"></a>
<h2 id="文件读取工具">10.文件读取工具</h2>
<a name="TcvAw"></a>

#### 模块：
Ocean
<a name="iefyo"></a>
#### 技术：
类：FileUtil
<a name="9T8En"></a>
#### 说明：
对常见文件的各种读写
<a name="EWYCM"></a>
<h2 id="数据非可逆压缩">11.数据非可逆压缩</h2>
<a name="mB2mS"></a>

#### 模块：
Ocean
<a name="vpDM9"></a>
#### 技术：
类：EncryUtil
<a name="3tobe"></a>
#### 说明：
提供对SHA-256、SHA-512和MD5三种非可逆的压缩方式
<a name="LUl3Q"></a>
<h2 id="身份证解析工具">12.身份证解析工具</h2>
<a name="hVsAW"></a>

#### 模块：
Ocean
<a name="ghetN"></a>
#### 技术：
类：IdCardParser
<a name="1Rva4"></a>
#### 说明：
提供身份证号码，可以通过身份证解析各种信息<br />1.解析地址：比如：安徽省合肥市庐阳区<br />2.解析生日：比如：19890312<br />3.解析男女：比如：男<br />4.解析星座（默认上面生日是阳历，如果是阴历则不准确）：比如：双鱼座<br />5.解析年龄：比如：23<br />6.检验身份证号有效性：

```java
    /**
     * 对象方法的所有用法
     */
    @Test
    @SneakyThrows
    public void testParser(){
        // 随便写的一个身份证号
        String idCard = "150125199002027411";
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance();
        // 获取全国数据省市对应图
        IdCardParser.initFromContent(FileUtil.readFromResource(this.getClass(), "/data/idAddress.json"));
        helper.setIdCard(idCard);

        // 身份证号的可用性：true
        show(helper.valid());
        // 解析地址，前提是函数initFromContent 调用，并将省市数据注入进去；返回：内蒙古自治区呼和浩特市武川县
        show(helper.parseAddress());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 解析生日信息：返回：1990-02-02 00:00:00
        show(format.format(helper.parseBirthday()));

        // 解析性别：男，女，未知；返回：男
        show(helper.parseGender());
        // 解析星座：返回：水瓶座
        show(helper.parseConstellation());
        // 解析年龄：29
        show(helper.parseAge());
    }
```
<a name="srNyL"></a>
<h2 id="分布式全局id">13.分布式全局id</h2>
<a name="teM92"></a>

该分布式id生成器，彻底解决了雪花算法存在的三个问题：
1.时间回拨问题
2.workerId的分配和回收问题
3.workerId的上限问题

其中业内对于前两个问题的解决方式都不是很完美，或者说是没有完全解决，而第三个问题，则就完全没有考虑。在这里Butterfly对上面三个问题进行了彻底的解决。算是完美解决了雪花算法存在的所有问题，而且性能比雪花算法还要高。

注意：
由于该功能方案比较复杂，已经独立出去，重新命名为Butterfly
[Butterfly](https://github.com/SimonAlong/Butterfly)

<a name="SatLe"></a>
<h2 id="jar包动态加载工具">14.jar包动态加载工具</h2>
<a name="VLpS7"></a>

#### 模块：
Ocean
<a name="P3QxH"></a>
#### 技术：
类：TeaCup
<a name="UTSOt"></a>
#### 说明：
用于动态的将jar包加载到系统中，然后就可以使用Class.forname进行对类进行加载操作

```java
// 单例化
public static TeaCup getInstance() {}

// 载入类
public Class<?> loadClass(String clsName){}

// 通过不同的方式将jar包读入进来
public void read(String jarRootPath) {}
public void read(URL url) {}
public void read(File file) {}
public void read(String... jarPath) {}
public void read(URL... urls) {}
public void read(File... jarPath) {}
```

<a name="1x5yi"></a>
<h2 id="布隆过滤器计算工具">15.布隆过滤器计算工具</h2>
<a name="VmVb1"></a>

#### 模块：
Ocean
<a name="09GJw"></a>
#### 技术：
类：zookeeperClient
<a name="XE5HE"></a>
#### 说明：
该工具类主要用于计算布隆过滤器的数组大小和函数的个数

```java
// 获取位数组的大小, n：实际数据大小， p：误判率
public long getBitsSize(long n, double p) {}
// long 获取哈希函数的最优个数， m:bitSize 位数组的bit个数, n:实际数据大小
public long getHashNum(long m, long n) {}
```


<a name="aeNfY"></a>
<h2 id="yaml与properties和json转换工具">16.yaml与properties和json转换工具</h2>
<a name="kOjzS"></a>

#### 模块：
Ocean
<a name="l2GKM"></a>
#### 技术：
类：YamlUtil
<a name="nqOGW"></a>
#### 说明：
该工具类主要用于将Yaml、properties、json和kv格式的转换
<a name="UhuSg"></a>
### properties 转 yml
```java
// properties 转yml
public String propertiesToYml(String propertiesContent) {}

// yml 转 properties
public String ymlToProperties(String ymlContent) {}

// json 转 yml
public String jsonToYml(String jsonContent) {

// yml 转 json
public String ymlToJson(String ymlContent) {}

/**
 * k-v的String类型转properties
 *
 * <p>其中key可能是a.b.c这种，而value可能是各种各样的类型，我们这里通过valueType进行区分
 *
 * @param key           主键
 * @param value         待转换的值
 * @param valueTypeEnum 值的类型，0：yml，1：properties，2：json，3：string
* @return 转换之后的yml类型
*/
public String kvToProperties(String key, String value, YamlUtil.ConfigValueTypeEnum valueTypeEnum) {}
```

<br />例子
```java
/**
 * 基本测试
 *
 * a.b.c.d.e=1
 * a.b1.c1.d1.e1=1
 */
@SneakyThrows
@Test
public void propertiesToYmlBaseTest() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/base.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/base.yml");

    //a:
    //  b:
    //    c:
    //      d:
    //        e: 1
    //  b1:
    //    c1:
    //      d1:
    //        e1: 1
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 基本测试：带注释
 *
 * # 总的
 * # test
 * a.b.c=1
 * # 测试1
 * a.b1.c.d.e=1
 * # 用来说明xxx
 * a.b2.c1.d1.e1=1
 * a.b2.c1.d1.e2=2
 * a.b2.c1.d1.e3=3
 * # 数组
 * a.b2.c1.d2[0]=3
 * a.b2.c1.d2[1]=3
 * a.b2.c1.d2[2]=3
 */
@SneakyThrows
@Test
public void propertiesToYmlBase1Test() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/base1.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/base1.yml");

    //# 总的
    //a:
    //  b:
    //    c: 1 # test
    //  b1:
    //    c:
    //      d:
    //        e: 1 # 测试1
    //  b2:
    //    c1:
    //      d1:
    //        e1: 1 # 用来说明xxx
    //        e2: 2
    //        e3: 3
    //      # 数组
    //      d2:
    //        - 3
    //        - 3
    //        - 3
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试1
 *
 * a.b.c[0].d=1
 * a.b.c[1].e=2
 * a.b.c[2].e=3
 * a.b.d.e=4
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest1() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array1.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array1.yml");
    //a:
    //  b:
    //    c:
    //      - d: 1
    //      - e: 2
    //      - e: 3
    //    d:
    //      e: 4
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试2
 *
 * a.b.c[0].d=1
 * a.b.c[0].e=2
 * a.b.c[0].f=3
 * a.b.c[1].d=4
 * a.b.c[1].e=5
 * a.b.c[1].f=6
 * a.b.d.e=7
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest2() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array2.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array2.yml");
    //a:
    //  b:
    //    c:
    //      - d: 1
    //        e: 2
    //        f: 3
    //      - d: 4
    //        e: 5
    //        f: 6
    //    d:
    //      e: 7
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试3：多级数组
 *
 * a.b.c[0].d=1
 * a.b.c[1].e[0]=2
 * a.b.c[1].e[1]=3
 * a.b.c[2].e[0]=4
 * a.b.d.e=5
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest3() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array3.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array3.yml");
    //a:
    //  b:
    //    c:
    //      - d: 1
    //      - e:
    //          - 2
    //          - 3
    //      - e:
    //          - 4
    //    d:
    //      e: 5
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试4
 *
 * a.b.c[0]=1
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest4() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array4.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array4.yml");
    //a:
    //  b:
    //    c:
    //      - 1
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试5
 *
 * a.b.c[0].d.e=1
 * a.b.c[0].d.f=2
 * a.b.c[1].d.e.f=3
 * a.b.c[2].e=4
 * a.b.c[3]=5
 * a.b.d.e=6
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest5() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array5.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array5.yml");
    // a:
    //  b:
    //    c:
    //      - d:
    //          e: 1
    //          f: 2
    //      - d:
    //          e:
    //            f: 3
    //      - e: 4
    //      - 5
    //    d:
    //      e: 6
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试6
 *
 * a.b.e[0]=2
 * a.b.d.e=3
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest6() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array6.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array6.yml");
    //a:
    //  b:
    //    e:
    //      - 2
    //    d:
    //      e: 3
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 数组测试7：数组（带字符的）测试
 *
 * knowledge.init.knowledgeTitles[0].kdTitle=听不清
 * knowledge.init.knowledgeTitles[0].keyWords=[你说什么，没听清，听不清楚，再说一遍]
 * knowledge.init.knowledgeTitles[0].question=[没听懂，听不清楚]
 * knowledge.init.knowledgeTitles[1].kdInfos[0]=你好
 * knowledge.init.knowledgeTitles[1].kdInfos[1]=hello
 * knowledge.init.knowledgeTitles[1].kdInfos[2]=hi
 */
@SneakyThrows
@Test
public void propertiesToYmlArrayTest7() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array7.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array7.yml");

    //knowledge:
    //  init:
    //    knowledgeTitles:
    //      - kdTitle: 听不清
    //        keyWords: '[你说什么，没听清，听不清楚，再说一遍]'
    //        question: '[没听懂，听不清楚]'
    //      - kdInfos:
    //        - 你好
    //        - hello
    //        - hi
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}

/**
 * 多行数据测试
 *
 * isc.log.hosts=root:dell@123:10.30.30.33:22\
 * root:dell@123:10.30.30.34:22\
 * root:dell@123:10.30.30.35:22
 */
@SneakyThrows
@Test
public void propertiesToYmlMultiLineTest() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/multi_line.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/multi_line.yml");

    //isc:
    //  log:
    //    hosts: |
    //      root:dell@123:10.30.30.33:22
    //      root:dell@123:10.30.30.34:22
    //      root:dell@123:10.30.30.35:22
    Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
}
```
<a name="TVVHn"></a>
### yml转properties
```java
/**
 * 基本测试
 *
 * a:
 *   b:
 *     c:
 *       d:
 *         e: 1
 *   b1:
 *     c1:
 *       d1:
 *         e1: 1
 */
@SneakyThrows
@Test
public void ymlToPropertiesBaseTest() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/base.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/base.yml");
    //a.b.c.d.e=1
    //a.b1.c1.d1.e1=1
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 基本测试：带注释
 *
 * # 总的
 * a:
 *   b:
 *     c: 1 # test
 *   b1:
 *     c:
 *       d:
 *         e: 1 # 测试1
 *   b2:
 *     c1:
 *       d1:
 *         e1: 1 # 用来说明xxx
 *         e2: 2
 *         e3: 3
 *       # 数组
 *       d2:
 *         - 3
 *         - 3
 *         - 3
 */
@SneakyThrows
@Test
public void propertiesToYmlBase1Test() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/base1.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/base1.yml");

    //# 总的
    //# test
    //a.b.c=1
    //# 测试1
    //a.b1.c.d.e=1
    //# 用来说明xxx
    //a.b2.c1.d1.e1=1
    //a.b2.c1.d1.e2=2
    //a.b2.c1.d1.e3=3
    //# 数组
    //a.b2.c1.d2[0]=3
    //a.b2.c1.d2[1]=3
    //a.b2.c1.d2[2]=3
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试
 *
 * a:
 *   b:
 *     c:
 *       - d: 1
 *       - e: 2
 *       - e: 3
 *     d:
 *       e: 4
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest1() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array1.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array1.yml");
    //a.b.c[0].d=1
    //a.b.c[1].e=2
    //a.b.c[2].e=3
    //a.b.d.e=4
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试2
 *
 * a:
 *   b:
 *     c:
 *       - d: 1
 *         e: 2
 *         f: 3
 *       - d: 4
 *         e: 5
 *         f: 6
 *     d:
 *       e: 7
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest2() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array2.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array2.yml");
    //a.b.c[0].d=1
    //a.b.c[0].e=2
    //a.b.c[0].f=3
    //a.b.c[1].d=4
    //a.b.c[1].e=5
    //a.b.c[1].f=6
    //a.b.d.e=7
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试3：多级数组
 *
 * a:
 *   b:
 *     c:
 *       - d: 1
 *       - e:
 *           - 2
 *           - 3
 *       - e:
 *           - 4
 *     d:
 *       e: 5
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest3() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array3.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array3.yml");
    //a.b.c[0].d=1
    //a.b.c[1].e[0]=2
    //a.b.c[1].e[1]=3
    //a.b.c[2].e[0]=4
    //a.b.d.e=5
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试4
 *
 * a:
 *   b:
 *     c:
 *       - 1
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest4() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array4.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array4.yml");
    //a.b.c[0]=1
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试5
 *
 * a:
 *   b:
 *     c:
 *       - d:
 *           e: 1
 *           f: 2
 *       - d:
 *           e:
 *             f: 3
 *       - e: 4
 *       - 5
 *     d:
 *       e: 6
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest5() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array5.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array5.yml");
    //a.b.c[0].d.e=1
    //a.b.c[0].d.f=2
    //a.b.c[1].d.e.f=3
    //a.b.c[2].e=4
    //a.b.c[3]=5
    //a.b.d.e=6
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试6
 *
 * a:
 *   b:
 *     e:
 *       - 2
 *     d:
 *       e: 3
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest6() {
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array6.properties");
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array6.yml");
    //a.b.e[0]=2
    //a.b.d.e=3
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 数组测试7：带字符
 *
 * knowledge:
 *   init:
 *     knowledgeTitles:
 *       - kdTitle: 听不清
 *         keyWords: '[你说什么，没听清，听不清楚，再说一遍]'
 *         question: '[没听懂，听不清楚]'
 *       - kdInfos:
 *         - 你好
 *         - hello
 *         - hi
 */
@SneakyThrows
@Test
public void ymlToPropertiesArrayTest7() {
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/array7.yml");
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/array7.properties");

    //knowledge.init.knowledgeTitles[0].kdTitle=听不清
    //knowledge.init.knowledgeTitles[0].keyWords=[你说什么，没听清，听不清楚，再说一遍]
    //knowledge.init.knowledgeTitles[0].question=[没听懂，听不清楚]
    //knowledge.init.knowledgeTitles[1].kdInfos[0]=你好
    //knowledge.init.knowledgeTitles[1].kdInfos[1]=hello
    //knowledge.init.knowledgeTitles[1].kdInfos[2]=hi
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}

/**
 * 多行测试
 *
 * isc:
 *   log:
 *     hosts: |
 *       root:dell@123:10.30.30.33:22
 *       root:dell@123:10.30.30.34:22
 *       root:dell@123:10.30.30.35:22
 */
@SneakyThrows
@Test
public void ymlToPropertiesMultiLineTest() {
    String ymlContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/yml/multi_line.yml");
    String propertiesContent = FileUtil.readFromResource(YamlUtilYmlToPropertiesTest.class, "/properties/multi_line.properties");

    //isc.log.hosts=root:dell@123:10.30.30.33:22\
    //root:dell@123:10.30.30.34:22\
    //root:dell@123:10.30.30.35:22
    Assert.assertEquals(propertiesContent.trim(), YamlUtil.ymlToProperties(ymlContent).trim());
}
```

<a name="aeNfY"></a>
<h2 id="LocalDateTimeUtil工具">17.LocalDateTimeUtil工具</h2>
<a name="kOjzS"></a>

主要转换的有如下
```text
 1.LocalDateTime ----> LocalDate
 2.LocalDateTime ----> Long
 3.LocalDateTime ----> Date
 4.LocalDateTime ----> String

 1.LocalDate ----> LocalDateTime
 2.LocalDate ----> Long
 3.LocalDate ----> Date
 4.LocalDate ----> String

 1.Date ----> LocalDateTime
 2.Date ----> LocalDate
 3.Date ----> Long
 4.Date ----> String

 1.Timestamp ----> LocalDateTime
 2.Timestamp ----> Long
 3.Timestamp ----> String
 4.Timestamp ----> LocalDate

 1.String ----> LocalDateTime
 2.String ----> LocalDate
 3.String ----> Date
 4.String ----> Timestamp
 5.String ----> LocalTime
 6.String ----> Time

 1.Long ----> Date
 2.Long ----> LocalDateTime
 3.Long ----> LocalDate
```
