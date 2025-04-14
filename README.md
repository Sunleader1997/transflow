## Transflow
集成 reactor 类似于 logstash，插件化数据流转服务
### 功能特点：
- 插件化各节点（比方说增加 kafka输入输出）
- 拖拽式编辑数据流
- 数据批量处理 （比方 es 批量存储）
- 高吞吐，基于 reactor 实现高吞吐数据流，以下一个transData 承接一个数据的情况下，每秒大约1.7w条数据
- ![img.png](doc%2Fimg.png)
## 演示
![gif](doc%2F20250327183942.gif)
# 打包
* transflow-all 下 执行 mvn clean package, 最终成品在 transflow-app 的 target 下 .zip
* 注意，会打包前端资源以及plugin，plugin放在 /plugins下，也会被打进 zip
* 发布版本因为大小受限，只提供基础demo插件
# Linux 安装
```
unzip transflow-app-0.1.0-distribution.zip -d /
```
# Linux 启动
- 通过 systemctl 启动
```
systemctl start transflow
```
- 通过脚本启动
```
cd /opt/transflow
sh startup.sh start
```
- 手动启动
```
cd /opt/transflow
java -jar transflow-app-0.1.0.jar
```
# 访问页面
http://localhost:18987/#/mgmt/job