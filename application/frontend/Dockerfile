FROM nginx:alpine

COPY ./src/index.html /usr/share/nginx/html/index.html
COPY ./src/output.css /usr/share/nginx/html/output.css
COPY ./src/script.js /usr/share/nginx/html/script.js
COPY ./src/static/favicon.png /usr/share/nginx/html/static/favicon.png

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]