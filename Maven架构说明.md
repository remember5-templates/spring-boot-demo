# Spring Boot Demo 项目 Maven 架构说明

## 架构设计原则

本项目采用**模块化独立架构**，每个模块可以：
- 使用不同的JDK版本
- 使用不同的Spring Boot版本
- 独立配置依赖和插件
- 独立构建和部署

## 架构特点

### 1. 根项目配置 (pom.xml)
- **packaging**: `pom` - 多模块项目
- **依赖管理**: 统一管理常用工具类版本
- **插件管理**: 提供插件模板，不强制版本
- **仓库配置**: 统一配置阿里云镜像

### 2. 模块独立性
每个模块可以独立配置：
```xml
<properties>
    <!-- 模块独立配置JDK版本 -->
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    
    <!-- 模块独立配置Spring Boot版本 -->
    <spring-boot.version>2.7.18</spring-boot.version>
</properties>
```

### 3. 模块类型

#### 3.1 公共模块 (demo-common)
- **用途**: 提供公共工具类和通用功能
- **打包方式**: `jar`
- **依赖**: 继承根项目的依赖管理

#### 3.2 应用模块 (demo-web, demo-webflux等)
- **用途**: 独立的Spring Boot应用
- **打包方式**: `jar` (可执行jar)
- **特点**: 可以独立运行，使用不同的JDK和Spring Boot版本

#### 3.3 聚合模块 (demo-admin)
- **用途**: 包含多个子模块的聚合项目
- **打包方式**: `pom`
- **特点**: 可以包含多个相关的子模块

## 使用示例

### 创建Java 8模块
```xml
<properties>
    <java.version>8</java.version>
    <spring-boot.version>2.2.0.RELEASE</spring-boot.version>
</properties>
```

### 创建Java 17模块
```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>2.7.18</spring-boot.version>
</properties>
```

### 创建Java 21模块
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
</properties>
```

## 构建命令

### 构建所有模块
```bash
mvn clean install
```

### 构建特定模块
```bash
mvn clean install -pl demo-web
```

### 跳过测试
```bash
mvn clean install -DskipTests
```

### 使用特定JDK版本构建
```bash
JAVA_HOME=/path/to/jdk8 mvn clean install -pl demo-web
JAVA_HOME=/path/to/jdk17 mvn clean install -pl demo-webflux
```

## 最佳实践

1. **版本管理**: 每个模块在properties中明确声明使用的版本
2. **依赖继承**: 优先使用根项目的dependencyManagement
3. **插件配置**: 每个模块独立配置maven-compiler-plugin
4. **模块隔离**: 避免模块间的循环依赖
5. **文档维护**: 及时更新模块的README文档

## 注意事项

1. 确保IDE支持多JDK版本配置
2. 不同JDK版本的模块不要相互依赖
3. 公共模块应使用最低兼容的JDK版本
4. 定期更新依赖版本，保持安全性 