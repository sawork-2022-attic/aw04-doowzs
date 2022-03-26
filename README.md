# 实验报告

- 运行环境： Apple M1（ARM架构，16G内存），使用Docker运行POS服务器、Redis集群；
- 压力测试：模拟用户每隔1秒钟将一个商品添加到购物车，总共添加10个商品。


## 1. Docker镜像

使用spring boot的插件可以直接创建Docker镜像：

```shell
$ mvn spring-boot:build-image
```

但是这个指令在ARM架构的电脑上有问题，会编译出x86架构的镜像出来，导致运行非常缓慢。
所以我参考了[Stackoverflow上的这个提问](https://stackoverflow.com/questions/69526553/how-do-i-define-architecture-arm64-when-building-the-docker-image-through-maven)，
改成用自己写的Dockerfile生成镜像，直接运行打包的jar文件，容器就能以较为正常的效率运行了。

测试使用1个POS服务器（限制1CPU运行），分别模拟500个用户和2000个用户同时访问，结果如下：

![](./assets/gatling-1-500.png)

![](./assets/gatling-1-2000.png)

可以看出2000用户访问时，POS服务器压力大，返回消息的延迟非常大。

## 2. HAProxy横向扩展

使用4个POS服务器，并使用HAProxy镜像做负载均衡，采用轮询（roundrobin）方式平衡负载，运行相同的测试得到如下结果：

![](./assets/gatling-2-500.png)

![](./assets/gatling-2-2000.png)

从结果中可以看出，500用户时，有大概7%左右的请求的延迟增大，但总体来看性能很好，猜测这个差异可能是由于HAProxy的配置和额外开销导致的。
而2000用户同时访问时，延迟超过1200ms的请求数量从>95%降低到了70%左右，可以看出添加负载均衡提高了处理高密度请求的能力。
如果仔细地调整HAProxy的配置，应该可以获得更好的性能。

此外，在启动4个POS服务器时，出现了500错误，这个错误的原因应该是频繁地请求资源导致京东的服务器切断了链接。
改为手动依次启动4个服务器即可避免触发阈值。

> # WebPOS
>
> The demo shows a web POS system , which replaces the in-memory product db in aw03 with a one backed by 京东.
>
>
> ![](jdpos.png)
>
> To run
>
> ```shell
> mvn clean spring-boot:run
> ```
>
> Currently, it creates a new session for each user and the session data is stored in an in-memory h2 db. 
> And it also fetches a product list from jd.com every time a session begins.
>
> 1. Build a docker image for this application and performance a load testing against it.
> 2. Make this system horizontally scalable by using haproxy and performance a load testing against it.
> 3. Take care of the **cache missing** problem (you may cache the products from jd.com) and **session sharing** problem (you may use a standalone mysql db or a redis cluster). Performance load testings.
>
> Please **write a report** on the performance differences you notices among the above tasks.
