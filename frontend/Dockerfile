FROM node:20-alpine AS build

WORKDIR /app

# 安装并使用 pnpm（比 npm 快 2-3 倍）
RUN npm install -g pnpm@latest --registry=https://registry.npmmirror.com

# 提升 Node 构建阶段可用堆内存，避免大型 Vite 打包在低内存宿主机上 OOM
ENV NODE_OPTIONS="--max-old-space-size=2048"

COPY frontend/package*.json ./
# 使用 pnpm install 替代 npm install，更快且更省空间
RUN pnpm install --registry=https://registry.npmmirror.com

COPY frontend ./

RUN pnpm run build:prod

FROM nginx:1.27-alpine

ENV TZ=Asia/Shanghai

WORKDIR /usr/share/nginx/html

COPY --from=build /app/dist ./

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
