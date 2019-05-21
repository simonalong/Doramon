export default [
  // user
  {
    path: '/user',
    component: '../layouts/UserLayout',
    routes: [
      { path: '/user', redirect: '/user/login' },
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
      { path: '/user/register-result', component: './User/RegisterResult' },
    ],
  },
  // app
  {
    path: '/',
    component: '../layouts/BasicLayout',
    Routes: ['src/pages/Authorized'],
    authority: ['admin', 'user'],
    routes: [
      // dashboard
      { path: '/', redirect: '/dashboard/analysis' },
<#list tableComponentInfos! as tableComponentInfos>
      {
        path: '${tableComponentInfos.tableName}',
        name: '${tableComponentInfos.tablePathName}List',
        component: './${dbName}/${tableComponentInfos.tablePathName}List',
      },
      </#list>
      {
        component: '404',
      },
    ],
  },
];
