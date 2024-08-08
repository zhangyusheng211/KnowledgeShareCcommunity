package com.moxi.mogublog.xo.test;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class CodeGeneral {

    /**
     * 代码生成    示例代码
     */
    @Test
    public void testGenerator() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/mogu_blog_business", "root", "mogu2018")
                .globalConfig(builder -> {
                    builder.author("陌溪") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.moxi.mogublog.xo") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\蘑菇社区\\CodeGen")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_message_push") // 设置需要生成的表名
                            .addTablePrefix("t_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}
