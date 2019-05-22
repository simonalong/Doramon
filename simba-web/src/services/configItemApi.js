import request from '@/utils/request';

const path = '/simon/tina/api/v1';
export async function getAllCodeList() {
  console.log('configItemApi.getAllCodeList 发送');
  return request(`${path}/config_group/codeList`);
}

export async function getCodeList() {
  console.log('configItemApi.getCodeList 发送');
  return request(`${path}/config_item/codeList`);
}

export async function getPageList(params) {
  console.log('configItemApi.getPageList 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/pageList`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function addConfigItem(params) {
  console.log('configItemApi.addConfigItem 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/add`, {
    method: 'PUT',
    body: {
      ...params,
    },
  });
}

export async function deleteConfigItem(params) {
  console.log('configItemApi.deleteConfigItem 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/delete/${params}`, {
    method: 'DELETE',
  });
}

export async function unloadData(params) {
  console.log('configItemApi.unloadData 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/unload`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function loadData(params) {
  console.log('configItemApi.loadData 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/load`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function updateConfigItem(params) {
  console.log('configItemApi.updateConfigItem 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/update`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageCount(params) {
  console.log('configItemApi.pageCount 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${path}/config_item/count`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}
