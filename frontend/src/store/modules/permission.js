import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

// 匹配views里面所有的.vue文件
const modules = import.meta.glob('./../../views/**/*.vue')

const usePermissionStore = defineStore(
  'permission',
  {
    state: () => ({
      routes: [],
      addRoutes: [],
      defaultRoutes: [],
      topbarRouters: [],
      sidebarRouters: []
    }),
    actions: {
      setRoutes(routes) {
        this.addRoutes = routes
        this.routes = constantRoutes.concat(routes)
      },
      setDefaultRoutes(routes) {
        this.defaultRoutes = constantRoutes.concat(routes)
      },
      setTopbarRoutes(routes) {
        this.topbarRouters = routes
      },
      setSidebarRouters(routes) {
        this.sidebarRouters = routes
      },
      generateRoutes(roles) {
        return new Promise(resolve => {
          // 向后端请求路由数据
          getRouters().then(res => {
            const sdata = JSON.parse(JSON.stringify(res.data))
            const rdata = JSON.parse(JSON.stringify(res.data))
            const defaultData = JSON.parse(JSON.stringify(res.data))
            // 将后端返回路由转为组件并规范化名称，避免父子/同级路由 name 冲突
            const sidebarRoutes = ensureUniqueNames(filterAsyncRouter(sdata))
            // 确保不会用后端菜单覆盖本地首页 /index
            const rewriteRoutesAll = ensureUniqueNames(filterAsyncRouter(rdata, false, true))
            const rewriteRoutes = stripHomeIndex(rewriteRoutesAll)
            const defaultRoutes = ensureUniqueNames(filterAsyncRouter(defaultData))
            const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
            asyncRoutes.forEach(route => { router.addRoute(route) })
            this.setRoutes(rewriteRoutes)
            this.setSidebarRouters(constantRoutes.concat(sidebarRoutes))
            this.setDefaultRoutes(sidebarRoutes)
            this.setTopbarRoutes(defaultRoutes)
            resolve(rewriteRoutes)
          })
        })
      }
    }
  })

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  const children = []
  childrenMap.forEach(el => {
    const parentPath = lastRouter ? String(lastRouter.path || '') : ''
    const childPath = String(el.path || '').replace(/^\/+/, '') // 移除子路径前导 '/'
    el.path = normalizeJoin(parentPath, childPath)
    if (el.children && el.children.length && el.component === 'ParentView') {
      children.push(...filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  const res = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route)
      }
    }
  })
  return res
}

export const loadView = (view) => {
  let res
  for (const path in modules) {
    const dir = path.split('views/')[1].split('.vue')[0]
    if (dir === view) {
      res = () => modules[path]()
    }
  }
  return res
}

// 从后端路由中剔除任何指向 /index 的记录，避免覆盖本地首页
function stripHomeIndex(routes) {
  function prune(list) {
    return list
      .filter(r => normalizePath(r.path) !== '/index')
      .map(r => ({
        ...r,
        children: r.children && r.children.length ? prune(r.children) : r.children
      }))
  }
  return prune(routes)
}

function normalizePath(p) {
  if (!p) return '/'
  const s = String(p)
  // 保持与 router 解析一致，去除多余斜杠
  return ('/' + s).replace(/\/+/, '/').replace(/\/+$/,'') || '/'
}

// 统一清洗并确保路由 name 唯一（避免出现父子同名或同级重名导致 addRoute 报错）
function ensureUniqueNames(routes, ancestorNames = new Set()) {
  const seen = new Set()
  routes.forEach((route, idx) => {
    // 标准化 name：优先使用已有 name，否则用 path 生成；统一去除特殊字符
    const rawName = route.name || route.path || `route_${idx}`
    let safeName = sanitizeName(rawName)

    // 如果与祖先/同级冲突，则附加基于 path 的后缀直到唯一
    const baseSuffix = sanitizeName(route.path || `child_${idx}`)
    let counter = 0
    while (ancestorNames.has(safeName) || seen.has(safeName)) {
      counter += 1
      safeName = `${safeName}_${baseSuffix}${counter > 1 ? '_' + counter : ''}`
    }
    route.name = safeName

    // 递归处理子路由
    if (route.children && route.children.length) {
      const nextAncestors = new Set(ancestorNames)
      nextAncestors.add(route.name)
      ensureUniqueNames(route.children, nextAncestors)
    }

    seen.add(route.name)
  })
  return routes
}

function sanitizeName(name) {
  if (typeof name !== 'string') {
    name = String(name)
  }
  // 将不适合作为 name 的字符统一替换为下划线，保持稳定且可读
  const n = name.trim()
  return n.replace(/[^A-Za-z0-9_\-]/g, '_').replace(/^_+/, '').replace(/_+$/, '') || 'route'
}

// 规范化路径拼接，避免出现重复斜杠，如 //manage//vote
function normalizeJoin(parent, child) {
  const p = String(parent || '').replace(/\/+$/, '')
  const c = String(child || '').replace(/^\/+/, '')
  const res = [p, c].filter(Boolean).join('/')
  return res.startsWith('/') ? res.replace(/\/+/, '/') : '/' + res
}

export default usePermissionStore
