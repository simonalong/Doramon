import {
  getPageList,
  addConfigGroup,
  deleteConfigGroup,
  updateConfigGroup,
  pageCount,
} from '@/services/configGroupApi';

export default {
  namespace: 'configGroup', // 这个是标示当前model的

  // 下面是定义的数据模型
  state: {
    maxTabIndex: 1, // 最大的标签页索引，用于标签新增计数用
    activePaneName: '1', // tabPane 的激活的name
    tabIndexList: ['1'], // 当前存在的标签的列表
    panes: [
      {
        name: '1',
        title: '配置组1',
        content: {
          tableList: [],
          tableLoading: false,
          searchParam: {},
          totalNumber: 0,
          pager: {
            pageNo: 1,
            pageSize: 20,
          },
        },
      },
    ],
  },

  // 异步处理函数
  effects: {
    // 用于其他操作之后刷新界面
    *tableFresh({ payload }, { put }) {
      console.log('configGroup.tableFresh 参数：');
      console.log(JSON.stringify(payload));
      yield put({
        type: 'getListCount',
        payload: {
          paneIndex: payload.paneIndex,
        },
      });

      yield put({
        type: 'getPageList',
        payload: {
          paneIndex: payload.paneIndex,
          pager: {
            pageNo: 1,
            pageSize: 20,
          },
        },
      });
    },

    // 获取配置列表
    *getPageList({ payload }, { call, put }) {
      console.log('configGroup.getPageList 参数：');
      console.log(JSON.stringify(payload));

      const values = {
        ...payload.searchParam,
        pager: payload.pager,
      };

      console.log(JSON.stringify(values));

      const response = yield call(getPageList, values);
      yield put({
        type: 'handlePageListResult',
        payload: {
          response,
          ...payload,
        },
      });
    },

    *getListCount({ payload }, { call, put }) {
      // console.log('configGroup.getListCount 参数：');
      // console.log(JSON.stringify(payload));

      const params =
        payload === undefined || payload.searchParam === undefined ? {} : payload.searchParam;
      const pager = payload === undefined || payload.pager === undefined ? {} : payload.pager;
      const values = {
        ...params,
        ...pager,
      };

      // console.log(JSON.stringify(values));
      const count = yield call(pageCount, values);
      yield put({
        type: 'handleCountResult',
        payload: {
          paneIndex: payload.paneIndex,
          count,
        },
      });
    },

    // 增加组配置
    *add({ payload }, { call, put }) {
      console.log('configGroup.add 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(addConfigGroup, payload);
      yield put({
        type: 'handleAddResult',
        payload: response,
      });

      // 调用界面刷新
      yield put({
        type: 'tableFresh',
        payload: {
          paneIndex: payload.paneIndex,
        },
      });
    },

    // 删除组配置
    *delete({ payload }, { call, put }) {
      console.log('configGroup.delete 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(deleteConfigGroup, payload);
      yield put({
        type: 'handleDeleteResult',
        payload: {
          response,
          id: payload,
        },
      });
    },

    // 修改组配置
    *update({ payload }, { call, put }) {
      console.log('configGroup.update 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(updateConfigGroup, payload);
      yield put({
        type: 'handleUpdateResult',
        payload: {
          response,
          param: payload,
        },
      });

      // 调用界面刷新
      yield put({
        type: 'tableFresh',
        payload: {
          paneIndex: payload.paneIndex,
        },
      });
    },
  },

  reducers: {
    setSearchParam(state, action) {
      return {
        ...state,
        searchParam: action,
      };
    },

    setTableLoading(state) {
      const newPanes = state.panes;
      const index = newPanes.findIndex(pane => pane.name === state.activePaneName);
      newPanes[index].content.tableLoading = true;

      return {
        ...state,
        panes: newPanes,
      };
    },

    handleCountResult(state, action) {
      // console.log('configGroup.handleCountResult 返回的结果');
      // console.log(JSON.stringify(action.payload));
      const pl = action.payload;

      const newPanes = state.panes;
      const index = pl.paneIndex;
      newPanes[index].content.totalNumber = pl.count;

      return {
        ...state,
        panes: newPanes,
      };
    },

    handlePageListResult(state, action) {
      // console.log('configGroup.handlePageListResult 返回的结果');
      // console.log(JSON.stringify(action));
      const pl = action.payload;

      const newPanes = state.panes;
      const index = pl.paneIndex;
      newPanes[index].content.searchParam = pl.searchParam;
      newPanes[index].content.pager.pageNo = pl.pageNo;
      newPanes[index].content.tableList = pl.response;
      newPanes[index].content.tableLoading = false;

      return {
        ...state,
        panes: newPanes,
      };
    },

    handleAddResult(state) {
      return {
        ...state,
      };
    },

    handleUpdateResult(state, action) {
      // console.log('configGroup.handleUpdateResult 返回的结果');
      // console.log(JSON.stringify(action.payload));

      const { panes } = state;

      if (action.payload.response === 1) {
        // 更新所有的页签中的数据
        const newItem = action.payload.param;
        for (let index = 0; index < panes.length; index += 1) {
          const tableListNew = panes[index].content.tableList;
          const dataIndex = tableListNew.findIndex(item => newItem.id === item.id);

          if (dataIndex > -1) {
            tableListNew.splice(dataIndex, 1, {
              ...tableListNew[dataIndex],
              ...newItem,
            });
          }
          panes[index].content.tableLoading = false;
        }
      }

      return {
        ...state,
        panes,
      };
    },

    handleDeleteResult(state, action) {
      // console.log('configGroup.handleDeleteResult 返回的结果');
      // console.log(action.payload);
      const { panes } = state;

      if (action.payload.response === '1') {
        // 删除页签中的所有有关数据
        for (let index = 0; index < panes.length; index += 1) {
          panes[index].content.tableList = panes[index].content.tableList.filter(
            item => item.id !== action.payload.id
          );
          panes[index].content.tableLoading = false;
        }
      }

      return {
        ...state,
        panes,
      };
    },

    // 增加标签
    addPane(state, action) {
      // console.log('configGroup.addPane 参数：');
      // console.log(JSON.stringify(action));
      return {
        ...state,
        maxTabIndex: action.payload.maxTabIndex,
        tabIndexList: action.payload.tabIndexList,
        panes: action.payload.panes,
        activePaneName: action.payload.activePaneName,
      };
    },

    // 删除标签，自己如果是激活的
    deletePaneActive(state, action) {
      console.log('configGroup.deletePaneActive 参数：');
      console.log(JSON.stringify(action.payload.activePaneName));
      return {
        ...state,
        panes: action.payload.panes,
        tabIndexList: action.payload.tabIndexList,
        activePaneName: action.payload.activePaneName,
      };
    },

    // 删除标签，自己非激活的
    deletePane(state, action) {
      console.log('configGroup.deletePane 参数：');
      console.log(JSON.stringify(action.payload.activePaneName));
      return {
        ...state,
        panes: action.payload.panes,
        tabIndexList: action.payload.tabIndexList,
      };
    },

    // 激活标签
    activePane(state, action) {
      // console.log('configGroup.activePane 参数：');
      // console.log(JSON.stringify(action));
      return {
        ...state,
        activePaneName: action.payload,
      };
    },
  },
};
