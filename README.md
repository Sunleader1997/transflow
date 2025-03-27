## Transflow
集成 reactor 类似于 logstash，插件化数据流转服务
### 功能特点：
- 插件化各节点（比方说增加 kafka输入输出）
- 拖拽式编辑数据流
- 数据批量处理 （比方 es 批量存储）
## 演示
![gif](doc%2F20250327183942.gif)
# 启动方式
* transflow-api 执行 mvn clean install
* transflow-plugins 执行 mvn clean package
```
根据 pom 包里的参数，插件默认放到 /plugins 里
```
* transflow-app 执行 mvn clean package
* java -jar 启动 [transflow-app-0.1.0.jar](transflow-app%2Ftarget%2Ftransflow-app-0.1.0.jar)
```
服务启动时会读取 /plugins 里的插件，可以动态加载 但是还没实现
```

# 流程配置
* 0.1.0 版本仅提供yml配置，后期可以实现动态加载
* [application.yml](transflow-app%2Fsrc%2Fmain%2Fresources%2Fapplication.yml)