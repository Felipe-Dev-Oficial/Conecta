FROM node:22.19.0-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration production

FROM nginx:alpine
COPY --from=build /app/dist/ui/browser /usr/share/nginx/html
EXPOSE 80
