
spring-boot-demo-elasticsearch


主要演示了 Spring Boot 如何集成 spring-boot-starter-data-elasticsearch 完成对 ElasticSearch 6.x 的高级使用技巧，包括创建索引、配置映射、删除索引、增删改查基本操作、复杂查询、高级查询、聚合查询等。

## 使用docker安装ES和elasticsearch-hq


作者编写本demo时，ElasticSearch版本为 6.8.6，使用 docker 运行，下面是所有步骤：

1. 下载镜像：`docker pull elasticsearch:6.8.6`
2. 运行容器：`docker run -d -p 9200:9200 -p 9300:9300 --name elasticsearch-6.8.6 elasticsearch:6.8.6`
3. 进入容器：`docker exec -it elasticsearch-6.8.6 /bin/bash`
4. 安装 ik 分词器：`./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.8.6/elasticsearch-analysis-ik-6.8.6.zip`
5. 安装拼音插件`./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.8.6/elasticsearch-analysis-pinyin-6.8.6.zip`
6. 修改 es 配置文件：`vi ./config/elasticsearch.yml`

```
cluster.name: "docker-cluster"
network.host: 0.0.0.0

# minimum_master_nodes need to be explicitly set when bound on a public IP
# set to 1 to allow single node clusters
# Details: https://github.com/elastic/elasticsearch/pull/17288
discovery.zen.minimum_master_nodes: 1

# just for elasticsearch-head plugin
http.cors.enabled: true
http.cors.allow-origin: "*"
```

7. 退出容器：`exit`
8. 停止容器：`docker stop elasticsearch-6.8.6`
9. 启动容器：`docker start elasticsearch-6.8.6`
10. 拉取elasticsearch-hq：  `docker pull elastichq/elasticsearch-hq`
11. 安装elasticsearch-hq：`docker run -d -p 5000:5000 elastichq/elasticsearch-hq`
12. 打开浏览器输入`http://localhost:5000` 地址处输入ES的地址即可 不要使用localhost或者127.0.0.1 请使用ipv4地址或者ipv6地址。windows打开cmd输入ipconfig即可查看，linux/mac终端输入ifconfig即可查看






## 添加依赖
