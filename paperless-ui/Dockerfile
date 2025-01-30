FROM nginx:alpine

RUN rm -rf /etc/nginx/conf.d

COPY ./index.html /usr/share/nginx/html/
COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./js /usr/share/nginx/html/js/
COPY ./pages /usr/share/nginx/html/pages/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]