// URL 工具：外链规范化与打开新标签
// 说明：
// - 将常见误写如 https;// 修正为 https://
// - 将中文全角符号（：；）转换为半角
// - 若无协议前缀，则默认补全为 https://

export function normalizeExternalUrl(input) {
  if (!input) return ''
  let url = String(input).trim()
  // 全角转半角
  url = url.replace(/；/g, ';').replace(/：/g, ':')
  // 常见误写修正
  url = url.replace(/;\/\//g, '://')
  url = url.replace(/^https;\/\//i, 'https://').replace(/^http;\/\//i, 'http://')
  // 已含协议或特殊协议
  if (/^(https?:|mailto:|tel:)/i.test(url)) return url
  // 协议相对 //example.com
  if (/^\/\//.test(url)) return 'https:' + url
  // 其他协议如 ftp:// 保留
  if (/^[a-z]+:\/\//i.test(url)) return url
  // 默认补全 https://
  return 'https://' + url.replace(/^\/+/, '')
}

export function openExternal(url) {
  const target = normalizeExternalUrl(url)
  if (target) window.open(target, '_blank')
}

