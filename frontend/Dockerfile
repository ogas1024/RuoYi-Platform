FROM node:20-alpine AS build

WORKDIR /app

COPY frontend/package*.json ./
# 使用 npm install 而非 npm ci，避免因锁文件与 package.json 轻微不一致导致构建失败。
RUN npm install --registry=https://registry.npmmirror.com

COPY frontend ./

RUN npm run build:prod

FROM nginx:1.27-alpine

ENV TZ=Asia/Shanghai

WORKDIR /usr/share/nginx/html

COPY --from=build /app/dist ./

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
