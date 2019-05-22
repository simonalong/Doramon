import request from '@/utils/request';

const path = '/simon/tina/api/v1';

export async function add(params) {
  console.log('taskApi.add 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/add`, {
    method: 'PUT',
    body: {
      ...params,
    },
  });
}

export async function deleteData(params) {
  console.log('taskApi.deleteData 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/delete/${params}`, {
    method: 'DELETE',
  });
}

export async function update(params) {
  console.log('taskApi.update 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/update`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageCount(params) {
  console.log('taskApi.pageCount 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/count`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageList(params) {
  console.log('taskApi.pageList 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/pageList`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function disable(params) {
  console.log('taskApi.disable 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/disable`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function enable(params) {
  console.log('taskApi.enable 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/enable`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function handRun(params) {
  console.log('taskApi.handRun 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/task/handRun`, {
    method: 'POST',
    body: {
      type: params.task_type,
      data: params.data,
      param: params.param,
    },
  });
}

export async function run(params) {
  console.log('taskApi.run 发送的参数');
  console.log(JSON.stringify(params));
  const result = request(`${path}/task/run`, {
    method: 'POST',
    body: {
      type: params.task_type,
      data: params.data,
      param: params.param,
    },
  });

  console.log('结果');
  console.log(result);
  return result;
}

export async function getAllCodeList() {
  console.log('taskApi.getAllCodeList 发送');
  return request(`${path}/config_group/codeList`);
}

export async function getCodeList() {
  console.log('taskApi.getCodeList 发送');
  return request(`${path}/task/codeList`);
}
