package com.zihuv.userservice.service.handler.filter.user;

import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.userservice.model.enums.UserRegisterErrorCodeEnum;
import com.zihuv.userservice.model.param.UserRegisterParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class UserRegisterCheckNotNull implements UserRegisterCreateChainFilter<UserRegisterParam> {
    @Override
    public void handler(UserRegisterParam requestParam) {
        if (StrUtil.isEmpty(requestParam.getUsername())) {
            throw new ClientException(UserRegisterErrorCodeEnum.USER_NAME_NOTNULL);
        } else if (StrUtil.isEmpty(requestParam.getPassword())) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_NOTNULL);
        } else if (StrUtil.isEmpty(requestParam.getPhone())) {
            throw new ClientException(UserRegisterErrorCodeEnum.PHONE_NOTNULL);
        } else if (Objects.isNull(requestParam.getIdType())) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_TYPE_NOTNULL);
        } else if (StrUtil.isEmpty(requestParam.getIdCard())) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_NOTNULL);
        } else if (StrUtil.isEmpty(requestParam.getMail())) {
            throw new ClientException(UserRegisterErrorCodeEnum.MAIL_NOTNULL);
        } else if (StrUtil.isEmpty(requestParam.getRealName())) {
            throw new ClientException(UserRegisterErrorCodeEnum.REAL_NAME_NOTNULL);
        }

    }

    @Override
    public int getOrder() {
        return 1;
    }
}