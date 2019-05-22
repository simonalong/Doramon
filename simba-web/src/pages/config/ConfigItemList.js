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
  Select,
  Icon,
  DatePicker,
  Pagination,
  Divider,
  Tabs,
  Modal,
} from 'antd';

// 引入codemirror封装
import { UnControlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
import 'codemirror/lib/codemirror';

// 不同的主题风格
import 'codemirror/theme/solarized.css';

// 不同的代码模式
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/groovy/groovy';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/mode/properties/properties';
import 'codemirror/mode/yaml/yaml';

// 代码格式核查
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/lint.css';
import 'codemirror/addon/lint/json-lint';
import 'codemirror/addon/lint/javascript-lint';

import 'codemirror/addon/edit/closebrackets';

import moment from 'moment';
import styles from './ConfigItemList.less';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';

const FormItem = Form.Item;
const { TextArea } = Input;

// 弹窗增加配置项
const CreateForm = Form.create()(prop => {
  const {
    modalVisible,
    form,
    handleAdd,
    hideAddModal,
    groupAllCodeList,
    timingType,
    setTimingType,
  } = prop;

  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      form.resetFields();

      console.log('数据添加,inner');
      console.log(JSON.stringify(fieldsValue));
      if (fieldsValue.tag === undefined) {
        console.log('数据添加,1');
        handleAdd({
          ...fieldsValue,
          tag: 'default',
        });
      } else {
        console.log('数据添加,2');
        handleAdd(fieldsValue);
      }
    });
  };
  const options = groupAllCodeList.map(d => <Select.Option key={d.value}>{d.text}</Select.Option>);
  const dateFormat = 'YYYY-MM-DD HH:mm:ss';

  // 放大查看配置输入
  const zoomIn = () => {
    Modal.success({
      title: <span>应用[{form.getFieldValue('group_code')}]的启动配置</span>,
      content: (
        <CodeMirror
          value={form.getFieldValue('conf_value')}
          options={{
            mode: 'application/json',
            theme: 'solarized light',
            lineNumbers: true,
          }}
          // 设置尺寸
          editorDidMount={editor => {
            editor.setSize('auto', 'auto');
          }}
        />
      ),
      okText: '确认',
      width: '1200px',
    });
  };

  const timingParser = () => {
    if (timingType === 'ASSIGN_UNAVAILABLE') {
      return (
        <Row>
          <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} label="过期时间">
            {form.getFieldDecorator('expire_time', {})(
              <DatePicker
                showTime
                format={dateFormat}
                style={{ width: '100%' }}
                placeholder="请输入过期时间"
              />
            )}
          </FormItem>
        </Row>
      );
    }

    if (timingType === 'SCHEDULE_UNAVAILABLE') {
      return (
        <Row>
          <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} label="任务分组">
            {form.getFieldDecorator('task_group', {})(
              <Input placeholder="请输入调度任务的分组group" />
            )}
          </FormItem>
          <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 18 }} label="任务name">
            {form.getFieldDecorator('task_name', {})(<Input placeholder="请输入调度任务的name" />)}
          </FormItem>
        </Row>
      );
    }

    return <Row />;
  };

  // 启动配置的值的类型只有yml和properties
  const valTypeParser = () => {
    if (form.getFieldValue('tag') === 'start') {
      return (
        <Row>
          <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="值类型">
            {form.getFieldDecorator('val_type', {
              rules: [{ required: true, message: '请选择value的类型！' }],
            })(
              <Select showSearch style={{ width: '100%' }}>
                <Select.Option value="YML">yml类型</Select.Option>
                <Select.Option value="PROPERTY">property类型</Select.Option>
              </Select>
            )}
          </FormItem>
        </Row>
      );
    }
    return (
      <Row>
        <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="值类型">
          {form.getFieldDecorator('val_type', {
            rules: [{ required: true, message: '请选择value的类型！' }],
          })(
            <Select showSearch style={{ width: '100%' }}>
              <Select.Option value="STRING">String类型</Select.Option>
              <Select.Option value="JSON">json类型</Select.Option>
              <Select.Option value="YML">yml类型</Select.Option>
              <Select.Option value="PROPERTY">property类型</Select.Option>
            </Select>
          )}
        </FormItem>
      </Row>
    );
  };

  return (
    <Modal
      destroyOnClose
      title="新增"
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => hideAddModal()}
      width="800px"
    >
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置组组">
        {form.getFieldDecorator('group_code', {
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
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置key">
        {form.getFieldDecorator('conf_key', {
          rules: [{ required: true, message: '请输入配置key！' }],
        })(<Input placeholder="请输入 conf_key" />)}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置标签">
        {form.getFieldDecorator('tag')(<Input placeholder="请输入 tag（start表示启动标签配置）" />)}
      </FormItem>
      {valTypeParser()}
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置值">
        {form.getFieldDecorator('conf_value', {
          rules: [{ required: true, message: '请输入配置value！' }],
        })(
          <Row>
            <Col span={22}>
              <TextArea rows={4} />
            </Col>
            <Col span={1} />
            <Col span={1}>
              <Button type="primary" shape="circle" icon="zoom-in" onClick={() => zoomIn()} />
            </Col>
          </Row>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="备注">
        {form.getFieldDecorator('remark', {
          rules: [{ required: true, message: '配置项描述！' }],
        })(<Input placeholder="请输入 remark" />)}
      </FormItem>
      {timingParser()}
    </Modal>
  );
});

const EditForm = Form.create()(props => {
  const { modalVisible, form, handleEdit, hideEditModal, item, setTimingType } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) {
        return;
      }
      form.resetFields();
      handleEdit(fieldsValue);
    });
  };

  // 放大查看配置输入
  const zoomIn = () => {
    Modal.success({
      title: <span>应用[{item.group_code}]的启动配置</span>,
      content: (
        <CodeMirror
          value={form.getFieldValue('conf_value')}
          options={{
            mode: 'application/json',
            theme: 'solarized light',
            lineNumbers: true,
          }}
          // 设置尺寸
          editorDidMount={editor => {
            editor.setSize('auto', 'auto');
          }}
        />
      ),
      okText: '确认',
      width: '1200px',
    });
  };

  // 类型的实效性根据类型展示不同的输入
  const timingParser = () => {
    if (item.timing_type === 'ASSIGN_UNAVAILABLE') {
      return (
        <Row>
          <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="过期时间">
            {form.getFieldDecorator('expire_time', {
              initialValue: item.expire_time,
            })(<Input placeholder="实效性为'指定时间失效'填写" />)}
          </FormItem>
        </Row>
      );
    }

    if (item.timing_type === 'SCHEDULE_UNAVAILABLE') {
      return (
        <Row>
          <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="任务分组">
            {form.getFieldDecorator('task_group', {
              initialValue: item.task_group,
            })(<Input placeholder="实效性为'调度周期失效'填写" />)}
          </FormItem>
          <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="任务name">
            {form.getFieldDecorator('task_name', {
              initialValue: item.task_name,
            })(<Input placeholder="实效性为'调度周期失效'填写" />)}
          </FormItem>
        </Row>
      );
    }

    return <Row />;
  };

  // 启动配置的值的类型只有yml和properties
  const valTypeParser = () => {
    if (item.tag === 'start') {
      return (
        <Row>
          <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="值类型">
            {form.getFieldDecorator('val_type', {
              initialValue: item.val_type,
              rules: [{ required: true, message: '请选择value的类型！' }],
            })(
              <Select showSearch style={{ width: '100%' }}>
                <Select.Option value="YML">yaml类型</Select.Option>
                <Select.Option value="PROPERTY">property类型</Select.Option>
              </Select>
            )}
          </FormItem>
        </Row>
      );
    }
    return (
      <Row>
        <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="值类型">
          {form.getFieldDecorator('val_type', {
            initialValue: item.val_type,
            rules: [{ required: true, message: '请选择value的类型！' }],
          })(
            <Select showSearch style={{ width: '100%' }}>
              <Select.Option value="STRING">字符类型</Select.Option>
              <Select.Option value="JSON">json类型</Select.Option>
              <Select.Option value="YML">yaml类型</Select.Option>
              <Select.Option value="PROPERTY">property类型</Select.Option>
            </Select>
          )}
        </FormItem>
      </Row>
    );
  };

  return (
    <Modal
      destroyOnClose
      title="修改"
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => hideEditModal()}
      width="800px"
    >
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置id">
        {form.getFieldDecorator('id', {
          initialValue: item.id,
        })(<Input disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置组">
        {form.getFieldDecorator('group_code', {
          initialValue: item.group_code,
          rules: [{ required: true, message: '请输入配置code！' }],
        })(<Input placeholder="请输入 code" disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置key">
        {form.getFieldDecorator('conf_key', {
          initialValue: item.conf_key,
          rules: [{ required: true, message: '请输入配置key！' }],
        })(<Input placeholder="请输入 conf_key" disabled />)}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置标签">
        {form.getFieldDecorator('tag', {
          initialValue: item.tag,
        })(<Input placeholder="请输入 tag（start表示启动标签配置）" disabled />)}
      </FormItem>
      {valTypeParser()}
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="配置值">
        {form.getFieldDecorator('conf_value', {
          initialValue: item.conf_value,
          rules: [{ required: true, message: '请输入配置value！' }],
        })(
          <Row>
            <Col span={22}>
              <TextArea rows={8} defaultValue={item.conf_value} />
            </Col>
            <Col span={1} />
            <Col span={1}>
              <Button type="primary" shape="circle" icon="zoom-in" onClick={() => zoomIn()} />
            </Col>
          </Row>
        )}
      </FormItem>
      <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="备注">
        {form.getFieldDecorator('remark', {
          initialValue: item.remark,
          rules: [{ required: true, message: '配置项描述！' }],
        })(<Input placeholder="请输入 remark" />)}
      </FormItem>
  {/*    <FormItem labelCol={{ span: 4 }} wrapperCol={{ span: 18 }} label="实效性">
        {form.getFieldDecorator('timing_type', {
          initialValue: item.timing_type,
        })(
          <Select showSearch style={{ width: '100%' }} onSelect={setTimingType}>
            <Select.Option value="PERM_AVAILABLE">永久有效</Select.Option>
            <Select.Option value="SCHEDULE_UNAVAILABLE">调度周期失效</Select.Option>
          </Select>
        )}
      </FormItem>*/}
      {timingParser()}
    </Modal>
  );
});

const IconStatus = Icon.createFromIconfontCN({
  scriptUrl: '//at.alicdn.com/t/font_1023507_jssj9r31a37.js',
});

/* eslint react/no-multi-comp:0 */
@connect(({ configItem, loading }) => ({
  configItem,
  loading: loading.models.configItem,
}))
// @Form.create() 是一个注解，就简化了xxx = Form.create(xxx);export xxx
@Form.create()
class ConfigItemList extends PureComponent {
  state = {
    addModalVisible: false,
    editModalVisible: false,
    timingType: '',
    item: {},
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
      width: '15%',
    },
    {
      key: 'conf_key',
      title: '配置key',
      dataIndex: 'conf_key',
      width: '15%',
    },
    {
      key: 'tag',
      title: '标签',
      dataIndex: 'tag',
      width: '5%',
    },
    {
      key: 'remark',
      title: '描述',
      dataIndex: 'remark',
      width: '36%',
    },
    {
      key: 'create_time',
      title: '创建时间',
      dataIndex: 'create_time',
      width: '10%',
      render: text => <span>{moment(text).format('YYYY-MM-DD HH:mm:ss')}</span>,
    },
    {
      name: 'status',
      title: '状态',
      dataIndex: 'status',
      width: '4%',
      render: (text, record) => {
        if (record.status === 'Y') {
          return <IconStatus type="icon-zhengchang" style={{ fontSize: '20px' }} onClick={() => this.showUnloadConfirm(record)} />;
        }
        return <IconStatus type="icon-jinyong" style={{ fontSize: '20px' }} onClick={() => this.showLoadConfirm(record)} />;
      },
    },
    {
      key: 'edit',
      title: '编辑',
      dataIndex: 'edit',
      width: '5%',
      render: (text, record) => (
        <span>
          <Button type="primary" icon="edit" onClick={() => this.showEditModal(record)} />
        </span>
      ),
    },
    {
      key: 'delete',
      title: '删除',
      dataIndex: 'delete',
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
      configItem: { activePaneName },
    } = this.props;
    console.log('启动');

    // 获取所有组code列表
    dispatch({
      type: 'configItem/fetchAllCodeList',
    });

    // 获取组code列表
    dispatch({
      type: 'configItem/fetchCodeList',
    });

    // 获取页面的总个数
    this.getPageDate(activePaneName, 1);
  }

  getPageDate(name, pageNo, searchParam) {
    const { dispatch } = this.props;
    const {
      configItem: { panes },
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
        type: 'configItem/getListCount',
        payload: {
          paneIndex: index,
          searchParam: param,
        },
      });

      dispatch({
        type: 'configItem/getPageList',
        payload: {
          paneIndex: index,
          pager,
          searchParam: param,
        },
      });
    }
  }

  showTimingType = timingType => {
    if (timingType === 'PERM_AVAILABLE') {
      return <span>永久有效</span>;
    }
    if (timingType === 'ASSIGN_UNAVAILABLE') {
      return <span>指定时间失效</span>;
    }
    if (timingType === 'SCHEDULE_UNAVAILABLE') {
      return <span>调度周期失效</span>;
    }
    return <span />;
  };

  timingParser = record => {
    if (record.timing_type === 'SCHEDULE_UNAVAILABLE') {
      return (
        <Row>
          <Col span={6}>
            <Badge status="success" text="调度分组group：" />
            <span>{record.task_group}</span>
          </Col>
          <Col span={6}>
            <Badge status="success" text="调度任务name：" />
            <span>{record.task_name}</span>
          </Col>
        </Row>
      );
    }

    return <Row />;
  };

  dataParser = record => {
    if (record.val_type === 'JSON') {
      return (
        <div>
          <Row>
            <Col span={24}>
              <CodeMirror
                value={record.conf_value}
                options={{
                  mode: 'application/json',
                  theme: 'solarized light',
                  lineNumbers: true,
                }}
              />
            </Col>
          </Row>
        </div>
      );
    }

    if (record.val_type === 'YML') {
      return (
        <div>
          <Row>
            <Col span={24}>
              <CodeMirror
                value={record.conf_value}
                options={{
                  mode: 'text/x-yaml',
                  theme: 'solarized light',
                  lineNumbers: true,
                }}
              />
            </Col>
          </Row>
        </div>
      );
    }

    if (record.val_type === 'PROPERTY') {
      return (
        <div>
          <Row>
            <Col span={24}>
              <CodeMirror
                value={record.conf_value}
                options={{
                  mode: 'text/x-properties',
                  theme: 'solarized light',
                  lineNumbers: true,
                }}
              />
            </Col>
          </Row>
        </div>
      );
    }

    if (record.val_type === 'GROOVY') {
      return (
        <div>
          <Row>
            <Col span={24}>
              <CodeMirror
                value={record.conf_value}
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
          <Col span={12}>
            <Badge status="success" text="配置：" />
            <span>{record.conf_value}</span>
          </Col>
        </Row>
      </div>
    );
  };

  expandedRowRender = record => (
    <div>
      <Row>
        <Col span={6}>
          <Badge status="success" text="组名称：" />
          <span>{record.group_code}</span>
        </Col>
        <Col span={6}>
          <Badge status="success" text="实效性：" />
          {this.showTimingType(record.timing_type)}
        </Col>
        <Col span={6}>
          <Badge status="success" text="创建时间：" />
          <span>{moment(record.create_time).format('YYYY-MM-DD HH:mm:ss')}</span>
        </Col>
        <Col span={6}>
          <Badge status="success" text="更新时间：" />
          <span>{moment(record.update_time).format('YYYY-MM-DD HH:mm:ss')}</span>
        </Col>
      </Row>
      <Divider style={{marginBottom: 32}}/>
      <Row>
        <Col span={12}>
          <Badge status="success" text="描述：" />
          <span>{record.remark}</span>
        </Col>
        <Col span={6}>
          <Badge status="success" text="标签：" />
          <span>{record.tag}</span>
        </Col>
      </Row>
      <Divider style={{marginBottom: 32}}/>
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
          type: 'configItem/delete',
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
      timingType: '',
    });
  };

  showEditModal = record => {
    this.setState({
      item: record,
      editModalVisible: true,
    });
  };

  hideEditModal = () => {
    const { item } = this.state;

    this.setState({
      editModalVisible: false,
      item,
    });
  };

  // 设置表格加载
  setTableLoading = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'configItem/setTableLoading',
    });
  };

  getActivePaneIndex = () => {
    const {
      configItem: { activePaneName, panes },
    } = this.props;

    return panes.findIndex(pane => pane.name === activePaneName);
  };

  // 添加配置项
  handleAdd = fields => {
    const { dispatch } = this.props;
    this.setTableLoading();

    console.log('添加的数据');
    console.log(JSON.stringify(fields));

    // 将中间添加的脚本放进去
    const params = {
      ...fields,
      paneIndex: this.getActivePaneIndex(),
      timing_type: 'PERM_AVAILABLE'
    };

    dispatch({
      type: 'configItem/add',
      payload: params,
    });

    this.hideAddModal();
  };

  // 设置当前选择的实效性
  setTimingType = value => {
    console.log('点击选择，value = ');
    console.log(value);

    this.setState({
      timingType: value,
    });
  };

  setUpdateTimingType = value => {
    const { item } = this.state;

    item.timing_type = value;
    this.setState({
      item,
    });
  };

  // 判断对象1是否包含对象2的所有属性
  contain = (object1, object2) => {
    let index = 0;
    const keys = Object.keys(object2);
    for (let i = 0; i < keys.length; i += 1) {
      const key = keys[i];
      if (object1[key] && object2[key] === object1[key]) {
        index += 1;
      }
    }
    return index === Object.keys(object2).length;
  };

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
      type: 'configItem/disable',
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
      type: 'configItem/enable',
      payload: item,
    });
  };

  // 下架
  showUnloadConfirm = row => {
    const { dispatch } = this.props;
    console.log('点击');
    console.log(JSON.stringify(row));
    const paneIndex = this.getActivePaneIndex();
    const showLoading = ()=>this.setTableLoading();
    Modal.confirm({
      title: '确定要下架',
      okText: '确定下架',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        showLoading();
        console.log('OK');
        dispatch({
          type: 'configItem/unload',
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

  // 上架
  showLoadConfirm = row => {
    const { dispatch } = this.props;
    console.log('点击');
    console.log(JSON.stringify(row));
    const paneIndex = this.getActivePaneIndex();
    const showLoading = ()=>this.setTableLoading();
    Modal.confirm({
      title: '确定要上架',
      okText: '确定上架',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        showLoading();
        console.log('OK');
        dispatch({
          type: 'configItem/load',
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

  handleEdit = fields => {
    const { dispatch } = this.props;
    const { item } = this.state;
    this.setTableLoading();

    console.log('编辑修改');
    console.log(JSON.stringify(fields));
    console.log(JSON.stringify(item));

    // 判断是否有修改，如果没有修改，则不向后端发起更新
    // if (!this.contain(item, fields)) {
    //   console.log('有变化需要修改');
    const params = {
      ...Object.assign(item, fields),
      paneIndex: this.getActivePaneIndex(),
    };

    console.log(JSON.stringify(params));
    dispatch({
      type: 'configItem/update',
      payload: params,
    });
    // }

    this.hideEditModal();
  };

  handleSearch = e => {
    e.preventDefault();

    const { form } = this.props;
    const {
      configItem: { activePaneName },
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
      if (fieldsValue !== undefined) {
        Object.keys(fieldsValue).forEach(key => {
          console.log(key, fieldsValue[key]);
          if (fieldsValue[key] === '') {
            delete fieldsValue[key];
          }
        });
      }
      /* eslint-disable no-param-reassign */

      console.log(JSON.stringify(fieldsValue));

      this.getPageDate(activePaneName, 1, fieldsValue);
    });
  };

  // 加载搜索输入框和搜索按钮
  renderSearchForm = () => {
    const {
      form: { getFieldDecorator },
      configItem: { groupCodeList },
    } = this.props;

    // console.log("加载groupCodeList")
    // console.log(JSON.stringify(groupCodeList));

    const options = groupCodeList.map(d => <Select.Option key={d.value}>{d.text}</Select.Option>);

    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={6} sm={24}>
            <FormItem label="配置组">
              {getFieldDecorator('group_code')(
                <Select
                  allowClear
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
          <Col md={5} sm={24}>
            <FormItem label="key">
              {getFieldDecorator('conf_key')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={4} sm={24}>
            <FormItem label="标签">
              {getFieldDecorator('tag')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={4} sm={24}>
            <FormItem label="状态">
              {getFieldDecorator('status')(
                <Select allowClear>
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
          <Col md={1} sm={24}>
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
      configItem: { activePaneName },
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
      type: 'configItem/activePane',
      payload: activePaneName,
    });
  };

  render() {
    const {
      configItem: { selectState, groupAllCodeList },
    } = this.props;
    const { addModalVisible, editModalVisible, timingType, item } = this.state;
    const parentAddMethods = {
      selectState,
      groupAllCodeList,
      timingType,
      handleAdd: this.handleAdd,
      setTimingType: this.setTimingType,
      hideAddModal: this.hideAddModal,
    };
    const parentEditMethods = {
      item,
      handleEdit: this.handleEdit,
      setTimingType: this.setUpdateTimingType,
      hideEditModal: this.hideEditModal,
    };

    const {
      configItem: { panes, activePaneName },
    } = this.props;

    const tabPanes = panes.map(pane => (
      <Tabs.TabPane tab={pane.title} key={pane.name}>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderSearchForm()}</div>
            <div className={styles.tableListOperator} />

            <Table
              rowKey={record => record.id}
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

export default ConfigItemList;
