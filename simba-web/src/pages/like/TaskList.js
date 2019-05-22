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
  Icon,
  Select,
  Divider,
  Drawer,
  Pagination,
  InputNumber,
  Tabs,
  Modal,
} from 'antd';

// 引入codemirror封装
import { UnControlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';

import 'codemirror/theme/solarized.css';

// 不同的代码模式
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/groovy/groovy';

import moment from 'moment';
import styles from './TaskList.less';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';

const { TextArea } = Input;
const EditableContext = React.createContext();
const FormItem = Form.Item;
const EditableFormRow = Form.create()(({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
));

// 弹窗增加配置项
const CreateForm = Form.create()(prop => {
  const { modalVisible, form, handleAdd, hideAddModal, groupAllCodeList, addScript } = prop;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      form.resetFields();
      handleAdd(fieldsValue);
    });
  };

  const options = groupAllCodeList.map(d => <Select.Option key={d.value}>{d.text}</Select.Option>);

  return (
    <Modal
      destroyOnClose
      title="新增"
      width={1000}
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => hideAddModal()}
    >
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="配置组code">
        {form.getFieldDecorator('task_group', {
          rules: [{ required: true, message: '请选择配置组code！' }],
        })(
          <Select
            showSearch
            style={{ width: '100%' }}
            placeholder="请选择配置组code"
            optionFilterProp="children"
            filterOption={(input, option) =>
              option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }
          >
            {options}
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务name" hasFeedback>
        {form.getFieldDecorator('task_name', {
          rules: [{ required: true, message: '请输入任务name！' }],
        })(<Input placeholder="请输入任务name" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务描述" hasFeedback>
        {form.getFieldDecorator('task_desc', {
          rules: [{ required: true, message: '请输入任务描述！' }],
        })(<Input placeholder="请输入任务描述" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="cron表达式" hasFeedback>
        {form.getFieldDecorator('cron', {
          rules: [{ required: true, message: '请输入cron表达式！' }],
        })(<Input placeholder="请输入cron表达式" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="状态" hasFeedback>
        {form.getFieldDecorator('status', {
          rules: [{ required: true, message: '请输入状态！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="Y">启用</Select.Option>
            <Select.Option value="N">禁用</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务类型" hasFeedback>
        {form.getFieldDecorator('task_type', {
          rules: [{ required: true, message: '请输入任务类型！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="GROOVY">groovy脚本</Select.Option>
            <Select.Option value="URL">url链接post方式</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="数据" hasFeedback>
        {form.getFieldDecorator('data', {
          rules: [{ required: false, message: '请输入数据！' }],
        })(
          <CodeMirror
            options={{
              mode: 'groovy',
              theme: 'solarized light',
              lineNumbers: true,
              lineWrapping: true,
            }}
            // 这个用于填写时候的回调
            onBeforeChange={() => {
              // console.log('onBeforeChange fresh');
              // console.log(JSON.stringify(data));
              // console.log(JSON.stringify(value));
            }}
            // 在失去焦点的时候触发，这个时候放数据最好
            onBlur={editor => {
              // console.log('onBlur fresh');
              // console.log(JSON.stringify(data));
              // console.log(JSON.stringify(value));
              // console.log(editor.getValue());
              addScript(editor.getValue());
            }}
          />
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="参数" hasFeedback>
        {form.getFieldDecorator('param', {
          rules: [{ required: false, message: '请输入参数！' }],
        })(<Input placeholder="请输入参数" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="执行状态" hasFeedback>
        {form.getFieldDecorator('run_status', {
          rules: [{ required: false, message: '请输入执行状态！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="RUNNING">执行中</Select.Option>
            <Select.Option value="DONE">完成</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="创建人" hasFeedback>
        {form.getFieldDecorator('create_user_name', {
          rules: [{ required: false, message: '请输入创建人！' }],
        })(<Input placeholder="请输入创建人" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="修改人" hasFeedback>
        {form.getFieldDecorator('update_user_name', {
          rules: [{ required: false, message: '请输入修改人！' }],
        })(<Input placeholder="请输入修改人" />)}
      </FormItem>
    </Modal>
  );
});

// 抽屉侧边弹窗
const DrawerForm = Form.create()(prop => {
  const { modalVisible, form, drawerRecord, resultOfRun, onClose, run, addScript } = prop;

  const title = `${drawerRecord.task_group}：${drawerRecord.task_name}`;
  const result = JSON.stringify(resultOfRun);

  const readRun = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      console.log('准备提交');
      console.log(JSON.stringify(fieldsValue));
      form.resetFields();
      run({ task_type: drawerRecord.task_type, data: fieldsValue.data });
    });
  };

  return (
    <Drawer
      title={title}
      placement="right"
      onClose={onClose}
      visible={modalVisible}
      width={1000}
      style={{
        overflow: 'auto',
        paddingBottom: '108px',
      }}
    >
      <Form onSubmit={readRun} layout="vertical">
        <Row>
          <Col span={24}>
            <h3>
              <span>{drawerRecord.task_desc}</span>
            </h3>
          </Col>
          <Col span={24}>
            <h3>
              <span>类型：{drawerRecord.task_type}</span>
            </h3>
          </Col>
          <br />
          <Col lg={24}>
            <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 24 }} label="数据：">
              {form.getFieldDecorator('data', {
                initialValue: drawerRecord.data,
                rules: [{ required: true, message: '请输入数据！' }],
              })(
                <CodeMirror
                  options={{
                    mode: 'groovy',
                    theme: 'solarized light',
                    lineNumbers: true,
                    lineWrapping: true,
                  }}
                  onBeforeChange={() => {
                    console.log('onBeforeChange fresh');
                    // console.log(JSON.stringify(data));
                    // console.log(JSON.stringify(value));
                  }}
                  // 在失去焦点的时候触发，这个时候放数据最好
                  onBlur={editor => {
                    // console.log('onBlur fresh');
                    // console.log(JSON.stringify(data));
                    // console.log(JSON.stringify(value));
                    // console.log(editor.getValue());
                    addScript(editor.getValue());
                  }}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={4}>
            <Button type="primary" htmlType="submit">
              运行
            </Button>
          </Col>
        </Row>
        <Row>
          <Divider dashed>运行返回值</Divider>
          <Col span={24}>
            <span>{result}</span>
          </Col>
        </Row>
      </Form>
    </Drawer>
  );
});

const EditForm = Form.create()(props => {
  const { modalVisible, form, handleEdit, hideEditModal, item, addScript } = props;
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
      width={1000}
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => hideEditModal()}
    >
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="index">
        {form.getFieldDecorator('id', {
          initialValue: item.id,
          rules: [{ required: false, message: '请输入index！' }],
        })(<Input placeholder="请输入index" disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="配置code">
        {form.getFieldDecorator('task_group', {
          initialValue: item.task_group,
          rules: [{ required: true, message: '请输入配置code！' }],
        })(<Input placeholder="请输入 code" disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务name">
        {form.getFieldDecorator('task_name', {
          initialValue: item.task_name,
          rules: [{ required: true, message: '请输入任务name！' }],
        })(<Input placeholder="请输入任务name" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务描述">
        {form.getFieldDecorator('task_desc', {
          initialValue: item.task_desc,
          rules: [{ required: true, message: '请输入任务描述！' }],
        })(<Input placeholder="请输入任务描述" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="cron表达式">
        {form.getFieldDecorator('cron', {
          initialValue: item.cron,
          rules: [{ required: true, message: '请输入cron表达式！' }],
        })(<Input placeholder="请输入cron表达式" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="状态">
        {form.getFieldDecorator('status', {
          initialValue: item.status,
          rules: [{ required: true, message: '请输入状态！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="Y">启用</Select.Option>
            <Select.Option value="N">禁用</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="任务类型">
        {form.getFieldDecorator('task_type', {
          initialValue: item.task_type,
          rules: [{ required: false, message: '请输入任务类型！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="GROOVY">groovy脚本</Select.Option>
            <Select.Option value="URL">url链接post方式</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="数据">
        {form.getFieldDecorator('data', {
          initialValue: item.data,
          rules: [{ required: true, message: '请输入数据！' }],
        })(
          <CodeMirror
            options={{
              mode: 'groovy',
              theme: 'solarized light',
              lineNumbers: true,
              lineWrapping: true,
            }}
            // 这个用于填写时候的回调，必须存在，数据更新不能放这里，因为value值是旧值
            onBeforeChange={() => {}}
            // 在失去焦点的时候触发，这里用于数据更新
            onBlur={editor => {
              // console.log('onBlur fresh');
              // console.log(JSON.stringify(data));
              // console.log(JSON.stringify(value));
              // console.log(editor.getValue());
              addScript(editor.getValue());
            }}
          />
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="参数">
        {form.getFieldDecorator('param', {
          initialValue: item.param,
          rules: [{ required: false, message: '请输入参数！' }],
        })(<Input placeholder="请输入参数" />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="执行状态">
        {form.getFieldDecorator('run_status', {
          initialValue: item.run_status,
          rules: [{ required: false, message: '请输入执行状态！' }],
        })(
          <Select style={{ width: '100%' }}>
            <Select.Option value="RUNNING">执行中</Select.Option>
            <Select.Option value="DONE">完成</Select.Option>
          </Select>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="创建人">
        {form.getFieldDecorator('create_user_name', {
          initialValue: item.create_user_name,
          rules: [{ required: false, message: '请输入创建人！' }],
        })(<Input placeholder="请输入创建人" disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 3 }} wrapperCol={{ span: 20 }} label="修改人">
        {form.getFieldDecorator('update_user_name', {
          initialValue: item.update_user_name,
          rules: [{ required: false, message: '请输入修改人！' }],
        })(<Input placeholder="请输入修改人" disabled />)}
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

const IconFont = Icon.createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/font_1019169_50hw0pfn04.js',
});

const IconStatus = Icon.createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/font_1023507_jssj9r31a37.js',
});

/* eslint react/no-multi-comp:0 */
@connect(({ taskModel, loading }) => ({
  taskModel,
  loading: loading.models.taskModel,
}))
// @Form.create() 是一个注解，就简化了xxx = Form.create(xxx);export xxx
@Form.create()
class TaskList extends PureComponent {
  state = {
    addModalVisible: false,
    editModalVisible: false,
    item: {},
  };

  columns = [
    {
      name: 'id',
      title: 'index',
      dataIndex: 'id',
      width: '8%',
    },
    {
      name: 'task_group',
      title: '配置组',
      dataIndex: 'task_group',
      width: '10%',
    },
    {
      name: 'task_name',
      title: '任务name',
      dataIndex: 'task_name',
      width: '20%',
    },
    {
      name: 'task_desc',
      title: '任务描述',
      dataIndex: 'task_desc',
      width: '30%',
    },
    {
      name: 'task_type',
      title: '任务类型',
      dataIndex: 'task_type',
      width: '10%',
    },
    {
      name: 'cron',
      title: 'cron表达式',
      dataIndex: 'cron',
      width: '12%',
    },
    {
      name: 'status',
      title: '状态',
      dataIndex: 'status',
      width: '8%',
      render: (text, record) => {
        if (text === 'Y') {
          return <IconStatus type="icon-zhengchang" onClick={() => this.disable(record)} />;
        }
        return <IconStatus type="icon-jinyong" onClick={() => this.enable(record)} />;
      },
    },
    {
      name: 'run_status',
      title: '手动',
      dataIndex: 'run_status',
      width: '20%',
      render: (text, record) => {
        if (text === 'RUNNING') {
          return <Icon type="sync" spin />;
        }
        return (
          <Button ghost onClick={() => this.handRun(record)}>
            <IconFont type="icon-run" />
          </Button>
        );
      },
    },
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

  componentDidMount() {
    const { dispatch } = this.props;
    const {
      taskModel: { activePaneName },
    } = this.props;
    console.log('启动');

    // 获取所有组code列表
    dispatch({
      type: 'taskModel/fetchAllCodeList',
    });

    // 获取组code列表
    dispatch({
      type: 'taskModel/fetchCodeList',
    });

    // 获取页面的总个数
    this.getPageDate(activePaneName, 1);
  }

  getPageDate(name, pageNo, searchParam) {
    const { dispatch } = this.props;
    const {
      taskModel: { panes },
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
        type: 'taskModel/pageCount',
        payload: {
          paneIndex: index,
          searchParam: param,
        },
      });

      dispatch({
        type: 'taskModel/pageList',
        payload: {
          paneIndex: index,
          pager,
          searchParam: param,
        },
      });
    }
  }

  disable = record => {
    const { dispatch } = this.props;
    console.log('禁用任务');
    this.setTableLoading();

    const item = {
      ...record,
    };

    item.status = 'N';

    console.log(JSON.stringify(item));
    dispatch({
      type: 'taskModel/disable',
      payload: item,
    });
  };

  enable = record => {
    const { dispatch } = this.props;
    console.log('启用任务');
    console.log(JSON.stringify(record));

    this.setTableLoading();

    const item = {
      ...record,
    };

    item.status = 'Y';

    console.log(JSON.stringify(item));
    dispatch({
      type: 'taskModel/enable',
      payload: item,
    });
  };

  /**
   * 手动运行
   * @param record
   */
  handRun = record => {
    const { dispatch } = this.props;
    console.log('手动运行');
    console.log(JSON.stringify(record));

    dispatch({
      type: 'taskModel/handRun',
      payload: record,
    });
  };

  dataParser = record => {
    if (record.task_type === 'GROOVY') {
      return (
        <div>
          <Row>
            <Col span={4}>
              <Badge status="success" text="数据groovy：" />
            </Col>
            <Col span={20}>
              <Button type="primary" onClick={() => this.showDrawerModal(record)}>
                启动脚本测试
              </Button>
            </Col>
            <br />
            <br />
            <Col span={24}>
              <CodeMirror
                value={record.data}
                options={{
                  mode: 'groovy',
                  theme: 'solarized light',
                  lineNumbers: true,
                }}
              />
            </Col>
          </Row>
        </div>
      );
    }
    return (
      <div>
        <Row>
          <Col span={24}>
            <Badge status="success" text="数据url：" />
            <span>{record.data}</span>
          </Col>
        </Row>
        <br />
        <Row>
          <Col span={24}>
            <Badge status="success" text="参数：" />
          </Col>
          <Col span={24}>
            <TextArea rows={6} value={record.param} />
          </Col>
        </Row>
      </div>
    );
  };

  expandedRowRender = record => (
    <div>
      <Row>
        <Col span={4}>
          <Badge status="success" text="任务类型：" />
          <span>{record.task_type}</span>
        </Col>
        <Col span={4}>
          <Badge status="success" text="创建人：" />
          <span>{record.create_user_name}</span>
        </Col>
        <Col span={4}>
          <Badge status="success" text="修改人：" />
          <span>{record.update_user_name}</span>
        </Col>
        <Col span={4}>
          <Badge status="success" text="创建时间：" />
          <span>{moment(record.create_time).format('YYYY-MM-DD HH:mm:ss')}</span>
        </Col>
        <Col span={4}>
          <Badge status="success" text="更新时间：" />
          <span>{moment(record.create_time).format('YYYY-MM-DD HH:mm:ss')}</span>
        </Col>
      </Row>
      <br />
      {this.dataParser(record)}
      <br />
    </div>
  );

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
          type: 'taskModel/delete',
          payload: row.id,
        });
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  };

  /**
   * 运行：
   * 1.如果是groovy脚本，则直接运行脚本
   * 2.如果是运行url调度，则运行其他的
   * @param script
   */
  run = param => {
    const { dispatch } = this.props;
    const {
      taskModel: { script },
    } = this.props;

    console.log('启动运行');
    // console.log(JSON.stringify(param));

    const params = {
      ...param,
      data: script,
    };

    dispatch({
      type: 'taskModel/setScriptToRun',
      payload: params,
    });

    dispatch({
      type: 'taskModel/run',
      payload: params,
    });
  };

  showDrawerModal = record => {
    const { dispatch } = this.props;

    console.log('展示侧边栏');
    // console.log(JSON.stringify(record));

    dispatch({
      type: 'taskModel/openDrawer',
    });

    dispatch({
      type: 'taskModel/showDrawer',
      payload: record,
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
    this.setState({
      item: record,
      editModalVisible: true,
    });

    this.addScript(record.data);
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
      type: 'taskModel/setTableLoading',
    });
  };

  getActivePaneIndex = () => {
    const {
      taskModel: { activePaneName, panes },
    } = this.props;

    return panes.findIndex(pane => pane.name === activePaneName);
  };

  // 添加脚本
  addScript = value => {
    const { dispatch } = this.props;

    dispatch({
      type: 'taskModel/addScript',
      payload: value,
    });
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

  // 添加
  handleAdd = fields => {
    const { dispatch } = this.props;
    const {
      taskModel: { script },
    } = this.props;

    this.setTableLoading();

    // 将中间添加的脚本放进去
    const params = {
      ...fields,
      paneIndex: this.getActivePaneIndex(),
      data: script,
    };

    dispatch({
      type: 'taskModel/add',
      payload: params,
    });

    // this.getPageDate(activePaneName, 1);

    this.hideAddModal();
  };

  handleEdit = fields => {
    const { dispatch } = this.props;
    const { item } = this.state;
    const {
      taskModel: { script },
    } = this.props;

    this.setTableLoading();

    console.log('编辑修改');
    console.log(JSON.stringify(fields));
    console.log(JSON.stringify(item));

    // 判断是否有修改，如果没有修改，则不向后端发起更新
    if (!this.contain(item, fields)) {
      console.log('有变化需要修改');
      const params = {
        ...Object.assign(item, fields),
        paneIndex: this.getActivePaneIndex(),
        data: script,
      };

      console.log(JSON.stringify(params));
      dispatch({
        type: 'taskModel/update',
        payload: params,
      });
    }

    this.hideEditModal();
  };

  handleSearch = e => {
    e.preventDefault();

    const { form } = this.props;
    const {
      taskModel: { activePaneName },
    } = this.props;

    console.log('启动查询');
    this.setTableLoading();

    console.log(JSON.stringify(form));
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      console.log('点击后的选项');
      console.log(JSON.stringify(fieldsValue));

      // 如果是全部则删除对应的选项，下面注释用于修复一些问题，不要删除
      /* eslint-disable no-param-reassign */
      // if (fieldsValue !== undefined) {
      //   Object.keys(fieldsValue).forEach(key => {
      //     console.log(key, fieldsValue[key]);
      //     if (key === 'task_group') {
      //       delete fieldsValue.task_group;
      //     }
      //
      //     if (fieldsValue[key] === '') {
      //       delete fieldsValue[key];
      //     }
      //   });
      // }
      /* eslint-disable no-param-reassign */

      console.log(JSON.stringify(fieldsValue));

      this.getPageDate(activePaneName, 1, fieldsValue);
    });
  };

  renderSearchForm = () => {
    const {
      form: { getFieldDecorator },
      taskModel: { groupCodeList },
    } = this.props;

    // console.log("加载groupCodeList")
    // console.log(JSON.stringify(groupCodeList));

    const options = groupCodeList.map(d => <Select.Option key={d.value}>{d.text}</Select.Option>);

    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={6} sm={24}>
            <FormItem label="配置组">
              {getFieldDecorator('task_group')(
                <Select
                  showSearch
                  placeholder="请选择配置组code"
                  style={{ width: '100%' }}
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {options}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col md={6} sm={24}>
            <FormItem label="任务名">
              {getFieldDecorator('task_name')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={6} sm={24}>
            <FormItem label="任务描述">
              {getFieldDecorator('task_desc')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={2} sm={24} lg={2}>
            <FormItem label="状态">
              {getFieldDecorator('status')(
                <Select>
                  <Select.Option value="Y">启用</Select.Option>
                  <Select.Option value="N">禁用</Select.Option>
                </Select>
              )}
            </FormItem>
          </Col>
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
      taskModel: { activePaneName },
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

  onClose = () => {
    const { dispatch } = this.props;

    dispatch({
      type: 'taskModel/closeDrawer',
    });
  };

  onTabChange = activePaneName => {
    const { dispatch } = this.props;

    dispatch({
      type: 'taskModel/activePane',
      payload: activePaneName,
    });
  };

  render() {
    const {
      taskModel: { selectState, groupAllCodeList, drawerRecord, resultOfRun, drawerVisible },
    } = this.props;
    // 替换表Table的组件
    const components = {
      body: {
        row: EditableFormRow,
        cell: EditableCell,
      },
    };
    const { addModalVisible, editModalVisible, item } = this.state;

    // 抽屉侧边栏的一些函数
    const drawerMethods = {
      drawerRecord,
      resultOfRun,
      addScript: this.addScript,
      onClose: this.onClose,
      run: this.run,
    };
    const parentAddMethods = {
      selectState,
      groupAllCodeList,
      addScript: this.addScript,
      handleAdd: this.handleAdd,
      hideAddModal: this.hideAddModal,
    };
    const parentEditMethods = {
      item,
      addScript: this.addScript,
      handleEdit: this.handleEdit,
      hideEditModal: this.hideEditModal,
    };

    const {
      taskModel: { panes, activePaneName },
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
        <DrawerForm {...drawerMethods} modalVisible={drawerVisible} />
        <CreateForm {...parentAddMethods} modalVisible={addModalVisible} />
        <EditForm {...parentEditMethods} modalVisible={editModalVisible} />
      </PageHeaderWrapper>
    );
  }
}

export default TaskList;
