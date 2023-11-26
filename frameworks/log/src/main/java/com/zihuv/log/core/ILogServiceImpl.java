package com.zihuv.log.core;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.log.annotation.ILog;
import com.zihuv.log.mapper.ILogMapper;
import com.zihuv.log.pojo.ILogDTO;
import com.zihuv.convention.enums.OperateTypeEnum;
import com.zihuv.convention.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ILogServiceImpl extends ServiceImpl<ILogMapper, ILogDTO> implements ILogService{

    @Override
    public void logAsync(ProceedingJoinPoint joinPoint, ILog log, ILogDTO logDTO, Object result) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String module = log.module();
        String name = log.name();
        int status = 0;
        OperateTypeEnum type = log.type();

        if (StrUtil.isEmpty(log.module())) {
            Tag tag = method.getDeclaringClass().getAnnotation(Tag.class);
            if (tag != null) {
                module = tag.name();
            }
        }
        if (StrUtil.isEmpty(log.name())) {
            Operation operation = method.getAnnotation(Operation.class);
            if (operation != null) {
                name = operation.summary();
            }
        }
        if (result instanceof Result<?> r) {
            if (200 == r.getCode()) {
                status = 1;
            }
        }

        logDTO.setModule(module);
        logDTO.setName(name);
        logDTO.setType(type.getType());
        logDTO.setParameter(this.getParameter(method, joinPoint.getArgs()));
        logDTO.setResult(result);
        logDTO.setStatus(status);

        this.save(logDTO);
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            // 将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StrUtil.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }
}