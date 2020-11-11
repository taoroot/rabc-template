# rbac-template


基于 Spring Boot + Vue 的 RBAC 后台模板 [查看演示](http://rbac.zhiyi.zone)

# HOST 配置
```
127.0.0.1 redis-server
127.0.0.1 sys-master-server
127.0.0.1 dev-server
```

# Mysql 配置

```sql
-- 创建数据库
CREATE DATABASE `rbac-template` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

-- 创建用户
CREATE USER `rbac-template`@`localhost` IDENTIFIED BY 'rbac-template';

-- 授权
GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create View, Delete, Drop, Event, Execute, 
Grant Option, Index, Insert, Lock Tables, References, Select, Show View, Trigger, Update ON `rbac-template`.* TO `rbac-template`@`%`;
```

# Redis 配置
```
docker run -d  --name redis-server -p 6379:6379 redis --requirepass "123456"
```