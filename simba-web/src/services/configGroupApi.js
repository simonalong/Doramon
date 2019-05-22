import request from '@/utils/request';

const path = '/simon/tina/api/v1';
export async function getPageList(params) {
  console.log('configGroupApi.getPageList 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_group/pageList`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function addConfigGroup(params) {
  console.log('configGroupApi.addConfigGroup 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_group/add`, {
    method: 'PUT',
    body: {
      ...params,
    },
  });
}

export async function deleteConfigGroup(params) {
  console.log('configGroupApi.deleteConfigGroup 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_group/delete/${params}`, {
    method: 'DELETE',
  });
}

export async function updateConfigGroup(params) {
  console.log('configGroupApi.updateConfigGroup 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_group/update`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageCount(params) {
  console.log('configGroupApi.pageCount 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_group/count`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}
