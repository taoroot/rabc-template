# 打包vue
cd ui/admin || exit
npm run build:prod
cd - || exit
# 打包java
mvn clean package -Dmaven.test.skip=true


# 测试服务器环境配置
user=ubuntu # 用户名
appName=rbac-template
path=/home/${user}/${appName} # 根目录
appPort=7300 # spring boot 启动端口
port=8022 # ssh端口

# 创建远程加目录
ssh -p ${port} ${user}@test-server mkdir -p ${path}
# 上传代码
scp -P ${port} -C -p target/*.jar ${user}@test-server:${path}/${appName}.jar
# 上传数据库
scp -P ${port} -C -p db.sql ${user}@test-server:${path}/init.sql
# 上传执行脚本
scp -P ${port} -C -p run.sh ${user}@test-server:${path}/run.sh
# 执行脚本
ssh -p ${port} ${user}@test-server bash ${path}/run.sh restart ${appName} ${appPort}

