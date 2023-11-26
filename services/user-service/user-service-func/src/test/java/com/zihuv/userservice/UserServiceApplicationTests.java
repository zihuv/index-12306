package com.zihuv.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'.length()");  //使用parseExpression方法来创建一个表达式
        System.out.println(exp.getValue());
    }

}
