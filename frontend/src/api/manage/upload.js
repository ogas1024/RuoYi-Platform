import request from '@/utils/request'

// OSS 文件上传
// 使用方法：
// const form = new FormData();
// form.append('file', file);
// uploadOss(form, { dir: 'images', publicUrl: true })
export function uploadOss(formData, query) {
    return request({
        url: '/manage/upload/oss',
        method: 'post',
        params: query,
        // 关闭防重复提交（FormData 序列化为 "{}" 会误判为重复）并静默错误提示
        headers: {'Content-Type': 'multipart/form-data', repeatSubmit: false, silent: true},
        data: formData
    })
}
