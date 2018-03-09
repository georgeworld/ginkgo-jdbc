# Ginkgo-JDBC
![image](https://raw.githubusercontent.com/georgeworld/georgeworld.github.com/master/ginkgo/jdbc/img/GinkgoJDBC-logo.png)<br>  

&nbsp;&nbsp;&nbsp;&nbsp;Ginkgo-JDBC Framework,是[老乔工作室](http://www.georgeinfo.com)在自己私有闭源的Georgeinfo-JDBC(乔治JDBC框架)的基础上，去掉私人闭源依赖库，开源出来的一款精简JDBC框架，【Ginkgo】是银杏树的意思，银杏树是一种生命力很强盛的植物，我们希望我们开源出来的一系列框架，可以像银杏树一样生命力强，所以我们的很多开源框架，都是以Ginkgo来作为命名前缀。<br>
&nbsp;&nbsp;&nbsp;&nbsp;2013年3月,Ginkgo JDBC Framework发布第一个内部版本，设计定稿后，基本功能没有经过大的重构，实现的功能基本满足当初设计的目标：***精简、高效、依赖少***。<br>
&nbsp;&nbsp;&nbsp;&nbsp;经过在多个项目中的实际应用（POS支付通道、风控系统、路由系统等金融交易类各个子系统），不断改进，框架已经很稳定。<br>
&nbsp;&nbsp;&nbsp;&nbsp;这个框架一开始就是[老乔](http://www.georgeinfo.com)利用私人时间，为自己所开发的一个框架，一开始只应用于自己的呼叫中心项目，后来逐步提供给其他项目使用。现在开源出来，希望有更多的人使用，更多的人参与改进，以便把这个框架做得更好。<br>  
&nbsp;&nbsp;&nbsp;&nbsp;Ginkgo JDBC 框架，适用于那些不想依赖大批第三方类库的场景，比如不想依赖一堆Spring类库，只想为自己的项目提供一个干净的JDBC操作功能，而且不需要各种花哨的JDBC操作，只想要基本的增删改查，分页，实体映射功能，此时，您就可以选择使用Ginkgo JDBC Framework.<br>
 
# 运行演示
&nbsp;&nbsp;&nbsp;&nbsp;演示代码在项目中的位置是：{src/test/java}/com/georgeinfo/test/MainTest.java，运行之前，先修改数据库连接文件：{src/test/resources}/resources/jdbc.properties文件中的数据库连接参数，然后在你自己的数据库上创建测试表如下：
> CREATE TABLE `user_info` (
    `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `name` varchar(45) DEFAULT NULL COMMENT '姓名',
    `creation_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`user_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='用户表';

![image](https://raw.githubusercontent.com/georgeworld/georgeworld.github.com/master/ginkgo/jdbc/img/src-code.png)<br>  
执行结果如下：<br>
![image](https://raw.githubusercontent.com/georgeworld/georgeworld.github.com/master/ginkgo/jdbc/img/code.png)<br>  
多次执行以后，数据库中的数据如下：<br>
![image](https://raw.githubusercontent.com/georgeworld/georgeworld.github.com/master/ginkgo/jdbc/img/data-in-db.png)<br>  

# 参与及讨论
  &nbsp;&nbsp;&nbsp;&nbsp;欢迎加入《互联网软件之家》QQ群：[693203950](//shang.qq.com/wpa/qunwpa?idkey=61c4589ea5618ae46d063f94cbd9394de290dd39ef46fca059a4309b8c1d7874)<br>  
  ![image](https://raw.githubusercontent.com/georgeworld/georgeworld.github.com/master/gstudio/res/img/qq_group.png) <br> 
  &nbsp;&nbsp;&nbsp;&nbsp;有问题，可以到[这里](https://github.com/georgeworld/ginkgo-jdbc/issues)来反馈，欢迎您的参与。
