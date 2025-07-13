# 版本说明

ES <= 7.10.1 是免费使用的，后续版本需要授权后使用


# Install ES

Reqirements: 
- Docker >= 20.10
- Docker Compose >= 1.29

```yaml
services:
  elasticsearch:
    image: docker.io/bitnami/elasticsearch:8:18.0
    ports:
      - '9200:9200'
      - '9300:9300'
    volumes:
      - 'elasticsearch_data:/bitnami/elasticsearch/data'
volumes:
  elasticsearch_data:
    driver: local
```

# GUI
https://elasticvue.com/


# Install Plugins
https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html

## analysis-ik
https://github.com/infinilabs/analysis-ik
`bin/elasticsearch-plugin install https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-8.18.0.zip`

## analysis-pinyin
https://github.com/infinilabs/analysis-pinyin

`bin/elasticsearch-plugin install https://release.infinilabs.com/analysis-pinyin/stable/elasticsearch-analysis-pinyin-8.18.0.zip`

## analysis-stconvert
https://github.com/infinilabs/analysis-stconvert

`bin/elasticsearch-plugin install https://release.infinilabs.com/analysis-stconvert/stable/elasticsearch-analysis-stconvert-8.18.0.zip`
