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
  Avatar,
  Select,
  DatePicker,
  Pagination,
  InputNumber,
  Tabs,
  Modal,
} from 'antd';

import moment from 'moment';
import styles from './${tablePathName}List.less';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';

const { RangePicker } = DatePicker;
const EditableContext = React.createContext();
const FormItem = Form.Item;
const EditableFormRow = Form.create()(({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
));

// 弹窗增加配置项
const CreateForm = Form.create()(prop => {
  const { modalVisible, form, handleAdd, hideAddModal } = prop;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      form.resetFields();
      handleAdd(fieldsValue);
    });
  };

  return (
    <Modal
      destroyOnClose
      title="新增"
      visible={modalVisible}
      onOk={okHandle}
      maskClosable={false}
      onCancel={() => hideAddModal()}
    >
      <#list dataAddFields! as addField>
      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 17 }} label="${addField.fieldInfo.desc}" hasFeedback>
        {form.getFieldDecorator('${addField.fieldInfo.name}', {
          rules: [{ required: <#if addField.require == 1>true<#else >false</#if>, message: '请输入${addField.fieldInfo.desc}！' }],
        })(<#if addField.fieldInfo.timeFlag == 1>
          <DatePicker
            style={{ width: '100%' }}
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="Select Time"
          /><#elseif addField.fieldInfo.enumFlag==1 ><#list enumFields! as enumField><#if enumField.fieldName == addField.fieldInfo.name>
          <Select style={{ width: '100%' }}>
          <#list enumField.metaList! as meta>
            <Select.Option value="${meta.name}">${meta.desc}</Select.Option>
          </#list>
          </Select></#if></#list><#else >
          <Input placeholder="请输入${addField.fieldInfo.desc}" /></#if>)}
      </FormItem>
      </#list>
    </Modal>
  );
});

const EditForm = Form.create()(props => {
  const { modalVisible, form, handleEdit, hideEditModal, item } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) {
        return;
      }
      form.resetFields();
      handleEdit(fieldsValue);
    });
  };

  return (
    <Modal
      destroyOnClose
      title="修改"
      visible={modalVisible}
      onOk={okHandle}
      maskClosable={false}
      onCancel={() => hideEditModal()}
    >
    <#list updateFields! as updateField>
      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 17 }} label="${updateField.fieldInfo.desc}">
        {form.getFieldDecorator('${updateField.fieldInfo.name}', {
          initialValue: <#if updateField.fieldInfo.timeFlag == 1>moment(item.${updateField.fieldInfo.name})<#else >item.${updateField.fieldInfo.name}</#if>,
          rules: [{ required: <#if updateField.require == 1>true<#else >false</#if>, message: '请输入${updateField.fieldInfo.desc}！' }],
        })(<#if updateField.fieldInfo.timeFlag == 1>
          <DatePicker
            style={{ width: '100%' }}
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="Select Time"
            <#if updateField.canEdit == 0>disabled</#if>
          /><#elseif updateField.fieldInfo.enumFlag==1 ><#list enumFields! as enumField><#if enumField.fieldName == updateField.fieldInfo.name>
          <Select style={{ width: '100%' }}<#if updateField.canEdit == 0>disabled</#if>>
          <#list enumField.metaList! as meta>
            <Select.Option value="${meta.name}">${meta.desc}</Select.Option>
          </#list>
          </Select></#if></#list><#else >
          <Input placeholder="请输入${updateField.fieldInfo.desc}" <#if updateField.canEdit == 0>disabled</#if> /></#if>
        )}
      </FormItem>
    </#list>
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
                              message: `请输入 ${r"${title}"}!`,
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
@connect(({ ${tablePathNameLower}Model, loading }) => ({
  ${tablePathNameLower}Model,
  loading: loading.models.${tablePathNameLower}Model,
}))
// @Form.create() 是一个注解，就简化了xxx = Form.create(xxx);export xxx
@Form.create()
class ${tablePathName}List extends PureComponent {
  state = {
    addModalVisible: false,
    editModalVisible: false,
    item: {},
  };

  columns = [
    <#list tableShowFields! as f>
    {
      name: '${f.fieldInfo.name}',
      title: '${f.fieldInfo.desc}',
      dataIndex: '${f.fieldInfo.name}',
      width: '${f.rate}%',
      <#if f.fieldInfo.timeFlag == 1>
      render: (text, record) => (
        <span>{moment(parseInt(record.${f.fieldInfo.name})).format('YYYY-MM-DD HH:mm:ss')}</span>
      ),
      <#elseif f.fieldInfo.picFlag == 1>
      render: (text, record) => <Avatar shape="square" src={record.${f.fieldInfo.name}} />,
      </#if>
    },
    </#list>
    {
      name: 'edit',
      title: '编辑',
      dataIndex: 'edit',
      width: '10%',
      render: (text, record) => (
        <span>
          <Button type="primary" icon="edit" onClick={() => this.showEditModal(record)} />
        </span>
      ),
    },
    {
      name: 'delete',
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

  // 界面初始化函数
  componentDidMount() {
    const {
      ${tablePathNameLower}Model: { activePaneName },
    } = this.props;
    console.log('启动');

    // 获取页面的总个数
    this.getPageDate(activePaneName, 1);
  }

  getPageDate(name, pageNo, searchParam) {
    const { dispatch } = this.props;
    const {
        ${tablePathNameLower}Model: { panes },
    } = this.props;

    this.setTableLoading();

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
        type: '${tablePathNameLower}Model/pageCount',
        payload: {
          paneIndex: index,
          searchParam: param,
        },
      });

      dispatch({
        type: '${tablePathNameLower}Model/pageList',
        payload: {
          paneIndex: index,
          pager,
          searchParam: param,
        },
      });
    }
  }

  expandedRowRender = record => (
    <div>
     <#list expandFields! as expandField>
      <Row>
       <#list expandField! as expand>
        <Col span={6}>
          <Badge status="success" text="${expand.desc}：" />
          <#if expand.timeFlag == 1><span>{moment(parseInt(record.create_time)).format('YYYY-MM-DD HH:mm:ss')}</span><#elseif expand.picFlag == 1><Avatar shape="square" src={record.${expand.name}} />><#else ><span>{record.${expand.name}}</span></#if>
        </Col>
       </#list>
      </Row>
      <br />
     </#list>
    </div>
  );

  showDeleteConfirm = row => {
    const { dispatch } = this.props;
    console.log('点击');
    console.log(JSON.stringify(row));
    const paneIndex = this.getActivePaneIndex();
    const showLoading = ()=>this.setTableLoading();
    Modal.confirm({
      title: '确定要删除这条配置',
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        showLoading();
        console.log('OK');
        dispatch({
          type: '${tablePathNameLower}Model/delete',
          payload: {
            id:row.id,
            paneIndex,
          },
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

  showEditModal = record => {
    console.log('点击编辑');
    this.setState({
      item: record,
      editModalVisible: true,
    });
  };

  hideEditModal = () => {
    this.setState({
      editModalVisible: false,
    });
  };

  // 设置表格加载
  setTableLoading = () => {
    const { dispatch } = this.props;
    dispatch({
      type: '${tablePathNameLower}Model/setTableLoading',
    });
  };

  // 获取激活的pane
  getActivePaneIndex = () => {
    const {
      ${tablePathNameLower}Model: { activePaneName, panes },
    } = this.props;

    return panes.findIndex(pane => pane.name === activePaneName);
  };

  // 添加
  handleAdd = fields => {
    const { dispatch } = this.props;

    this.setTableLoading();

    // 将中间添加的脚本放进去
    const params = {
      ...fields,
      paneIndex: this.getActivePaneIndex(),
    };

    dispatch({
      type: '${tablePathNameLower}Model/add',
      payload: params,
    });

    this.hideAddModal();
  };

  // 判断对象1是否包含对象2的所有属性
  contain = (object1, object2) => {
    let index = 0;
    const keys = Object.keys(object2);
    for (let i = 0; i < keys.length; i += 1) {
      const name = keys[i];
      if (object1[name] && object2[name] === object1[name]) {
        index += 1;
      }
    }
    return index === Object.keys(object2).length;
  };

  handleEdit = fields => {
    const { dispatch } = this.props;
    const { item } = this.state;


    console.log('编辑修改');
    console.log(JSON.stringify(fields));
    console.log(JSON.stringify(item));

    // 判断是否有修改，如果没有修改，则不向后端发起更新
    if (!this.contain(item, fields)) {
      this.setTableLoading();
      console.log('有变化需要修改');
      const params = {
        ...Object.assign(item, fields),
        paneIndex: this.getActivePaneIndex(),
      };

      console.log(JSON.stringify(params));
      dispatch({
        type: '${tablePathNameLower}Model/update',
        payload: params,
      });
    }

    this.hideEditModal();
  };

  handleSearch = e => {
    e.preventDefault();

    const { form } = this.props;
    const {
       ${tablePathNameLower}Model: { activePaneName },
    } = this.props;

    console.log('启动查询');
    this.setTableLoading();

    form.validateFields((err, fieldsValue) => {
      if (err) return;

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
          <#list searchFields! as searchField>
          <Col lg={5}>
            <FormItem label="${searchField.desc}">
              {getFieldDecorator('${searchField.name}')(
                <#if searchField.timeFlag == 1>
                <RangePicker
                  showTime={{
                     hideDisabledOptions: true,
                     defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('11:59:59', 'HH:mm:ss')],
                  }}
                  format="YYYY-MM-DD HH:mm:ss"
                />
                <#elseif searchField.enumFlag==1 >
                <#list enumFields! as enumField>
                <#if enumField.fieldName == searchField.name>
                <Select allowClear>
                  <#list enumField.metaList! as meta>
                  <Select.Option value="${meta.name}">${meta.desc}</Select.Option>
                  </#list>
                </Select>
                </#if>
                </#list>
                <#else >
                <Input placeholder="请输入" />
                </#if >
               )}
            </FormItem>
          </Col>
          </#list>
          <Col md={2} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                查询
              </Button>
            </span>
          </Col>
          <Col md={2} sm={24}>
            <Button icon="plus" type="primary" onClick={this.showAddModal}>
              新建
            </Button>
          </Col>
        </Row>
      </Form>
    );
  };

  onChange = page => {
    const {
       ${tablePathNameLower}Model: { activePaneName },
    } = this.props;

    console.log('页面索引修改');

    this.getPageDate(activePaneName, page);
  };

  onEdit = (targetKey, action) => {
    const { dispatch } = this.props;
    const {
       ${tablePathNameLower}Model: { panes, maxTabIndex, activePaneName, tabIndexList },
    } = this.props;

    if (action === 'remove') {
      // 删除的不是当前激活的，则直接删除
      const activePaneNameStr = `${r"${activePaneName}"}`;
      if (activePaneNameStr !== targetKey) {
        dispatch({
          type: '${tablePathNameLower}Model/deletePane',
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
            console.log(`新的激活的${r"${newActivePaneName}"}`);
          }
        });

        if (newActivePaneName !== '0') {
          dispatch({
            type: '${tablePathNameLower}Model/deletePaneActive',
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
      const name = `${r"${tableIndex}"}`;
      tabIndexList.push(name);
      panes.push({
        name,
        title: `${tableNameCn}${r"${tableIndex}"}`,
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
        type: '${tablePathNameLower}Model/addPane',
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
      type: '${tablePathNameLower}Model/activePane',
      payload: activePaneName,
    });
  };

  render() {
    const {
       ${tablePathNameLower}Model: { selectState, groupAllCodeList },
    } = this.props;

    // 替换表Table的组件
    const components = {
      body: {
        row: EditableFormRow,
        cell: EditableCell,
      },
    };

    const { addModalVisible, editModalVisible, item } = this.state;
    const parentAddMethods = {
      selectState,
      groupAllCodeList,
      handleAdd: this.handleAdd,
      hideAddModal: this.hideAddModal,
    };
    const parentEditMethods = {
      item,
      handleEdit: this.handleEdit,
      hideEditModal: this.hideEditModal,
    };

    const {
       ${tablePathNameLower}Model: { panes, activePaneName },
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
              columns={this.columns}
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
        <CreateForm {...parentAddMethods} modalVisible={addModalVisible} />
        <EditForm {...parentEditMethods} modalVisible={editModalVisible} />
      </PageHeaderWrapper>
    );
  }
}

export default ${tablePathName}List;
