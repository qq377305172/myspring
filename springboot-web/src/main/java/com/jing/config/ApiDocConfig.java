package com.jing.config;//package com.example.demo.config;
//
//import com.apidoc.common.Const;
//import com.apidoc.service.ApiDocService;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import tk.mybatis.spring.annotation.MapperScan;
//
//import java.io.File;
//
///**
// * @author Admin
// * @title: ApiDocConfig
// * @projectName demo
// * @description: TODO
// * @date 2020/3/28 16:15
// */
//@Configuration
//@EnableTransactionManagement//启动事务
//@ComponentScan("com.apidoc")//扫描组件类
//@MapperScan("com.apidoc.dao")//扫描数据库操作层的类
//@EntityScan("com.apidoc.entity")//扫描实体类
//public class ApiDocConfig {
//    @Bean
//    public ApiDocService generator() {
//        //所有的常量都在Const类下，需要修改常量的在这里配置即可
//        //配置代码的绝对路径，方便扫描代码的注释，因为注释编译之后就被jvm剔除了，只能扫描源码，不配置则不扫描注释
//        //默认路径为{项目路径}+src/main/java
//        Const.codePath = Const.projectPath + "apidoc-demo" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
//        return new ApiDocService();
//    }
//}
