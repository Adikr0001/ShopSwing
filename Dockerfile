FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM tomcat:9.0
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
FROM tomcat:9.0
RUN rm -rf /usr/local/tomcat/webapps/* \
    && mkdir -p /usr/local/tomcat/data
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
COPY scripts/render-entrypoint.sh /usr/local/bin/render-entrypoint.sh
RUN chmod +x /usr/local/bin/render-entrypoint.sh

# Use SQLite only (ignore Render DATABASE_URL if a Postgres DB is linked). Data survives container restarts
# but not redeploys unless you attach a Render disk and point SHOPSWING_SQLITE_PATH there.
ENV SHOPSWING_USE_SQLITE_ONLY=1
ENV SHOPSWING_SQLITE_PATH=/usr/local/tomcat/data/shopswing.db

# Render forwards HTTP to $PORT. Official Tomcat image listens on 8080 until entrypoint patches server.xml.
EXPOSE 10000
CMD ["/usr/local/bin/render-entrypoint.sh"]
