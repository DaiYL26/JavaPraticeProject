package com.example.login.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@MapperScan("com.example.login.dao")
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            if (!tableName.equals("word")) {
                return tableName;
            }
            // 获取参数方法
            Map<String, Integer> paramMap = RequestDataHelper.getRequestData();
            System.out.println("tableName : --------" + tableName);
            if (paramMap == null || paramMap.size() == 0) {
                return tableName;
            }
            paramMap.forEach((k, v) -> System.err.println(k + "----" + v));
            Integer dictID = paramMap.get("dictID");

            if (dictID != null) {
                paramMap.remove("dictID");
                if (dictID == 1) {
                    return "cet4";
                } else if (dictID == 2) {
                    return "cet6";
                } else if (dictID == 3) {
                    return "gk";
                } else {
                    return tableName;
                }
            } else {
                return tableName;
            }

        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        // 3.4.3.2 作废该方式
        // dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
        return interceptor;
    }
}
