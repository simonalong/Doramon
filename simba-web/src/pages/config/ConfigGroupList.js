import React, { PureComponent } from 'react';
import { connect } from 'dva';
import {
  Row,
  Col,
  Card,
  Badge,
  Form,
  Input,
  Button,
  Table,
  Pagination,
  InputNumber,
  Popconfirm,
  Tabs,
  Modal,
} from 'antd';

import moment from 'moment';
import styles from './ConfigGroupList.less';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';

const EditableContext = React.createContext();
const FormItem = Form.Item;
const EditableFormRow = Form.create()(({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
));

// 弹窗增加配置组
const CreateForm = Form.create()(props => {
  const { modalVisible, form, handleAdd, hideAddModal } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) {
        return;
      }
      form.resetFields();
      handleAdd(fieldsValue);
    });
  };
  return (
    <Modal
      destroyOnClose
      title="新建规则"
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => hideAddModal()}
    >
      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 17 }} label="配置组码">
        {form.getFieldDecorator('group_code', {
          rules: [{ required: true, message: '请输入配置组的配置组code！' }],
        })(<Input placeholder="请输入配置组码" />)}
      </FormItem>
      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 17 }} label="配置组名字">
        {form.getFieldDecorator('group_name', {
          rules: [{ required: true, message: '请输入配置组的配置组组名字！' }],
        })(<Input placeholder="请输入配置组名" />)}
      </FormItem>
    </Modal>
  );
});

// 可编辑的列中的元素
class EditableCell extends PureComponent {
  getInput = () => {
    const { inputType } = this.props;
    if (inputType === 'number') {
      return <InputNumber />;
    }
    return <Input />;
  };

  render() {
    const { editing, dataIndex, title, inputType, record, index, ...restProps } = this.props;
    return (
      <EditableContext.Consumer>
        {form => {
          const { getFieldDecorator } = form;
          return (
            <td {...restProps}>
              {editing ? (
                <FormItem style={{ margin: 0 }}>
                  {getFieldDecorator(dataIndex, {
                    rules: [
                      {
                        required: true,
                        message: `请输入 ${title}!`,
                      },
                    ],
                    initialValue: record[dataIndex],
                  })(this.getInput())}
                </FormItem>
              ) : (
                restProps.children
              )}
            </td>
          );
        }}
      </EditableContext.Consumer>
    );
  }
}

/* eslint react/no-multi-comp:0 */
@connect(({ configGroup, loading }) => ({
  configGroup,
  loading: loading.models.configGroup,
}))
// @Form.create() 是一个注解，就简化了xxx = Form.create(xxx);export xxx
@Form.create()
class ConfigGroupList extends PureComponent {
  state = {
    addModalVisible: false,
    editingId: '',
  };

  columns = [
    {
      key: 'id',
      title: 'id',
      dataIndex: 'id',
      width: '10%',
    },
    {
      key: 'group_code',
      title: '组code',
      dataIndex: 'group_code',
      width: '20%',
    },
    {
      key: 'group_name',
      title: '组名称',
      dataIndex: 'group_name',
      editable: true,
      width: '34%',
    },
    {
      key: 'create_time',
      title: '更新',
      dataIndex: 'create_time',
      editable: false,
      width: '30%',
      render: (text, record) => (
        <span>{moment(record.update_time).format('YYYY-MM-DD HH:mm:ss')}</span>
      ),
    },
    {
      key: 'edit',
      title: '编辑',
      dataIndex: 'edit',
      width: '20%',
      render: (text, record) => {
        const editable = this.isEditing(record);
        return (
          <div>
            {editable ? (
              <span>
                <EditableContext.Consumer>
                  {form => (
                    <Popconfirm title="确定保存？" onConfirm={() => this.save(form, record)}>
                      <a style={{ marginRight: 8 }}>保存</a>
                    </Popconfirm>
                  )}
                </EditableContext.Consumer>
                <a onClick={() => this.cancel(record.id)}>取消</a>
              </span>
            ) : (
              <Button type="primary" icon="edit" onClick={() => this.edit(record.id)}>
                {' '}
                编辑
              </Button>
            )}
          </div>
        );
      },
    },
    {
      key: 'delete',
      title: '删除',
      dataIndex: 'delete',
      editable: false,
      width: '5%',
      render: (text, row) => (
        <span>
          <Button type="danger" icon="delete" onClick={() => this.showDeleteConfirm(row)} />
        </span>
      ),
    },
  ];

  componentDidMount() {
    const {
      configGroup: { activePaneName },
    } = this.props;
    console.log('启动');

    // 获取页面的总个数
    this.getPageDate(activePaneName, 1);
  }

  getPageDate(name, pageNo, searchParam) {
    const { dispatch } = this.props;
    const {
      configGroup: { panes },
    } = this.props;

    this.setTableLoading();

    console.log(JSON.stringify(panes));

    const index = panes.findIndex(pane => pane.name === name);
    if (index > -1) {
      console.log(index);
      console.log(JSON.stringify(searchParam));

      let param = panes[index].content.searchParam;

      console.log(JSON.stringify(param));

      if (searchParam !== undefined) {
        console.log('ddd');
        param = searchParam;
      }

      let pager = { ...panes[index].content.pager };
      if (pageNo !== undefined) {
        console.log('ccc');
        pager = {
          ...pager,
          pageNo,
        };
      }

      // 获取页面的总个数
      dispatch({
        type: 'configGroup/getListCount',
        payload: {
          paneIndex: index,
          searchParam: param,
        },
      });

      dispatch({
        type: 'configGroup/getPageList',
        payload: {
          paneIndex: index,
          pager,
          searchParam: param,
        },
      });
    }
  }

  showDeleteConfirm = row => {
    const { dispatch } = this.props;
    console.log('点击');
    console.log(JSON.stringify(row));
    Modal.confirm({
      title: '确定要删除这条配置',
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        console.log('OK');
        dispatch({
          type: 'configGroup/delete',
          payload: row.id,
        });
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  };

  showAddModal = () => {
    this.setState({
      addModalVisible: true,
    });
  };

  hideAddModal = () => {
    this.setState({
      addModalVisible: false,
    });
  };

  // 设置表格加载
  setTableLoading = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'configGroup/setTableLoading',
    });
  };

  getActivePaneIndex = () => {
    const {
      configGroup: { activePaneName, panes },
    } = this.props;

    return panes.findIndex(pane => pane.name === activePaneName);
  };

  // 添加配置组
  handleAdd = fields => {
    const { dispatch } = this.props;
    this.setTableLoading();

    // 将中间添加的脚本放进去
    const params = {
      ...fields,
      paneIndex: this.getActivePaneIndex(),
    };

    dispatch({
      type: 'configGroup/add',
      payload: params,
    });

    this.hideAddModal();
  };

  handleFormReset = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'rule/fetch',
      payload: {},
    });
  };

  handleSearch = e => {
    e.preventDefault();

    const { form } = this.props;
    const {
      configGroup: { activePaneName },
    } = this.props;

    console.log('启动查询');
    this.setTableLoading();

    form.validateFields((err, fieldsValue) => {
      if (err) {
        return;
      }

      this.getPageDate(activePaneName, 1, fieldsValue);
    });
  };

  // 加载搜索输入框和搜索按钮
  renderSearchForm = () => {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={12} sm={24}>
            <FormItem label="配置组code">
              {getFieldDecorator('group_code')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={9} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                查询
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                重置
              </Button>
            </span>
          </Col>
          <Col md={1} sm={24}>
            <Button icon="plus" type="primary" onClick={this.showAddModal}>
              新建
            </Button>
          </Col>
        </Row>
      </Form>
    );
  };

  expandedRowRender = record => (
    <div>
      <Row>
        <Col span={6}>
          <Badge status="success" text="组名称：" />
          <span>{record.group_name}</span>
        </Col>
        <Col span={12}>
          <Badge status="success" text="创建时间：" />
          <span>{moment(record.create_time).format('YYYY-MM-DD HH:mm:ss')}</span>
        </Col>
      </Row>
    </div>
  );

  edit = id => {
    // console.log('key是什么');
    // console.log(JSON.stringify(id));
    this.setState({ editingId: id });
  };

  isEditing = record => {
    const { editingId } = this.state;
    return record.id === editingId;
  };

  save = (form, record) => {
    form.validateFields((error, row) => {
      if (error) {
        return;
      }
      this.setTableLoading();

      const { dispatch } = this.props;

      const params = {
        ...Object.assign(record, row),
        paneIndex: this.getActivePaneIndex(),
      };

      console.log(JSON.stringify(params));

      dispatch({
        type: 'configGroup/update',
        payload: params,
      }).then(() => {
        this.setState({ editingId: '' });
      });
    });
  };

  cancel = () => {
    this.setState({ editingId: '' });
  };

  onChange = page => {
    const {
      configGroup: { activePaneName },
    } = this.props;

    console.log('页面索引修改');

    this.getPageDate(activePaneName, page);
  };

  onEdit = (targetKey, action) => {
    const { dispatch } = this.props;
    const {
      taskModel: { panes, maxTabIndex, activePaneName, tabIndexList },
    } = this.props;

    if (action === 'remove') {
      // 删除的不是当前激活的，则直接删除
      const activePaneNameStr = `${activePaneName}`;
      if (activePaneNameStr !== targetKey) {
        dispatch({
          type: 'taskModel/deletePane',
          payload: {
            panes: panes.filter(pane => pane.name !== targetKey),
            tabIndexList: tabIndexList.filter(tableIndex => tableIndex !== targetKey),
          },
        });
      } else {
        // 删除的是激活的则激活左侧标签，如果左侧没有，则激活右侧，如果右侧也没有，则删除不生效
        let newActivePaneName = '0';
        tabIndexList.forEach((tableIndex, i) => {
          if (tableIndex === targetKey) {
            if (i - 1 >= 0) {
              newActivePaneName = tabIndexList[i - 1];
            } else if (i + 1 < tabIndexList.length) {
              newActivePaneName = tabIndexList[i + 1];
            } else {
              console.log('删除不生效');
            }
            console.log(`新的激活的${newActivePaneName}`);
          }
        });

        if (newActivePaneName !== '0') {
          dispatch({
            type: 'taskModel/deletePaneActive',
            payload: {
              panes: panes.filter(pane => pane.name !== targetKey),
              tabIndexList: tabIndexList.filter(tableIndex => tableIndex !== targetKey),
              activePaneName: newActivePaneName,
            },
          });
        }
      }
    } else {
      const tableIndex = maxTabIndex + 1;
      const name = `${tableIndex}`;
      tabIndexList.push(name);
      panes.push({
        name,
        title: `任务调度${tableIndex}`,
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
      });

      dispatch({
        type: 'taskModel/addPane',
        payload: {
          maxTabIndex: tableIndex,
          tabIndexList,
          panes,
          activePaneName: name,
        },
      });

      this.getPageDate(name, 1);
    }
  };

  onTabChange = activePaneName => {
    const { dispatch } = this.props;

    dispatch({
      type: 'configGroup/activePane',
      payload: activePaneName,
    });
  };

  render() {
    // 替换表Table的组件
    const components = {
      body: {
        row: EditableFormRow,
        cell: EditableCell,
      },
    };
    const columns = this.columns.map(col => {
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        onCell: record => ({
          record,
          inputType: col.dataIndex === 'age' ? 'number' : 'text',
          dataIndex: col.dataIndex,
          title: col.title,
          editing: this.isEditing(record),
        }),
      };
    });
    const { addModalVisible } = this.state;
    const parentMethods = {
      handleAdd: this.handleAdd,
      hideAddModal: this.hideAddModal,
    };

    const {
      configGroup: { panes, activePaneName },
    } = this.props;

    const tabPanes = panes.map(pane => (
      <Tabs.TabPane tab={pane.title} key={pane.name}>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderSearchForm()}</div>
            <div className={styles.tableListOperator} />

            <Table
              rowKey={record => record.id}
              components={components}
              dataSource={pane.content.tableList}
              columns={columns}
              loading={pane.content.tableLoading}
              pagination={false}
              expandedRowRender={this.expandedRowRender}
            />
            <br />
            <Pagination
              showQuickJumper
              onChange={this.onChange}
              defaultCurrent={1}
              total={pane.content.totalNumber}
              current={pane.content.pager.pageNo}
              defaultPageSize={pane.content.pager.pageSize}
            />
          </div>
        </Card>
      </Tabs.TabPane>
    ));

    return (
      <PageHeaderWrapper>
        <Tabs
          onChange={this.onTabChange}
          activeKey={activePaneName}
          defaultActiveKey="1"
          type="editable-card"
          onEdit={this.onEdit}
        >
          {tabPanes}
        </Tabs>
        <CreateForm {...parentMethods} modalVisible={addModalVisible} />
      </PageHeaderWrapper>
    );
  }
}

export default ConfigGroupList;
