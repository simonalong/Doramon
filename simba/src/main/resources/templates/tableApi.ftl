import request from '@/utils/request';

const path = '${backendUrl}';

export async function add(params) {
  console.log('${tablePathNameLower}Api.add 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${r"${path}"}/${tableUrlName}/add`, {
    method: 'PUT',
    body: {
      ...params,
    },
  });
}

export async function deleteData(params) {
  console.log('${tablePathNameLower}Api.deleteData 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${r"${path}"}/${tableUrlName}/delete/${r"${params}"}`, {
    method: 'DELETE',
  });
}

export async function update(params) {
  console.log('${tablePathNameLower}Api.update 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${r"${path}"}/${tableUrlName}/update`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageCount(params) {
  console.log('${tablePathNameLower}Api.pageCount 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${r"${path}"}/${tableUrlName}/count`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}

export async function pageList(params) {
  console.log('${tablePathNameLower}Api.pageList 发送的参数');
  console.log(JSON.stringify(params));
  return request(`${r"${path}"}/${tableUrlName}/pageList`, {
    method: 'POST',
    body: {
      ...params,
    },
  });
}





