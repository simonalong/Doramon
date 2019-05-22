import {
  getPageList,
  addConfigItem,
  deleteConfigItem,
  updateConfigItem,
  pageCount,
  loadData,
  unloadData,
  getCodeList,
  getAllCodeList,
} from '@/services/configItemApi';

export default {
  namespace: 'configItem', // 这个是标示当前model的

  // 下面是定义的数据模型
  state: {
    maxTabIndex: 1, // 最大的标签页索引，用于标签新增计数用
    activePaneName: '1', // tabPane 的激活的name
    tabIndexList: ['1'], // 当前存在的标签的列表
    panes: [
      {
        name: '1',
        title: '配置1',
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
    groupAllCodeList: [], // 这个所有组的code列表
    groupCodeList: [], // 这个是配置对应的所有的code列表
  },

  // 异步处理函数
  effects: {
    // 用于其他操作之后刷新界面
    *tableFresh({ payload }, { put }) {
      console.log('configItem.tableFresh 参数：');
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
      console.log('configItem.getPageList 参数：');
      console.log(JSON.stringify(payload));

      const values = {
        ...payload.searchParam,
        pager: payload.pager,
      };

      console.log(JSON.stringify(values));
      const response = yield call(getPageList, values);
      console.log('configItem.getPageList 结果：');
      yield put({
        type: 'handlePageListResult',
        payload: {
          response,
          ...payload,
        },
      });
    },

    *getListCount({ payload }, { call, put }) {
      // console.log('configItem.getListCount 参数：');
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

    // 获取所有的GroupCode
    *fetchAllCodeList({ payload }, { call, put }) {
      // console.log('configItem.getCodeList');

      const codeList = yield call(getAllCodeList, payload);
      // console.log('configItem.结果');
      // console.log(JSON.stringify(codeList));
      yield put({
        type: 'handleAllCodeList',
        payload: codeList,
      });
    },

    // 获取configItem 已经配置的CodeList
    *fetchCodeList({ payload }, { call, put }) {
      // console.log('configItem.getCodeList');

      const codeList = yield call(getCodeList, payload);
      // console.log('configItem.结果');
      // console.log(JSON.stringify(codeList));
      yield put({
        type: 'handleCodeList',
        payload: codeList,
      });
    },

    // 增加组配置
    *add({ payload }, { call, put }) {
      // console.log('configItem.add 参数：');
      // console.log(JSON.stringify(payload));
      const response = yield call(addConfigItem, payload);
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
      // console.log('configItem.delete 参数：');
      // console.log(JSON.stringify(payload));
      const response = yield call(deleteConfigItem, payload);
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
      // console.log('configItem.update 参数：');
      // console.log(JSON.stringify(payload));
      const response = yield call(updateConfigItem, payload);
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

    // 上架
    *load({ payload }, { call, put }) {
      console.log('configItem.load 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(loadData, payload);
      yield put({
        type: 'handleLoad',
        payload: {
          response,
          id: payload.id,
        },
      });

      // 添加调用界面刷新
      yield put({
        type: 'tableFresh',
        payload: {
          paneIndex: payload.paneIndex,
        },
      });
    },

    // 下架
    *unload({ payload }, { call, put }) {
      console.log('configItem.unload 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(unloadData, payload);
      yield put({
        type: 'handleUnload',
        payload: {
          response,
          id: payload.id,
        },
      });

      // 添加调用界面刷新
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
      // console.log('configItem.handleCountResult 返回的结果');
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
      newPanes[index].content.pager.pageNo = pl.pager.pageNo;
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
      console.log('configItem.handleUpdateResult 返回的结果');
      console.log(JSON.stringify(action.payload));

      // 若成功，则不不需要重新加载后端，而是直接修改前段的内存数据
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
      // console.log('configItem.handleDeleteResult 返回的结果');
      // console.log(action.payload);
      const { panes } = state;
      // 删除页签中的所有有关数据
      if (action.payload.response === '1') {
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

    // 关闭任务
    handleDisable(state, action) {
      console.log('taskModel.handleDisable 参数：');
      console.log(JSON.stringify(action));

      // 若成功，则不不需要重新加载后端，而是直接修改前端的内存数据
      const { panes } = state;
      if (action.payload.response === 1) {
        // 更新所有的页签中的数据
        const itemRecord = action.payload.record;
        for (let index = 0; index < panes.length; index += 1) {
          const tableListNew = panes[index].content.tableList;
          const dataIndex = tableListNew.findIndex(item => itemRecord.id === item.id);

          if (dataIndex > -1) {
            tableListNew.splice(dataIndex, 1, {
              ...tableListNew[dataIndex],
              ...itemRecord,
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

    // 开启任务
    handleEnable(state, action) {
      console.log('taskModel.handleEnable 参数：');
      console.log(JSON.stringify(action));

      // 若成功，则不不需要重新加载后端，而是直接修改前端的内存数据
      const { panes } = state;
      if (action.payload.response === 1) {
        // 更新所有的页签中的数据
        const itemRecord = action.payload.record;
        for (let index = 0; index < panes.length; index += 1) {
          const tableListNew = panes[index].content.tableList;
          const dataIndex = tableListNew.findIndex(item => itemRecord.id === item.id);

          if (dataIndex > -1) {
            tableListNew.splice(dataIndex, 1, {
              ...tableListNew[dataIndex],
              ...itemRecord,
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

    handleUnload(state, action) {
      // console.log('taskModel.handleUnload 返回的结果');
      // console.log(action.payload);

      return {
        ...state,
      };
    },

    handleLoad(state, action) {
      // console.log('taskModel.handleLoad 返回的结果');
      // console.log(action.payload);

      return {
        ...state,
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

    handleAllCodeList(state, action) {
      const data = action.payload.map(user => ({
        text: user,
        value: user,
      }));

      return {
        ...state,
        groupAllCodeList: data,
        tableLoading: false,
      };
    },

    handleCodeList(state, action) {
      // action.payload.unshift('_全部_');
      const data = action.payload.map(user => ({
        text: user,
        value: user,
      }));

      return {
        ...state,
        groupCodeList: data,
        tableLoading: false,
      };
    },
  },
};
