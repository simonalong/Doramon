package com.simon.ocean;


import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 一致性哈希服务，用于服务于节点的管理，管理服务名字和数据范围的对应
 *
 * @author zhouzhenyong
 * @since 2019/1/28 下午4:12
 */
@Slf4j
public class ConsistentHashUtil {

    /**
     * 用可序化的线程安全map存储数据
     */
    private ConcurrentSkipListMap<String, ServerNode> serverNodeMap = new ConcurrentSkipListMap<>();
    /**
     * 表示哈希的管理范围多大
     */
    private static Integer SIZE_POWER = 10;
    /**
     * 哈希服务的最大值，同时也代表了服务个数的最大值，如果服务数据不多，则可以设置小一点
     */
    private static Integer HASH_MAX_SIZE = 1 << SIZE_POWER;

    private static ConsistentHashUtil instance = new ConsistentHashUtil();

    /**
     * 拆分回调的钩子，其中Pair类型中key为旧的服务名字，value为新的服务名字，Map中的value的ControlRange类型为对应的交接范围
     */
    BiConsumer<Pair<String, String>, ControlRange> splitHook;
    /**
     * 合并回调钩子，其中Pair类型中key为要删除的服务名字，value为接手这块服务的名字，Map中的value的ControlRange类型为对应的交接范围
     */
    BiConsumer<Pair<String, String>, ControlRange> mergeHook;

    private ConsistentHashUtil(){}

    public static ConsistentHashUtil getInstance(){
        return instance;
    }

    /**
     * 注册拆分的回调
     * @param splitHook pair：key为新增的服务名，value为被拆分的服务名
     */
    public void registerSplitHook(BiConsumer<Pair<String, String>, ControlRange> splitHook){
        this.splitHook = splitHook;
    }

    /**
     * 注册合并的回调
     * @param mergeHook pair：key为合并后的服务名，value为被删除的服务名
     */
    public void registerMergeHook(BiConsumer<Pair<String, String>, ControlRange> mergeHook){
        this.mergeHook = mergeHook;
    }

    /**
     * 服务的注册
     *
     * @param serverName 服务名字
     */
    public void registerServer(String serverName) {
        if(null == serverName || "".equals(serverName)){
            return;
        }

        if (null == serverNodeMap.get(serverName)){
            ServerNode node = new ServerNode();

            // 获取要拆分的节点
            ServerNode toSplitNode = getSplitNode();
            node.setServerName(serverName);
            String toSplitServerName = toSplitNode.getServerName();
            // 有可拆分的服务
            if (null != toSplitServerName) {
                node.setControlRange(splitControlRange(toSplitNode));
                node.setNeighborServer(splitNeighborServer(toSplitNode, serverName));

                // 更新原节点数据
                updateServerNode(toSplitNode);

                if (null != splitHook) {
                    // 先将数据放进去，下面回调中会用到
                    serverNodeMap.put(serverName, node);

                    // 新旧服务节点删除分离出去的范围
                    splitHook.accept(new Pair<>(serverName, toSplitServerName), node.getControlRange());
                }
            } else {
                // 没有可拆分的服务，则认为是第一个节点
                node.setControlRange(ControlRange.of(0, HASH_MAX_SIZE - 1));
                node.setNeighborServer(NeighborServer.builder().build());

                // 先将数据放进去，下面回调中会用到
                serverNodeMap.put(serverName, node);

                // 针对初始化的节点，进行初始化回调
                splitHook.accept(new Pair<>(serverName, null), ControlRange.of(0, HASH_MAX_SIZE - 1));
            }
        }
    }

    /**
     * 服务的删除，一般用于其他服务不可用，则需要刷新映射
     * @param serverName 服务的名字
     */
    public void deleteServer(String serverName){
        if(null == serverName || "".equals(serverName)){
            return;
        }

        ServerNode toDelNode = serverNodeMap.get(serverName);
        if(null != toDelNode) {
            // 返回新的合并之后的服务节点
            ServerNode newServerNode = mergeControlRange(toDelNode);
            if (null != newServerNode) {
                mergeNeighborServer(toDelNode);
                serverNodeMap.remove(serverName);

                if (null != mergeHook) {
                    // 调用回调
                    mergeHook.accept(new Pair<>(newServerNode.getServerName(), serverName), toDelNode.getControlRange());
                }
            } else {
                serverNodeMap.remove(serverName);
            }
        }
    }

    /**
     * 通过数据的id判断，返回管理该条数据的服务名字
     * @param id 某个实际数据的id
     */
    public String getServerName(Long id){
        Integer index = getIndex(id);

        Set<Entry<String, ServerNode>> entrySet = serverNodeMap.entrySet();
        for (Entry<String, ServerNode> entry : entrySet){
            ControlRange controlRange = entry.getValue().getControlRange();
            if (controlRange.contain(index)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 判断id是否是位于对应的服务名字下的
     * @param targetServerName 服务名字
     * @param id 对应的数据id
     * @return true：位于服务管理内部，false：不属于该服务管理
     */
    public boolean judgeBelongTo(String targetServerName, Long id) {
        String serverName = getServerName(id);
        if (null != serverName) {
            return serverName.equals(targetServerName);
        }
        return false;
    }

    /**
     * 获取服务对应的管理范围
     * @param serverName 可用的服务名字
     * @return 一个范围，[from, to]，左右都相等
     */
    public ControlRange getRange(String serverName){
        ServerNode serverNode = serverNodeMap.get(serverName);
        if (null != serverNode) {
            return serverNode.getControlRange();
        }
        return null;
    }

    /**
     * 判断当前节点是否是最小的那个节点
     */
    public boolean isMinNode(String serverName){
        return serverNodeMap.firstKey().equals(serverName);
    }

    public TreeSet<String> getServerNameSet(){
        return new TreeSet<>(serverNodeMap.keySet());
    }

    /**
     * 获取服务的节点信息
     * @param serverName 可用的服务名字
     */
    public ServerNode getServerNode(String serverName){
        if(null != serverName && !"".equals(serverName)) {
            return serverNodeMap.get(serverName);
        }
        return null;
    }

    public void clear(){
        serverNodeMap.clear();
    }

    /**
     * 重新初始化数据
     * @param serverRange 服务和对应的范围
     */
    public void initServerRange(Map<String, ServerNode> serverRange){
        // 清理当前的所有服务范围
        clear();
        if(null != serverRange && !serverRange.isEmpty()){
            serverNodeMap.putAll(serverRange);
        }
    }

    /**
     * 合并控制范围，优先向左合并，如果左边没有，则向又合并，如果右边也没有，则当前没有激活的节点了
     * @param toDelNode 待删除的节点
     * @return ServerNode: 合并之后的新的控制范围
     */
    private ServerNode mergeControlRange(ServerNode toDelNode) {
        if (null != toDelNode) {
            NeighborServer neighborServer = toDelNode.getNeighborServer();
            ControlRange toDelRange = toDelNode.getControlRange();

            String leftServer = neighborServer.leftServer;
            if (null != leftServer) {
                // 合并优先考虑左边的服务节点
                ServerNode leftServerNode = serverNodeMap.get(leftServer);
                if (null != leftServerNode) {
                    ControlRange controlRange = leftServerNode.getControlRange();
                    controlRange.setTo(toDelRange.getTo());
                    return leftServerNode;
                }
            } else {
                // 如果左边的邻居不可用，则合并右边
                String rightServer = neighborServer.rightServer;
                if (null != rightServer) {
                    // 合并优先考虑左边的服务节点
                    ServerNode rightServerNode = serverNodeMap.get(rightServer);
                    if (null != rightServerNode) {
                        ControlRange controlRange = rightServerNode.getControlRange();
                        controlRange.setFrom(toDelRange.getFrom());
                        return rightServerNode;
                    }
                }
            }
        }
        return null;
    }

    private void mergeNeighborServer(ServerNode toDelNode){
        if (null != toDelNode) {
            NeighborServer neighborServer = toDelNode.getNeighborServer();

            String leftServer = neighborServer.leftServer;
            String rightServer = neighborServer.rightServer;
            // 左边服务不空，则修改左边服务的 rightServer
            if (null != leftServer){
                ServerNode leftNode = serverNodeMap.get(leftServer);
                if(null != leftNode) {
                    leftNode.getNeighborServer().setRightServer(rightServer);
                }
            }

            // 右边服务不空，则修改左边服务的 leftServer
            if (null != rightServer){
                ServerNode rightNode = serverNodeMap.get(rightServer);
                if (null != rightNode) {
                    rightNode.getNeighborServer().setLeftServer(leftServer);
                }
            }
        }
    }

    /**
     * 在有新服务进来，则看下哪个服务需要进行拆分
     */
    private ServerNode getSplitNode() {
        if (!serverNodeMap.isEmpty()) {
            return serverNodeMap.values().stream().sorted().findFirst().get();
        }

        return new ServerNode();
    }

    /**
     * 数据取余转换
     */
    private Integer getIndex(Long id){
        return Math.toIntExact((id & (1 << SIZE_POWER) - 1));
    }

    /**
     * 拆分控制范围
     * @param toSplitNode 待拆分的对象
     * @return 返回新分配的的范围
     */
    private ControlRange splitControlRange(ServerNode toSplitNode) {
        if (null != toSplitNode) {
            ControlRange toSplitRange = toSplitNode.getControlRange();
            Integer to = toSplitRange.getTo();

            Integer middle = (toSplitRange.size() / 2) + toSplitRange.getFrom();
            toSplitRange.setTo(middle);
            return ControlRange.of(middle + 1, to);
        }
        return ControlRange.of(0, 0);
    }

    /**
     * 拆分左右相邻的服务节点
     * @return 返回新的相邻左右节点名字
     */
    private NeighborServer splitNeighborServer(ServerNode toSplitNode, String serverName){
        if (null != toSplitNode) {
            NeighborServer toSplitNeighbor = toSplitNode.getNeighborServer();

            // 生成新的neighbor
            NeighborServer newNeighborServer = NeighborServer.builder()
                .leftServer(toSplitNode.getServerName())
                .rightServer(toSplitNeighbor.getRightServer()).build();

            // 刷新原来节点右边节点的左边的指向
            String rightServerName = toSplitNeighbor.getRightServer();
            if(null != rightServerName){
                ServerNode rightNode = serverNodeMap.get(rightServerName);
                rightNode.getNeighborServer().setLeftServer(serverName);
            }

            toSplitNeighbor.setRightServer(serverName);
            return newNeighborServer;
        }
        return NeighborServer.builder().build();
    }

    private void updateServerNode(ServerNode serverNode){
        String serverName = serverNode.getServerName();
        if (null != serverName){
            serverNodeMap.put(serverName, serverNode);
        }
    }

    /**
     * 数据的范围[from, to]，前后都相等
     */
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor(staticName = "of")
    public static class ControlRange implements Comparable<ControlRange>{
        @NonNull
        Integer from;
        @NonNull
        Integer to;

        boolean contain(Integer data){
            return null != data && from <= data && data <= to;
        }

        Integer size(){
            return to - from;
        }

        @Override
        public String toString(){
            return "[from = " + from + ", to = " + to + "]";
        }

        @Override
        public int compareTo(ControlRange controlRange) {
            if (this.equals(controlRange)){
                return 0;
            }

            // size是首先判断的点，如果size相等，则再考虑位置from
            if (this.size() > controlRange.size()) {
                return 1;
            } else if (this.size() < controlRange.size()) {
                return -1;
            } else {
                if (this.from < controlRange.from) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    /**
     * 相邻服务，用于服务禁用时候的数据迁移
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class NeighborServer{

        /**
         * 左边服务的名字
         */
        private String leftServer;
        /**
         * 右边服务的名字
         */
        private String rightServer;
    }

    /**
     * 服务节点
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(of = "serverName")
    public static class ServerNode implements Comparable<ServerNode>{

        private String serverName;
        private NeighborServer neighborServer;
        private ControlRange controlRange;

        @Override
        public int compareTo(ServerNode o) {
            // 倒序
            return -controlRange.compareTo(o.getControlRange());
        }
    }
}
