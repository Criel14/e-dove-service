package com.criel.edove.auth.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * mybatis plus 生成器：生成entity, mapper, service, service impl（不生成 controller）
 */
public class MybatisPlusGenerator {
    public static void main(String[] args) {

        // 管理员
        String root = "root";
        String password = "eDoveMysql1014";

        // 获取配置
        CurrentConfig config = getConfig();

        // 生成代码
        FastAutoGenerator.create(config.url, root, password)
                // 1. 全局配置
                .globalConfig(builder -> {
                    builder.author("Criel")        // 设置作者
                            .outputDir(System.getProperty("user.dir") + config.path + "/src/main/java"); // 输出目录
                })
                // 2. 包配置
                .packageConfig(builder -> {
                    builder.parent(config.packageName)    // 设置父包名
                            .entity("entity")         // 实体类包名
                            .mapper("mapper")         // Mapper 包名
                            .service("service")       // Service 包名
                            .serviceImpl("service.impl") // Service Impl 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    System.getProperty("user.dir") + config.path + "/src/main/resources/mapper")); // 设置mapper.xml文件路径
                })
                // 3. 策略配置
                .strategyConfig(builder -> {
                    // 设置需要生成的表名，可多个
                    builder.addInclude(config.tables)
                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok()             // 启用Lombok（默认生成@Getter/@Setter，可全局替换为@Data）
                            .enableTableFieldAnnotation() // 开启字段注解（@TableField）
                            // Service 策略配置
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 格式化Service接口文件名称，例如：UserService
                            .formatServiceImplFileName("%sServiceImpl") // 格式化Service实现类文件名称
                            // Controller 策略配置：禁用 Controller 生成
                            .controllerBuilder().disable();
                })
                .execute(); // 执行生成
    }

    /**
     * 获取当前配置
     */
    private static CurrentConfig getConfig() {
        // 数据库连接配置
        String userUrl = "jdbc:mysql://172.28.80.78:3306/e_dove_user?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String authUrl = "jdbc:mysql://172.28.80.78:3306/e_dove_auth?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String storeUrl = "jdbc:mysql://172.28.80.78:3306/e_dove_store?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String parcelUrl = "jdbc:mysql://172.28.80.78:3306/e_dove_parcel?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai";

        // 包路径
        String userPath = "/e-dove-user";
        String authPath = "/e-dove-auth";
        String storePath = "/e-dove-store";
        String parcelPath = "/e-dove-parcel";

        // 包名
        String userPackageName = "com.criel.edove.user";
        String authPackageName = "com.criel.edove.auth";
        String storePackageName = "com.criel.edove.store";
        String parcelPackageName = "com.criel.edove.parcel";

        // 表名
        String[] userTables = {"user", "user_address"};
        String[] authTables = {"user_auth", "role", "user_role", "permission", "role_permission"};
        String[] storeTables = {"store", "shelf", "shelf_layer"};
        String[] parcelTables = {"parcel"};

        // 当前配置
        // CurrentConfig config = new CurrentConfig(userUrl, userPath, userPackageName, userTables);
        // CurrentConfig config = new CurrentConfig(authUrl, authPath, authPackageName, authTables);
        // CurrentConfig config = new CurrentConfig(storeUrl, storePath, storePackageName, storeTables);
        CurrentConfig config = new CurrentConfig(parcelUrl, parcelPath, parcelPackageName, parcelTables);

        return config;
    }

    /**
     * 配置类
     */
    public static class CurrentConfig {

        public String url;

        public String path;

        public String packageName;

        public String[] tables;

        public CurrentConfig(String url, String path, String packageName, String[] tables) {
            this.url = url;
            this.path = path;
            this.packageName = packageName;
            this.tables = tables;
        }
    }
}