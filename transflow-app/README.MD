## 问题1 ： pf4j 需要自定义 Manager
```angular2html
默认的 AbstractPluginManager 只能加载单例 plugin
```
1. loadPlugin 负责加载 插件， 加载一次就可以
2. startPlugin 需要返回 plugin 实例，所有实例需要统一管理

## 问题2 ： 处理结束后如何 commit
```angular2html
已经通过reactor流程编排解决
```

## 问题3 ： 如何单例output
```angular2html
其实大部分场景下 output 都是可以复用的
比方说 kafka 的 producer client
如果每个job都需要新建一个client连接，那么就浪费了资源
```