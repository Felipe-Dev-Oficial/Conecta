FROM node:22.19.0-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install --network=host

# Copia o código fonte
COPY . .

RUN npm run build -- --configuration production

FROM node:22.19.0-alpine

WORKDIR /app

COPY --from=build /app/dist/ui ./dist/ui

ENV PORT=3000
EXPOSE 3000

CMD ["node", "dist/ui/server/server.mjs"]