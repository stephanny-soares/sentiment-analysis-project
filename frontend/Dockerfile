FROM nginx:alpine

# Remove default config and add our own static serving config
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy static assets
COPY index.html /usr/share/nginx/html/index.html
COPY app.css /usr/share/nginx/html/app.css
COPY app.js /usr/share/nginx/html/app.js

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]