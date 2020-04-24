# linux主机批量执行命令的jar包
一般来说，我们的服务器集群机器比较多，如果没有elk那些日志服务框架的话，一台一台查看日志比较费劲，
所以，本人简单地用java实现了一个批量执行linux命令的小工具，主要用于查看日志用。
# 使用方法
> 1.打开项目，编译，mvn install 拷贝里面的jar包；
>2.运行命令：
```
java -jar linux-util-1.0-SNAPSHOT "cat log_strategy.log | grep 'abc'" "111.111.111,111.111.112" "root,root,22"
```
上面命令的意思：对用户/密码为root/root， ssh协议端口为22 ，远程主机111.111.111,111.111.112的集群执行cat log_strategy.log | grep 'abc'命令




