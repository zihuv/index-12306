package com.zihuv.idempotent.core.factory;

import com.zihuv.base.context.ApplicationContextHolder;
import com.zihuv.idempotent.core.service.param.IdempotentParamService;
import com.zihuv.idempotent.core.service.spel.IdempotentSpELByMQExecuteHandler;
import com.zihuv.idempotent.core.service.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;

/**
 * 幂等执行处理器简单工厂
 */
public class IdempotentExecuteHandlerFactory {

    /**
     * 获取幂等执行处理器
     *
     * @param scene 指定幂等验证场景类型
     * @param type  指定幂等处理类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI -> {
                switch (type) {
                    case PARAM -> result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                    case SPEL -> result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                    default -> {
                    }
                }
            }
            // TODO 处理 MQ 幂等性
            case MQ -> result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
            default -> {
            }
        }
        return result;
    }

}