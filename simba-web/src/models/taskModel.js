import {
  pageList,
  add,
  deleteData,
  update,
  pageCount,
  getCodeList,
  getAllCodeList,
  disable,
  enable,
  run,
  handRun,
} from '@/services/taskApi';

export default {
  namespace: 'taskModel', // 这个是标示当前model的

  // 下面是定义的数据模型
  state: {
    maxTabIndex: 1, // 最大的标签页索引，用于标签新增计数用
    activePaneName: '1', // tabPane 的激活的key
    tabIndexList: ['1'], // 当前存在的标签的列表
    panes: [
      {
        name: '1',
        title: '任务调度1',
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
    drawerRecord: {}, // 抽屉弹窗的数据
    resultOfRun: null, // 调度测试的结果
    drawerVisible: false, // 抽屉标志位
    script: null, // 编辑中的脚本
  },

  // 异步处理函数
  effects: {
    // 用于其他操作之后刷新界面
    *tableFresh({ payload }, { put }) {
      console.log('taskModel.tableFresh 参数：');
      console.log(JSON.stringify(payload));
      yield put({
        type: 'pageCount',
        payload: {
          paneIndex: payload.paneIndex,
        },
      });

      yield put({
        type: 'pageList',
        payload: {
          paneIndex: payload.paneIndex,
          pager: {
            pageNo: 1,
            pageSize: 20,
          },
        },
      });
    },

    // 增加组配置
    *add({ payload }, { call, put }) {
      console.log('task.add 参数：');
      console.log(JSON.stringify(payload));
      const response = yield call(add, payload);
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
      // console.log('task.delete 参数：');
      // console.log(JSON.stringify(payload));
      const response = yield call(deleteData, payload);
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
      // console.log('task.update 参数：');
      // console.log(JSON.stringify(payload));
      const response = yield call(update, payload);
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

    // 获取配置列表
    *pageList({ payload }, { call, put }) {
      console.log('taskModel.pageList 参数：');
      console.log(JSON.stringify(payload));

      const values = {
        ...payload.searchParam,
        pager: payload.pager,
      };

      console.log(JSON.stringify(values));
      const response = yield call(pageList, values);
      console.log('taskModel.pageList 结果：');
      yield put({
        type: 'handlePageListResult',
        payload: {
          response,
          ...payload,
        },
      });
    },

    *pageCount({ payload }, { call, put }) {
      // console.log('taskModel.pageCount 参数：');
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
      // console.log('taskModel.getCodeList');

      const codeList = yield call(getAllCodeList, payload);
      // console.log('taskModel.结果');
      // console.log(JSON.stringify(codeList));
      yield put({
        type: 'handleAllCodeList',
        payload: codeList,
      });
    },

    // 获取configItem 已经配置的CodeList
    *fetchCodeList({ payload }, { call, put }) {
      // console.log('taskModel.getCodeList');

      const codeList = yield call(getCodeList, payload);
      // console.log('taskModel.结果');
      // console.log(JSON.stringify(codeList));
      yield put({
        type: 'handleCodeList',
        payload: codeList,
      });
    },

    // 任务关闭
    *disable({ payload }, { call, put }) {
      console.log('taskModel.disable 参数：');
      // console.log(JSON.stringify(payload));

      const response = yield call(disable, payload);
      yield put({
        type: 'handleDisable',
        payload: {
          response,
          record: payload,
        },
      });
    },

    // 开启任务
    *enable({ payload }, { call, put }) {
      console.log('taskModel.enable 参数：');
      // console.log(JSON.stringify(payload));

      const response = yield call(enable, payload);
      yield put({
        type: 'handleEnable',
        payload: {
          response,
          record: payload,
        },
      });
    },

    // 手动运行一次脚本
    *handRun({ payload }, { call }) {
      console.log('taskModel.handRun 参数：');
      console.log(JSON.stringify(payload));

      const response = yield call(handRun, payload);
      if (response !== undefined) {
        console.log('执行完毕');
      }
    },

    // 测试运行界面输入的脚本
    *run({ payload }, { call, put }) {
      console.log('taskModel.run 参数：');
      console.log(JSON.stringify(payload));

      const response = yield call(run, payload);

      console.log('返回的结果');
      console.log(JSON.stringify(response));

      yield put({
        type: 'handleRun',
        payload: {
          response,
          record: payload,
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
      console.log('taskModel.handleCountResult 返回的结果');
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
      console.log('taskModel.handlePageListResult 返回的结果');
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

    handleAddResult(state, action) {
      // console.log('taskModel.handleAddResult 返回的结果');
      console.log(JSON.stringify(action));

      return {
        ...state,
      };
    },

    handleUpdateResult(state, action) {
      // console.log('taskModel.handleUpdateResult 返回的结果');
      // console.log(JSON.stringify(action.payload));

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

      console.log(JSON.stringify(panes));

      return {
        ...state,
        panes,
      };
    },

    handleDeleteResult(state, action) {
      // console.log('taskModel.handleDeleteResult 返回的结果');
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

    // 增加标签
    addPane(state, action) {
      // console.log('taskModel.addPane 参数：');
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
      console.log('taskModel.deletePaneActive 参数：');
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
      console.log('taskModel.deletePane 参数：');
      console.log(JSON.stringify(action.payload.activePaneName));
      return {
        ...state,
        panes: action.payload.panes,
        tabIndexList: action.payload.tabIndexList,
      };
    },

    // 激活标签
    activePane(state, action) {
      // console.log('taskModel.activePane 参数：');
      console.log(JSON.stringify(action));
      return {
        ...state,
        activePaneName: action.payload,
      };
    },

    // 关闭任务
    handleDisable(state, action) {
      console.log('taskModel.handleDisable 参数：');
      // console.log(JSON.stringify(action));

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
      // console.log(JSON.stringify(action));

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

    // 打开抽屉放置数据
    showDrawer(state, action) {
      // console.log('taskModel.showDrawer 参数：');
      // console.log(JSON.stringify(action));

      return {
        ...state,
        drawerRecord: action.payload,
        script: action.payload.data,
      };
    },

    // 设置要执行的脚本
    setScriptToRun(state, action) {
      console.log('taskModel.setScriptToRun 参数：');
      console.log(JSON.stringify(action));

      const { drawerRecord, panes } = state;
      // console.log(JSON.stringify(panes));
      drawerRecord.data = action.payload.data;
      // console.log(JSON.stringify(panes));
      return {
        ...state,
        panes,
        drawerRecord,
      };
    },

    // 启动脚本测试
    handleRun(state, action) {
      console.log('taskModel.handleRun 参数：');
      console.log(JSON.stringify(action));

      const { drawerRecord } = state;
      drawerRecord.data = action.payload.record.data;
      return {
        ...state,
        drawerRecord,
        resultOfRun: action.payload.response,
      };
    },

    // 打开抽屉
    openDrawer(state, action) {
      console.log('taskModel.openDrawer 参数：');
      console.log(JSON.stringify(action));

      return {
        ...state,
        drawerVisible: true,
      };
    },

    // 关闭抽屉
    closeDrawer(state, action) {
      console.log('taskModel.closeDrawer 参数：');
      console.log(JSON.stringify(action));

      return {
        ...state,
        drawerVisible: false,
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

    // 脚本添加
    addScript(state, action) {
      console.log('taskModel.addScript 参数：');
      console.log(JSON.stringify(action));

      return {
        ...state,
        script: action.payload,
      };
    },
  },
};
