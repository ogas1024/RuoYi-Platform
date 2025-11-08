import request from '@/utils/request'

// 门户图片上传（仅登录可用；限制为图片且<=2MB）
export function uploadOssPortal(formData, query) {
    return request({
        url: '/portal/upload/oss',
        method: 'post',
        params: query,
        // 关闭防重复提交并静默全局错误提示（由组件内统一提示）
        headers: {'Content-Type': 'multipart/form-data', repeatSubmit: false, silent: true},
        data: formData
    })
}
