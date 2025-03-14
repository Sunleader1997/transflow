const routes = [
  {
    path: '/mgmt',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '/mgmt/job',
        component: () => import('pages/mgmt/job/JobIndex.vue'),
        children: [
          {
            name: "jobPanel",
            path: '/mgmt/job/panel/:jobId',
            component: () => import('pages/mgmt/job/model/JobPanel.vue'),
            props: true
          },
        ],
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
