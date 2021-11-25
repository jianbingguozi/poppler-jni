FROM alpine as compile

# 安装系统依赖
RUN sed -i "s/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g" /etc/apk/repositories \
    && apk add cmake make g++ openjdk8 poppler-dev

ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk

# 编译链接文件
RUN cmake CMakeLists.txt \
    && make

# 编译jar包
RUN mvn clean

FROM alpine as runner
RUN sed -i "s/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g" /etc/apk/repositories \
    && apk add openjdk8 poppler
ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
COPY --from= compile target/poppler-0.0.1-SNAPSHOT.jar .
