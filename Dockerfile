ARG REGISTRY="docker.io"

#
# Build stage
#

FROM $REGISTRY/library/maven:3-eclipse-temurin-17 AS builder

#  Corporate proxy definition when necessary
ARG http_proxy_protocol
ARG http_proxy_host
ARG http_proxy_port

# Home directory definition
ENV HOME="/root"

# Update, upgrade and install necessary packages
RUN <<EOF
    apt-get update -y
    apt-get upgrade -y
    apt-get install -y gettext
EOF

# Copy sources in working directory
WORKDIR $HOME
ADD . .

# Create settings file to handle proxy configuration when necessary
RUN <<EOF
    mkdir $HOME/.m2
    if [ -n "$http_proxy_host" ] && [ -n "$http_proxy_port" ] && [ -n "$http_proxy_protocol" ]
    then
        envsubst < .m2-settings.xml > $HOME/.m2/settings.xml
    fi
    rm .m2-settings.xml
EOF

# Build
RUN mvn -Dmaven.test.skip=true clean package

#
# Package stage
#

FROM $REGISTRY/library/eclipse-temurin:17-jre-jammy

# User and group definition
ENV USER=voxatile
ENV UID=10001
ENV GROUP=voxatile
ENV GID=10001

# Create user and group
RUN <<EOF
    addgroup --gid ${GID} ${GROUP}
    adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    --ingroup "${GROUP}" \
    "${USER}"
EOF

# Pick results from builder
WORKDIR /app
COPY --from=builder /root/target/Generator.jar Generator.jar

# Run as user and group
USER ${USER}:${GROUP}

# Generator as entrypoint
ENTRYPOINT ["java", "-jar", "/app/Generator.jar"]
# Output directory default value
CMD ["/output"]
