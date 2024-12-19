项目实现设计定时任务从一个数据API中获取数据，存MySQL表后通过rabbitmq发送消息，接收到消息后再从数据库获取数据并送给前端


config:配置类
controller：控制层
entity：实体类
interceptor：拦截器
mapper：mapper层
service：service层
task：定时任务层
vo：vo数据层
main：spring boot启动方法
