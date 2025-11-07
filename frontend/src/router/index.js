import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  // 管理端：课程资源分享（静态注册，避免菜单未配置时404）
  {
    path: '/manage/course-resource',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'audit',
        name: 'ManageCourseResourceAudit',
        component: () => import('@/views/manage/courseResource/audit.vue'),
        meta: { title: '课程资源-待审核' }
      },
      {
        path: 'approved',
        name: 'ManageCourseResourceApproved',
        component: () => import('@/views/manage/courseResource/approved.vue'),
        meta: { title: '课程资源-已上架' }
      },
      {
        path: 'trash',
        name: 'ManageCourseResourceTrash',
        component: () => import('@/views/manage/courseResource/trash.vue'),
        meta: { title: '课程资源-回收站' }
      }
    ]
  },
  // 管理端：失物招领（静态注册，避免菜单未配置时404）
  {
    path: '/manage/lostfound',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'index',
        name: 'ManageLostFoundIndex',
        component: () => import('@/views/manage/lostfound/List.vue'),
        meta: { title: '失物招领-已发布' }
      },
      {
        path: 'audit',
        name: 'ManageLostFoundAudit',
        component: () => import('@/views/manage/lostfound/Audit.vue'),
        meta: { title: '失物招领-待审核' }
      },
      {
        path: 'recycle',
        name: 'ManageLostFoundRecycle',
        component: () => import('@/views/manage/lostfound/Recycle.vue'),
        meta: { title: '失物招领-回收站' }
      }
    ]
  },
  // 门户（普通用户）常量路由：使用独立布局，不依赖后台菜单
  {
    path: '/portal',
    component: () => import('@/layout/portal/PortalLayout.vue'),
    hidden: true,
    children: [
      // 课程资源分享模块：模块化前缀 /portal/course-resource
      {
        path: 'course-resource',
        name: 'PortalCourseResource',
        component: () => import('@/views/portal/courseResource/index.vue'),
        meta: { title: '课程资源' }
      },
      {
        path: 'course-resource/course',
        name: 'PortalCourse',
        component: () => import('@/views/portal/courseResource/course.vue'),
        meta: { title: '课程' }
      },
      {
        path: 'course-resource/list',
        name: 'PortalResourceList',
        component: () => import('@/views/portal/courseResource/list.vue'),
        meta: { title: '资源列表' }
      },
      {
        path: 'course-resource/my',
        name: 'PortalMyResource',
        component: () => import('@/views/portal/courseResource/my.vue'),
        meta: { title: '我上传的' }
      },
      {
        path: 'course-resource/edit',
        name: 'PortalEditResource',
        component: () => import('@/views/portal/courseResource/edit.vue'),
        meta: { title: '编辑资源' }
      },
      {
        path: 'course-resource/top',
        name: 'PortalTop',
        component: () => import('@/views/portal/courseResource/top.vue'),
        meta: { title: '排行榜' }
      },
      // 门户通知公告（与课程资源分享并列）
      {
        path: 'notice',
        name: 'PortalNotice',
        component: () => import('@/views/portal/notice/index.vue'),
        meta: { title: '通知公告' }
      }
      ,
      // 数字图书馆（与课程资源分享并列）：/portal/library
      {
        path: 'library',
        name: 'PortalLibrary',
        component: () => import('@/views/portal/library/List.vue'),
        meta: { title: '数字图书馆' }
      },
      // 功能房预约门户
      {
        path: 'facility',
        name: 'PortalFacility',
        component: () => import('@/views/portal/facility/List.vue'),
        meta: { title: '功能房' }
      },
      {
        path: 'facility/detail',
        name: 'PortalFacilityDetail',
        component: () => import('@/views/portal/facility/Detail.vue'),
        meta: { title: '功能房详情' }
      },
      {
        path: 'facility/my',
        name: 'PortalFacilityMy',
        component: () => import('@/views/portal/facility/My.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'library/list',
        name: 'PortalLibraryList',
        component: () => import('@/views/portal/library/List.vue'),
        meta: { title: '全部图书' }
      },
      {
        path: 'library/detail',
        name: 'PortalLibraryDetail',
        component: () => import('@/views/portal/library/Detail.vue'),
        meta: { title: '图书详情' }
      },
      {
        path: 'library/edit',
        name: 'PortalLibraryEdit',
        component: () => import('@/views/portal/library/Edit.vue'),
        meta: { title: '编辑图书' }
      },
      {
        path: 'library/upload',
        name: 'PortalLibraryUpload',
        component: () => import('@/views/portal/library/Upload.vue'),
        meta: { title: '上传图书' }
      },
      {
        path: 'library/top',
        name: 'PortalLibraryTop',
        component: () => import('@/views/portal/library/Top.vue'),
        meta: { title: '下载与贡献榜' }
      },
      {
        path: 'library/contributions',
        name: 'PortalLibraryContributions',
        component: () => import('@/views/portal/library/Contributions.vue'),
        meta: { title: '我的上传' }
      },
      {
        path: 'library/fav',
        name: 'PortalLibraryFavorite',
        component: () => import('@/views/portal/book/Favorite.vue'),
        meta: { title: '我的收藏' }
      }
      ,
      // 失物招领门户
      {
        path: 'lostfound',
        name: 'PortalLostFound',
        component: () => import('@/views/portal/lostfound/List.vue'),
        meta: { title: '失物招领' }
      },
      {
        path: 'lostfound/edit',
        name: 'PortalLostFoundEdit',
        component: () => import('@/views/portal/lostfound/Edit.vue'),
        meta: { title: '发布/编辑失物' }
      },
      {
        path: 'lostfound/my',
        name: 'PortalLostFoundMy',
        component: () => import('@/views/portal/lostfound/My.vue'),
        meta: { title: '我的发布' }
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/portal/course-resource',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

export default router
