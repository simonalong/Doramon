package com.simon.ocean;

import static com.simon.ocean.Out.*;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/5/22 下午2:18
 */
public class ConsistentHashUtilTest {

    private static ConsistentHashUtil service = ConsistentHashUtil.getInstance();
    @BeforeClass
    public static void before(){
        service.registerSplitHook((k,v)->{
            show("新服务" + k.getKey() + "接手旧服务" + k.getValue() + "范围" + v.toString());
        });

        service.registerMergeHook((k, v)->{
            show("服务" + k.getKey() + "接手服务" + k.getValue() + "（删除）的范围" + v.toString());
        });
    }

    @BeforeClass
    public static void after(){
        service.clear();
    }

    /**
     * 测试服务的注册
     */
    @Test
    public void testRegister() {
        tab();

        service.registerServer("1");

        tab();
        service.registerServer("2");

        tab();
        service.registerServer("3");

        tab();
        service.registerServer("4");

        tab();
        service.registerServer("5");

        tab();
        service.registerServer("6");

        tab();
        service.registerServer("7");

        tab();
    }

    /**
     * 测试服务的注销
     */
    @Test
    public void testServerRmv() {
        // 先注册进去一些服务
        testRegister();

        tab();
        // 删除服务3
        service.deleteServer("3");

        tab();
        // 删除服务7
        service.deleteServer("7");

        tab();
        // 删除服务6
        service.deleteServer("6");

        tab();
        // 删除服务1
        service.deleteServer("1");

        tab();
        // 删除服务4
        service.deleteServer("4");

        tab();
        // 删除服务5
        service.deleteServer("5");

        tab();
        // 删除服务2
        service.deleteServer("2");

        tab();

        // 重新注册看下
        testRegister();
    }

    /**
     * 这里用于数据的id查询位于哪个服务
     */
    @Test
    public void test3(){
        // 先注册进去一些服务
        testRegister();

        Long id = 234L;
        Assert.assertEquals("5", service.getServerName(id));
        show("ok");

        id=0L;
        Assert.assertEquals("1", service.getServerName(id));
        show("ok");

        id=511L;
        Assert.assertEquals("7", service.getServerName(id));
        show("ok");

        id=2675L;
        Assert.assertEquals("2", service.getServerName(id));
        show("ok");

        id=2047L;
        Assert.assertEquals("4", service.getServerName(id));
        show("ok");
    }

    /**
     * 测试服务的范围查询
     */
    @Test
    public void test4(){
        // 先注册进去一些服务
        testRegister();

        tab();
        show(service.getRange("1").toString());
        tab();
        show(service.getRange("2").toString());
        tab();
        show(service.getRange("3").toString());
        tab();
        show(service.getRange("4").toString());
        tab();
        show(service.getRange("5").toString());
        tab();
        show(service.getRange("6").toString());
        tab();
        show(service.getRange("7").toString());
        tab();
    }

//    /**
//     * 测试，删除数据后，在添加数据，和一次性输入数据的对比
//     */
//    @Test
//    public void test5(){
//        TreeSet<String> dataSet1 = new TreeSet<>(Arrays.asList("1", "2", "3", "4"));
//        service.registerServer(dataSet1);
//
//        tab();
//        // 删除服务2
//        service.deleteServer("2");
//
//
////        Map<String, ServerNode> nodeMap = new TreeMap<>();
////        Arrays.asList("1", "3", "4").forEach(f-> nodeMap.computeIfAbsent(f, s->new ServerNode(service.getServerNode(f))));
//
//        tab();
//        // 增加服务5
//        service.registerServer("5");
//
//
//        tab("全部清理");
//        service.delete();
//
//        tab();
//        service.initServerRange(nodeMap);
//        service.registerServer("5");
//
//        tab();
//    }

//    @Test
//    public void test6(){
//        TreeSet<String> dataSet2 = new TreeSet<>(Arrays.asList("3", "2", "1"));
//        service.registerServer(dataSet2);
//
//    }

    /**
     * 测试服务的注册
     */
    @Test
    public void test7() {
        tab();
        service.registerServer("3");
        service.registerServer("2");
        service.registerServer("1");

        tab();
        service.clear();
        tab("清理");
        tab();
        service.registerServer("1");
        service.registerServer("2");
        service.registerServer("3");

        tab();
    }

//
//    @Test
//    public void test8(){
//        service.registerServer("2");
//        service.registerServer("3");
//        service.registerServer("4");
//        service.registerServer("0");
//        service.registerServer("1");
//
//        tab();
//        tab("清理");
//        tab();
//        service.delete();
//        TreeSet<String> dataSet2 = new TreeSet<>(Arrays.asList("2", "3", "4", "0"));
//        service.registerServer(dataSet2);
//        service.registerServer("1");
//
//        tab();
//    }

}
