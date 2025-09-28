**毕设：快递驿站系统（后端部分）**



# 基本思路

选题是复刻一个菜鸟驿站，但只能用自己的手机和电脑，所以一些东西可能得换种方式实现或模拟，例如扫描枪、驿站的机器、取件码标签打印等 ；

用户的主要使用流程是：在手机小程序上可以查看快递的预计送达时间（数据可能需要Mock），送达代收点后，可以看到取件码（例如3-2-1004），到了代收点，可以手机展示个人身份码条形码，对着驿站里的机器的摄像头（由于条件限制，用前端界面+电脑摄像头模拟）放入身份码和快递上的条形码，就可以出库； 

驿站工作人员的主要使用流程是：驿站人员的可以通过小程序扫描包裹上的快递单条形码，系统生成取件码（如3-2-1004），系统打印取件码标签，驿站人员将标签贴在包裹上并将包裹放在对应的货架上，系统通知用户包裹已入库； 

附加功能（暂不实现）：寄包裹（上门取件），上门送包裹等



# 服务拆分

| 服务 / 模块                                    | 职责                                                         |
| ---------------------------------------------- | ------------------------------------------------------------ |
| **Auth 服务**（认证 /发 token /校验 token）    | 负责用户登录（校验用户名＋密码／其他方式），生成 JWT，管理 token 生效／失效逻辑，提供一个验证接口（比如给其他服务校验 token） |
| **User 服务**（用户资料 +地址管理）            | 存用户基本资料（姓名、手机号等）、地址管理、可能还有角色权限信息（比如用户是不是驿站人员） |
| **Package / 驿站业务 服务**                    | 处理包裹／取件码／包裹状态（入库、出库、扫描、标签生成／打印等）逻辑 |
| **通知服务**（可以是微服务也可以只是一个模块） | 当包裹入库完毕／生成取件码时通知用户等                       |



# 项目部署

项目中所有的组件都通过 docker compose 部署，yaml文件在项目中准备好：`./resource/docker-compose/e-dove.yaml`；


## docker环境

>  实际上windows版docker也可以；如果用linux虚拟机，则wsl和vmware都可以；

### linux环境（可选）

组件部署需要准备docker环境，这里以`wsl`为例子；

在windows下安装wsl，默认为ubuntu系统：

```
wsl --install
```

### 安装docker

>  在wsl中安装docker

方式一：

​	按照 [wsl官网](https://learn.microsoft.com/zh-cn/windows/wsl/tutorials/wsl-containers)上的说明，安装Windows 的 Docker Desktop，并集成至wsl；

方式二：

​	按照[docker官网](https://docs.docker.com/desktop/setup/install/linux/ubuntu/)上的说明，直接在wsl中安装docker；

### 配置docker镜像源（可选）

参考 [DockerHub 国内加速镜像列表](https://github.com/dongyubin/DockerHub) 配置即可；

**tip**：由于镜像源不稳定且可能被下架，可以不配置镜像源，使用**代理**：

1. 确保linux虚拟机的网络模式是**桥接模式**（wsl默认）
2. 开启代理，启用**服务模式**，还需要开启**TUN模式**；



## 启动组件

将`e-dove.yaml`文件放入linux的`home`目录；

启动命令：

```
docker compose -f e-dove.yaml -p e-dove up -d
```

关闭命令：

```
docker compose -f e-dove.yaml -p e-dove down
```



## 配置组件

### 创建nacos命名空间

组件启动后，在本机访问 `linux地址:8848/nacos` 进入nacos控台，首次进入需要设置**初始密码**，设置为`nacos`；

> 即用户名`nacos`，密码`nacos`；如果不这样设置，需要不少地方的配置；

在"命名空间"栏创建命名空间，命名空间ID设置为`e-dove-1014`;

![](./resource/images/nacos命名空间.png)



### Redis配置

redis的**配置**已准备在项目中：`./resource/redis/redis.conf`；

将文件放入linux的`home`目录，并将文件移动到挂载配置文件的位置；

```
sudo mv ./redis.conf ./e-dove/redis/conf/redis.conf
```



### Seata配置

seata相关的**配置**已准备在项目中：`./resource/seata/applicayion.yml`

添加前，需要修改配置中的内容：

```yml
services:
  seata-server:
	...
    environment:
      - SEATA_IP=172.28.80.78  # 改成linux的ip地址，可用`ip a`命令查看
    ...
```

将文件放入linux的`home`目录，并将文件移动到挂载配置文件的位置；

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

1. 使用 `e-dove.yaml` 中的 `root` 用户登录MySQL；
2. 执行 `./resource/mysql` 目录下的所有sql语句；



## 环境变量

项目中一些地方需要使用环境变量做配置，需要配置**项目运行的系统**的环境变量（可以是windows，也可以是打包后运行在的linux或docker）

> 用引用环境变量的方式，就不用改那么多地方了；但是有些地方还是必须要修改，比如seata的配置等，没有办法；

### docker所在地址

使用：

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
E_DOVE_DOCKER_IP_ADDR = docker所在的主机的地址 （如果部署在本机，则可以配置成127.0.0.1）
```




# 系统设计

## 双token身份校验

jwt + refresh token + 黑名单

时序图：

```mermaid
sequenceDiagram
    autonumber
    participant User as 用户端
    participant Server as 服务端
    participant Redis as Redis 存储

    %% 登录流程
    User->>Server: login 请求（手机号 + 密码/验证码）
    Server-->>User: 返回 accessToken + refreshToken

    %% 普通业务请求流程
    loop 多次业务请求
        User->>Server: 携带 accessToken 的业务请求
        alt accessToken 验证成功
            Server-->>User: 返回业务数据（200 OK）
        else 验证失败（如 token 过期／无效）
            Server-->>User: 返回 401 Unauthorized
        end
    end

    %% refresh 流程
    User->>Server: 发起 refresh 请求（带 refreshToken）
    Server->>Server: 验证 refreshToken 签名＆格式＆过期合法性
    alt 验证失败
        Server-->>User: 返回 401 Unauthorized
    else 验证成功
        Server->>Redis: 检查黑名单中是否存在 jti
        alt 在黑名单中
            Server-->>User: 返回 401 Unauthorized
        else 不在黑名单
            Server->>Redis: 获取 REFRESH_TOKEN_PREFIX + userId 存储的 jti
            Redis-->>Server: 返回存储的 jti
            Server->>Server: 比对提交来的 jti 和 Redis 中的 jti
            alt 不一致
                Server-->>User: 返回 401 Unauthorized
            else 一致
                Server->>Server: 生成 新 accessToken
                Server->>Server: 生成 新 refreshToken（包含新的 jti）
                Server->>Redis: 更新 REFRESH_TOKEN_PREFIX + userId 的 jti 并设置过期时间
                Server-->>User: 返回 新的 accessToken + 新的 refreshToken
            end
        end
    end

```



## 服务间认证

OAuth 2.0



## 用户权限控制

RBAC



## 登录/注册

**注册**：必须使用手机号 + 验证码，可选择设置邮箱和密码；

**登录**：提供3种登录方式：

- 手机号 + 验证码（若用户不存在，则会自动注册）
- 手机号 + 密码
- 邮箱 + 密码

其他：

- 前端还需要人机验证码；
- 密码的处理采用：`Argon2id`密码哈希算法（在抗 GPU／ASIC 攻击方面比`bcrypt`更强）

> tip：`Argon /ˈɑːrɡən/ `, `bcrypt /biːˈkrɪpt/`

```mermaid
sequenceDiagram
  participant frontend as 前端系统
  participant backend as 服务系统
  participant Redis
  participant MySQL

  %% 登录流程
  Note over frontend,MySQL: 登录流程
  frontend ->> backend: 提交 登录请求（手机号/邮箱 + 密码 或 验证码等）
  backend ->> backend: 校验请求参数合法性

  alt 手机号 + 验证码 登录
    backend ->> Redis: 拉取该手机号对应的验证码
    Redis -->> backend: 返回验证码（或无数据）
    alt 验证码 不存在 / 失效 / 错误
      backend -->> frontend: 返回 “验证码错误 / 失效” 错误
    else 验证码 匹配正确
      backend ->> MySQL: 查询此手机号对应的用户记录
      MySQL -->> backend: 返回用户记录（可能为空）
      alt 用户记录为空（用户未注册）
        backend ->> MySQL: 新建用户记录（手机号为唯一标识）
        MySQL -->> backend: 返回新用户记录
        backend ->> MySQL: 为该用户赋予默认角色/权限
        MySQL -->> backend: 确认角色权限写入
      end
      backend ->> backend: 执行登录后续操作（生成 access token / refresh token 等）
    end

  else 手机号 + 密码 登录
    backend ->> MySQL: 查询该手机号对应的用户记录
    MySQL -->> backend: 返回用户记录（可能为空）
    alt 用户不存在
      backend -->> frontend: 返回 “用户未注册” 错误
    else 用户存在
      backend ->> backend: 校验请求中密码与用户记录中密码是否匹配
      alt 密码不匹配
        backend -->> frontend: 返回 “用户名或密码错误” 错误
      else 密码匹配
        backend ->> backend: 执行登录后续操作（生成 token 等）
      end
    end

  else 邮箱 + 密码 登录
    backend ->> MySQL: 查询该邮箱对应的用户记录
    MySQL -->> backend: 返回用户记录（可能为空）
    alt 用户不存在
      backend -->> frontend: 返回 “用户未注册” 错误
    else 用户存在
      backend ->> backend: 校验密码是否正确
      alt 密码错误
        backend -->> frontend: 返回 “用户名或密码错误” 错误
      else 密码正确
        backend ->> backend: 执行登录后续操作（生成 token 等）
      end
    end
  end

  alt 登录成功
    backend ->> MySQL: 查询用户角色 / 权限信息
    MySQL -->> backend: 返回角色/权限数据
    backend ->> Redis: 存储 refresh token 标识或会话状态（可过期设置）
    backend -->> frontend: 返回 登录结果（包含 access token、refresh token、用户信息、权限等）
  end

  %% 注册流程
  Note over frontend,MySQL: 注册流程
  frontend ->> backend: 提交 注册请求（手机号 / 邮箱 / 用户名 / 密码 / 验证码等）
  backend ->> backend: 校验请求参数合法性

  backend ->> MySQL: 检查手机号是否已被注册
  MySQL -->> backend: 返回结果
  alt 手机号已被使用
    backend -->> frontend: 返回 “手机号已注册” 错误
  else 手机号可用
    backend ->> MySQL: 检查邮箱是否已被注册
    MySQL -->> backend: 返回结果
    alt 邮箱已被使用
      backend -->> frontend: 返回 “邮箱已注册” 错误
    else 邮箱可用
      backend ->> MySQL: 检查用户名是否已被占用
      MySQL -->> backend: 返回结果
      alt 用户名已被占用
        backend -->> frontend: 返回 “用户名已存在” 错误
      else 用户名可用
        backend ->> Redis: 验证手机验证码合法性（与缓存中数据比对）
        Redis -->> backend: 返回验证码状态
        alt 验证码错误 / 失效
          backend -->> frontend: 返回 “验证码错误 / 失效” 错误
        else 验证码校验通过
          backend ->> Redis: 验证邮箱验证码合法性
          Redis -->> backend: 返回邮箱验证码状态
          alt 验证码错误 / 失效
            backend -->> frontend: 返回 “邮箱验证码错误 / 失效” 错误
          else 邮箱验证码也通过
            backend ->> backend: 对用户密码进行加密 / 安全处理
            backend ->> MySQL: 插入新用户记录（手机号 / 邮箱 / 用户名 / 密码等）
            MySQL -->> backend: 返回插入成功 / 用户记录
            backend ->> MySQL: 为用户赋予默认角色 / 权限
            MySQL -->> backend: 确认角色/权限写入
            backend ->> backend: （可选）注册后自动登录 → 生成 token 等
            backend ->> MySQL: 查询用户角色 / 权限信息
            MySQL -->> backend: 返回角色 / 权限
            backend ->> Redis: 存储 refresh token 标识或会话状态
            backend -->> frontend: 返回 注册成功 + 登录结果
          end
        end
      end
    end
  end

```



## 日志框架

选择`logback`，Spring自带



# 数据库表设计

为每个微服务创建一个单独的数据库和对应用户；

不添加**外键约束**，由应用层维护外键的映射关系；
