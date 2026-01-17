# 项目部署

项目中所有的组件都通过 docker compose 部署，yaml文件在项目中已准备好：[e-dove.yaml](https://github.com/Criel14/e-dove-service/blob/master/resource/docker-compose/e-dove.yaml)


## docker环境

>  实际上windows版docker也可以；如果用linux虚拟机，则wsl和vmware都可以，下面的内容以wsl为例；

### linux环境(wsl)

在windows下安装wsl，默认为ubuntu系统：

```
wsl --install
```

### 安装docker

>  在wsl中安装docker

方式一：按照 [wsl官网](https://learn.microsoft.com/zh-cn/windows/wsl/tutorials/wsl-containers) 上的说明，安装Windows 的 Docker Desktop，并集成至wsl；

方式二：按照 [docker官网](https://docs.docker.com/desktop/setup/install/linux/ubuntu/) 上的说明，直接在wsl中安装docker；

### 配置docker镜像源（可选）

参考 [DockerHub 国内加速镜像列表](https://github.com/dongyubin/DockerHub) 配置即可；

**tip**：由于镜像源不稳定且可能被下架，可以不配置镜像源，使用**代理**：

1. 确保linux虚拟机的网络模式是**桥接模式**（wsl默认）
2. 开启代理，启用**服务模式**，还需要开启**TUN模式**；



## 启动组件

将`e-dove.yaml`文件放入linux的`home`目录；

> 任意有权限的目录都可以，后续的命令都在这个目录执行；

启动命令：

```
docker compose -f e-dove.yaml -p e-dove up -d
```

关闭命令：

```
docker compose -f e-dove.yaml -p e-dove down
```

> 第一次启动需要安装镜像，需要等待一段时间



## 配置组件

配置完成后的目录结构如下：

```
home
├── e-dove.yaml
└── e-dove
    ├── mysql
    │   └── conf
    │       └── ...（mysql配置文件，仅保留目录，项目里暂无配置）
    ├── nacos-standalone-logs
    │   └── ...（nacos日志文件，docker挂载后生成在此处）
    ├── redis
    │   ├── conf
    │       └── redis.conf（redis配置文件）
    │   └── logs（redis日志，docker挂载后生成在此处）
    └── seata
        ├── conf
        │   └── application.yml（seata配置文件）
        └── jdbc
            └──mysql-connector-j-8.4.0.jar（jdbc依赖）
```



### 创建nacos命名空间

组件启动后，在本机访问 `linux地址:8848/nacos` 进入nacos控台，首次进入需要设置**初始密码**，设置为：用户名`nacos`，密码`nacos`；

在"**命名空间**"栏创建命名空间，命名空间ID设置为`e-dove-1014`，如下图所示：

![](..//images/nacos命名空间.png)



### Redis配置

redis的**配置**已准备在项目中：[redis.conf](https://github.com/Criel14/e-dove-service/blob/master/resource/redis/redis.conf)

将文件放入linux的`home`目录，并将文件移动到挂载配置文件的位置；

```
sudo mv ./redis.conf ./e-dove/redis/conf/redis.conf
```



### Seata配置

seata相关的**配置**已准备在项目中：[application.yml](https://github.com/Criel14/e-dove-service/blob/master/resource/seata/application.yml)

添加前，需要修改 [e-dove.yaml](https://github.com/Criel14/e-dove-service/blob/master/resource/docker-compose/e-dove.yaml#L73) 中的内容：

```yml
services:
  seata-server:
	...
    environment:
      - SEATA_IP=172.28.80.78  # 改成linux的ip地址，可用`ip a`命令查看
    ...
```

将`application.yml`放入linux的`home`目录，并将文件移动到挂载配置文件的位置；

```
sudo mv application.yml ./e-dove/seata/conf/
```

除此之外，seata镜像本身不包含**jdbc**，需要自己准备，已准备在项目中：`./resource/seata/mysql-connector-j-8.4.0.jar`

将文件放入linux的`home`目录，并将文件移动到挂载配置文件的位置；

```
sudo mv mysql-connector-j-8.4.0.jar ./e-dove/seata/jdbc/
```



### 初始化MySQL

docker compose 启动完成后：

1. 使用 [e-dove.yaml](https://github.com/Criel14/e-dove-service/blob/master/resource/docker-compose/e-dove.yaml#L42) 中的 `root` 用户登录MySQL；

```yaml
services:
  mysql:
    environment:
      - MYSQL_ROOT_PASSWORD=eDoveMysql1014 # 管理员密码
    ...
```

2. 执行 [./resource/mysql](https://github.com/Criel14/e-dove-service/tree/master/resource/mysql) 目录下的所有sql语句；



## 环境变量

项目中一些地方需要使用环境变量做配置，需要配置**项目运行的系统**的环境变量（可以是windows，也可以是打包后运行在的linux或docker）

> 在配置里引用环境变量，部署就不用改那么多地方；
>
> 但是有些地方还是必须要修改，比如seata的配置等，没有办法；

### docker所在地址

项目中的使用示例：

```yaml
spring:
  data:
    redis:
      host: ${E_DOVE_DOCKER_IP_ADDR}
```

查看linux虚拟机的IP地址：

```
ip a
```

找到**eth0**的IP地址：

```
criel@CrielLaptop:~$ ip a
1: lo: ... # 省略
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 1000
    link/ether 00:15:5d:29:21:16 brd ff:ff:ff:ff:ff:ff
    inet 172.28.80.78/20 brd 172.28.95.255 scope global eth0  # <---- IP地址为此处的172.28.80.78
       valid_lft forever preferred_lft forever
    inet6 fe80::215:5dff:fe29:2116/64 scope link
       valid_lft forever preferred_lft forever
3: ... # 省略
```

将IP地址保存在环境变量中：

```
E_DOVE_DOCKER_IP_ADDR=你的docker所在的主机的地址 （如果部署在本机，则可以配置成127.0.0.1）
```



### 大模型的 API key

项目中用到了大模型，选择的是DeepSeek，前往 [deepseek开放平台](https://platform.deepseek.com/api_keys) 获取你的 API key；

项目中的使用示例：

```yaml
# 大模型配置，这里用deepseek
langchain4j:
  open-ai:
    chat-model:
      api-key: ${DEEPSEEK_API_KEY}
      base-url: https://api.deepseek.com
      model-name: deepseek-chat
      log-requests: true
      log-responses: true
```

将 API key 保存在环境变量中：

```
DEEPSEEK_API_KEY=你的API key
```

> 如果想用别的模型，则上面的配置信息也需要相应的修改