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
      {
        path: '/config',
        icon: 'setting',
        name: 'config',
        routes: [
          {
            path: '/config/config-group-list',
            name: 'configgrouplist',
            component: './config/ConfigGroupList',
          },
          {
            path: '/config/config-item-list',
            name: 'configitemlist',
            component: './config/ConfigItemList',
          },
        ],
      },
      {
        component: '404',
      },
    ],
  },
];
