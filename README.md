## 环境

```
JDK 8
MySQL >= 5.6
```

## 准备工作

```
修改 src\main\resources\application.yml 中的数据库配置
尽量不要修改端口如果要修改的话前端项目也需要修改

如果需要修改Netty端口需要去 src\main\java\com\im\netty\WssServer.java 中修改

在MySQL中创建叫做 im 的数据库
然后导入 im.sql
```

