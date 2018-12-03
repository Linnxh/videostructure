//package com.sensing.core.swagger;
//
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import springfox.documentation.builders.ParameterBuilder;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.schema.ModelRef;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.Parameter;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//
//@EnableSwagger2
//@EnableWebMvc
//@Configurable
//public class SwaggerDocumentationConfig extends WebMvcConfigurationSupport {
//
//    @Bean
//    public Docket productApi() {
//        //添加head参数start
//        ParameterBuilder tokenPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        tokenPar.name("uuid").description("用户uuid").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
//        pars.add(tokenPar.build());
//        //添加head参数end
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select().apis(RequestHandlerSelectors.basePackage("com.sensing.core.controller"))
//                .build()
//                .globalOperationParameters(pars)
//                .apiInfo(apiInfo());
//    }
//
//
//    private ApiInfo apiInfo() {
//        return new ApiInfo(
//                "人车接口化接口文档",
//                "人车接口化接口文档",
//                "1.0",
//                "http://community.sensing.com",
//                new Contact("master", "community.sensing.com", "XXX@XXX.com"),
//                "License of API", "API license URL", Collections.emptyList());
//    }
//
//}
