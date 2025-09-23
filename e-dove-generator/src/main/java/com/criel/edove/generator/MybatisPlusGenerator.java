package com.criel.edove.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * mybatis plus 生成器：生成entity, mapper, service, service impl（不生成 controller）
 */
public class MybatisPlusGenerator {
    public static void main(String[] args) {

        // 数据库连接配置
        String url = "jdbc:mysql://172.28.80.78:3306/e_dove_user?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "eDoveMysql1014";

        // 生成代码
        FastAutoGenerator.create(url, username, password)
                // 1. 全局配置
                .globalConfig(builder -> {
                    builder.author("Criel")        // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/e-dove-user/src/main/java"); // 输出目录
                })
                // 2. 包配置
                .packageConfig(builder -> {
                    builder.parent("com.criel.edove.user")    // 设置父包名
                            .entity("entity")         // 实体类包名
                            .mapper("mapper")         // Mapper 包名
                            .service("service")       // Service 包名
                            .serviceImpl("service.impl") // Service Impl 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    System.getProperty("user.dir") + "/e-dove-user/src/main/resources/mapper")); // 设置mapper.xml文件路径
                })
                // 3. 策略配置
                .strategyConfig(builder -> {
                    // 设置需要生成的表名，可多个
                     builder.addInclude("user", "role", "user_role", "permission", "role_permission", "user_address")
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
}