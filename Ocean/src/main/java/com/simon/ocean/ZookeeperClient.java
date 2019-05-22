package com.simon.ocean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.CollectionUtils;

/**
 * @author zhouzhenyong
 * @since 2019/5/18 下午10:01
 */
@Slf4j
public class ZookeeperClient {

    private static final String LOG_PRE = "[zookeeper]：";
    private static final int SESSION_TIMEOUT = 10000;
    /**
     * 跟路径
     */
    private String rootPath;
    private ZooKeeper zk = null;
    /**
     * 用于服务启动的同步
     */
    private CountDownLatch countDown = new CountDownLatch(1);
    /**
     * 链接的zk配置，用于重连时候用
     */
    private String connectString;
    private static ZookeeperClient zookeeperClient = new ZookeeperClient();
    /**
     * 观察者回调器
     */
    private CallbackWatcher watcher = new CallbackWatcher();
    /**
     * 节点的路径映射（根据调用端配置关注的部分）
     */
    private Map<String, Set<String>> nodePathMap = new HashMap<>();
    /**
     * 存储当前应用中的节点数据映射，key为路径，value为当前路径下的子节点路径，注意，里面的路径全都是全路径
     */
    private Map<String, BiConsumer<NodeChgEnum, String>> pathChgWatchMap = new HashMap<>();
    /**
     * zk节点删除和新增的回调
     */
    private BiConsumer<String, Pair<String, String>> addAndDeleteHook;

    public static ZookeeperClient getInstance(){
        if(null == zookeeperClient){
            synchronized (ZookeeperClient.class){
                if(null == zookeeperClient){
                    zookeeperClient = new ZookeeperClient();
                }
            }
        }
        return zookeeperClient;
    }

    /**
     * 创建zookeeper的客户端
     */
    public ZookeeperClient connect(String connectString) {
        this.close();
        try {
            this.connectString = connectString;
            zk = new ZooKeeper(connectString, SESSION_TIMEOUT, watcher);
            countDown.await();
        } catch (InterruptedException e) {
            log.error(LOG_PRE + "连接创建失败，发生 InterruptedException");
            e.printStackTrace();
        } catch (IOException e) {
            log.error(LOG_PRE + "连接创建失败，发生 IOException");
            e.printStackTrace();
        }
        return zookeeperClient;
    }

    /**
     * 创建根节点
     * @param rootPath 根节点
     * @return 根节点数据
     */
    public ZookeeperClient addRoot(String rootPath) {
        this.rootPath = rootPath;
        addPersistentNode(rootPath);
        try {
            zk.getChildren(rootPath, true);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return zookeeperClient;
    }

    /**
     * 添加永久节点
     * @param nodePath 节点路径
     * @return zk的客户端类
     */
    public String addPersistentNode(String nodePath, String data){
        return createNode(nodePath, data, CreateMode.PERSISTENT);
    }

    /**
     * 添加永久临时节点
     * @param nodePath 父级节点路径
     * @return 新生成的节点路径
     */
    public String addPersistentSeqNode(String nodePath, String data){
        return createNode(nodePath, data, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    /**
     * 添加临时节点
     * @param nodePath 节点路径
     * @return 新生成的节点路径
     */
    public String addEphemeralNode(String nodePath, String data){
        return createNode(nodePath, data, CreateMode.EPHEMERAL);
    }

    /**
     * 添加临时节点
     * @param nodePath 节点路径
     * @return 新生成的节点路径
     */
    public String addEphemeralSeqNode(String nodePath, String data){
        return createNode(nodePath, data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * 添加永久节点
     * @param nodePath 节点路径
     * @return zk的客户端类
     */
    public String addPersistentNode(String nodePath){
        return createNode(nodePath, "", CreateMode.PERSISTENT);
    }

    /**
     * 添加永久临时节点
     * @param nodePath 父级节点路径
     * @return 新生成的节点路径
     */
    public String addPersistentSeqNode(String nodePath){
        return createNode(nodePath, "", CreateMode.PERSISTENT_SEQUENTIAL);
    }

    /**
     * 添加临时节点
     * @param nodePath 节点路径
     * @return 新生成的节点路径
     */
    public String addEphemeralNode(String nodePath){
        return createNode(nodePath, "", CreateMode.EPHEMERAL);
    }

    /**
     * 添加临时节点
     * @param nodePath 节点路径
     * @return 新生成的节点路径
     */
    public String addEphemeralSeqNode(String nodePath){
        return createNode(nodePath, "", CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * 添加观察者
     * @param namespace 空间
     * @param callback 回调信息：key为节点的修改类型， value为节点中的值
     * @return zk的客户端类
     */
    public ZookeeperClient addWatch(String namespace, BiConsumer<NodeChgEnum, String> callback) {
        String namespacePath = rootPath + "/" + namespace;
        pathChgWatchMap.putIfAbsent(namespacePath, callback);
        addWatchChildren(namespacePath);
        return zookeeperClient;
    }

    /**
     * 添加只关注的路径下的一些数据的修改
     * @param watchPath 要关注的路径列表
     */
    public void addWatchPath(String... watchPath) {
        Stream.of(watchPath).forEach(w -> nodePathMap.compute(w, (k, v) -> {
            List<String> nodeList = new ArrayList<>();
            try {
                nodeList = zk.getChildren(w, true).stream().map(r -> w + "/" + r).collect(Collectors.toList());
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
            if (null == v) {
                addWatchPath(nodeList.toArray(new String[]{}));
                return new HashSet<>(nodeList);
            } else {
                v.addAll(nodeList);
                addWatchPath(nodeList.toArray(new String[]{}));
                return v;
            }
        }));
    }

    /**
     * 修改zk中的数据
     * @param nodePath 节点的全路径
     * @param data 对应的数据
     * @return zk的客户端类
     */
    public ZookeeperClient writeNodeData(String nodePath, String data){
        try {
            this.zk.setData(nodePath, data.getBytes(), -1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return zookeeperClient;
    }

    /**
     * zk的数据节点变更时候的回调
     *
     * @param addAndDeleteHook key为路径，value中的key为增加的节点，value中的value为删除的节点
     */
    public void registerNodeAddAndDeleteHook(BiConsumer<String, Pair<String, String>> addAndDeleteHook){
        this.addAndDeleteHook = addAndDeleteHook;
    }

    /**
     * 刷新节点列表，一般用于数据回调之后
     */
    public void refreshNode(String nodePath, Set<String> nodeList){
        nodePathMap.put(nodePath, nodeList);
    }

    public void refreshNode(String nodePath){
        nodePathMap.put(nodePath, new TreeSet<>(getChildrenPathList(nodePath)));
    }

    public void rmrNode(String nodePath){
        nodePathMap.remove(nodePath);
    }

    /**
     * 向对应的nodeMap中添加对应的节点
     * @param nodePath node路径
     */
    public void addNode(String nodePath, String node) {
        nodePathMap.compute(nodePath, (k, v) -> {
            if (null == v) {
                TreeSet<String> valueSet = new TreeSet<>();
                valueSet.add(node);
                return valueSet;
            } else {
                v.add(node);
                return v;
            }
        });
    }

    public class CallbackWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            log.info(LOG_PRE + "---------------------start-------------------");
            KeeperState state = event.getState();
            EventType type = event.getType();
            String path = event.getPath();

            log.info(LOG_PRE + "收到Watcher通知");
            log.info(LOG_PRE + "连接状态:\t" + state.toString());
            log.info(LOG_PRE + "事件类型:\t" + type.toString());
            log.info(LOG_PRE + "path:\t" + path);
            if (KeeperState.SyncConnected == state) {
                // 成功连接上ZK服务器
                if (EventType.None == type) {
                    log.info(LOG_PRE + "成功连接上ZK服务器");
                    countDown.countDown();
                }
                //更新子节点
                else if (EventType.NodeChildrenChanged == type) {
                    log.info(LOG_PRE + "子节点变更");
                    childrenFresh(path);
                }
            }
            else if (KeeperState.Disconnected == state) {
                log.info(LOG_PRE + "与ZK服务器断开连接");
            }
            else if (KeeperState.Expired == state) {
                log.info(LOG_PRE + "会话失效，重新建立连接");

                // 重新创建连接
                connect(connectString);
            }
            log.info(LOG_PRE + "---------------------end-------------------");
        }
    }

    /**
     * 关闭ZK连接
     */
    public void close() {
        if (null != this.zk) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取对应路径的子节点名称列表
     */
    public List<String> getChildrenPathList(String path){
        try {
            return zk.getChildren(path, false).stream().map(r -> path + "/" + r).collect(Collectors.toList());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 添加对应路径对应的子路径
     *
     * @param path 待监控的路径
     */
    public void addWatchChildren(String path) {
        try {
            zk.getChildren(path, true);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取指定节点数据内容
     *
     * @param path 路径
     */
    public String readData(String path) {
        try {
            log.info(LOG_PRE + "获取数据成功，path：" + path);
            return new String(this.zk.getData(path, false, null));
        } catch (KeeperException e) {
            log.info(LOG_PRE + "读取数据失败，发生KeeperException，path: " + path);
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            log.info(LOG_PRE + "读取数据失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
            return "";
        }
    }

    public enum NodeChgEnum{
        /**
         * 节点删除
         */
        DELETE,
        /**
         * 节点新增
         */
        ADD;
    }

    /**
     * 根据路径获取节点的增加和删除的节点数据
     */
    public Pair<String, String> getAddAndDeleteSet(String path){
        return getAddAndDeleteSet(new TreeSet<>(nodePathMap.get(path)), childrenSet(path));
    }

    private TreeSet<String> childrenSet(String path){
        return new TreeSet<>(getChildrenPathList(path));
    }

    /**
     * 获取子节点数据
     * @param path 指定路径下的子节点发生了变更
     */
    private void childrenFresh(String path) {
        // 用于其他服务有变更时候的回调
        serverListChg(path, childrenSet(path));

        // 服务刷新后，继续增加对这个节点的子节点监控
        addWatchChildren(path);
    }

    /**
     * 在服务节点发生变更的时候，更新本地哈希数据和任务调度
     *
     * @param newServerSet 新的服务列表
     */
    private void serverListChg(String path, TreeSet<String> newServerSet) {
        log.info(LOG_PRE + "所有子节点：" + newServerSet);

        if (null != addAndDeleteHook) {
            addAndDeleteHook.accept(path, getAddAndDeleteSet(new TreeSet<>(nodePathMap.get(path)), newServerSet));
        }

        nodePathMap.put(path, newServerSet);
    }

    /**
     * 获取新旧服务名字对比，查看哪些是新增的，哪些是删除的
     * @param oldServerSet 旧的服务名字列表
     * @param newServerSet 新的服务名字列表
     * @return 删除的服务集合和新增的服务集合
     */
    private Pair<String, String> getAddAndDeleteSet(TreeSet<String> oldServerSet, TreeSet<String> newServerSet){
        String addStr = null;
        String removeStr = null;
        if (!CollectionUtils.isEmpty(oldServerSet) && !CollectionUtils.isEmpty(newServerSet)){
            if(newServerSet.containsAll(oldServerSet)){
                addStr = newServerSet.stream().filter(s->!oldServerSet.contains(s)).findFirst().get();
            } else if(oldServerSet.containsAll(newServerSet)){
                removeStr = oldServerSet.stream().filter(s->!newServerSet.contains(s)).findFirst().get();
            }
        } else {
            if (!CollectionUtils.isEmpty(newServerSet)) {
                addStr = newServerSet.first();
            }

            if(!CollectionUtils.isEmpty(oldServerSet)){
                removeStr = oldServerSet.first();
            }
        }
        return new Pair<>(addStr, removeStr);
    }

    /**
     * 给这个节点中创建临时节点
     */
    private String createNode(String node, String data, CreateMode createMode) {
        try {
            if (null == this.zk.exists(node, false)) {
                String realPath = zk.create(node, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info(LOG_PRE + "节点创建成功, Path: " + realPath);
                return realPath;
            } else {
                log.info(LOG_PRE + "节点" + node + "已经存在");
                return node;
            }
        } catch (KeeperException e) {
            log.info(LOG_PRE + "节点创建失败，发生KeeperException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            log.info(LOG_PRE + "节点创建失败，发生 InterruptedException");
            e.printStackTrace();
        }
        return null;
    }
}

