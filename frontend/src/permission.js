import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

// 判断是否为管理端路由（无角色用户禁止访问）
function isAdminLikePath(path) {
  if (!path) return false
  try {
    const p = String(path)
    return p === '/'
      || p === '/index'
      || p.startsWith('/index')
      || p.startsWith('/manage')
      || p.startsWith('/system')
      || p.startsWith('/monitor')
      || p.startsWith('/tool')
  } catch (e) {
    return false
  }
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (useUserStore().roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        useUserStore().getInfo().then(() => {
          isRelogin.show = false
          usePermissionStore().generateRoutes().then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            accessRoutes.forEach(route => {
              if (!isHttp(route.path)) {
                router.addRoute(route) // 动态添加可访问路由表
              }
            })
            const user = useUserStore()
            if (user.portalOnly) {
              // 无角色用户：强制跳转门户（若当前目标为管理端或根路径）
              if (isAdminLikePath(to.path) || to.path === '/' || to.path === '/index') {
                next({ path: '/portal', replace: true })
              } else {
                next({ ...to, replace: true })
              }
            } else {
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            }
          })
        }).catch(err => {
          useUserStore().logOut().then(() => {
            ElMessage.error(err)
            next({ path: '/' })
          })
        })
      } else {
        const user = useUserStore()
        if (user.portalOnly && isAdminLikePath(to.path)) {
          // 已登录且为门户用户，禁止进入管理端
          if (to.path !== '/portal') {
            ElMessage.warning('当前账号未分配角色，仅可访问门户')
          }
          next({ path: '/portal' })
        } else {
          next()
        }
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
