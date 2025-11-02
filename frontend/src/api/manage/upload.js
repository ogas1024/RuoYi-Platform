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
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData
  })
}

