## Transflow
集成 reactor 类似于 logstash，插件化数据流转服务
### 功能特点：
- 插件化各节点（比方说增加 kafka输入输出）
- 拖拽式编辑数据流
- 数据批量处理 （比方 es 批量存储）
## 演示
![gif](doc%2F20250327183942.gif)
# 打包
* transflow-all 下 执行 mvn clean package, 最终成品在 transflow-app 的 target 下 .zip
* 注意，会打包前端资源以及plugin，plugin放在 /plugins下，也会被打进 zip
```
根据 pom 包里的参数，插件默认放到 /plugins 里
```
* 解压后 在 /opt/tranflow 下启动
* java -jar 启动 [transflow-app-0.1.0.jar](transflow-app%2Ftarget%2Ftransflow-app-0.1.0.jar)
```
服务启动时会读取 /plugins 里的插件，可以动态加载 
```