# AIO(all in one )

集成了全部的功能


# 准备

~~IDEA请安装插件：`envfile`~~ 新版IDEA支持`.env`文件

启动前，请准备好`postgres`数据库、`redis`，

修改配置文件`.env`


# 注意事项
Spring Boot 中配置文件变量引用：
1. `${KEY}`：直接引用环境变量或系统属性，如果不存在或为空，会抛出异常。
2. `${KEY:-default_value}`：检查环境变量是否存在或为空，如果不存在或为空，则使用默认值。适合大多数场景。
3. `${KEY:default_value}`：仅检查环境变量是否存在，如果不存在则使用默认值；如果存在但为空，则使用空字符串。适合特定场景。

