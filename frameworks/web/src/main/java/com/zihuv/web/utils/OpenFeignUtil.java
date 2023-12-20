package com.zihuv.web.utils;

import com.zihuv.convention.exception.ServiceException;
import com.zihuv.convention.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class OpenFeignUtil {

    public static Result<?> send(Supplier<?> supplier, String errorMessage) {
        try {
            return getResult(supplier, errorMessage);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error(errorMessage);
            throw e;
        }
    }

    public static <T> T send(Supplier<?> supplier, String errorMessage, Class<T> clazz) {
        try {
            Result<?> result = getResult(supplier, errorMessage);
            T t = clazz.isInstance(result.getData()) ? clazz.cast(result.getData()) : null;
            if (t == null) {
                throw new ServiceException("[OpenFeignUtil] Result.getData() 强制类型转换失败");
            }
            return t;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error(errorMessage);
            throw e;
        }
    }

    private static Result<?> getResult(Supplier<?> supplier, String errorMessage) {
        Result<?> result = new Result<>();
        Object object = supplier.get();
        if (object == null) {
            throw new ServiceException(errorMessage);
        }
        if (object instanceof Result<?> r) {
            if (!r.isSuccess()) {
                throw new ServiceException(errorMessage);
            }
            result = r;
        }
        return result;
    }
}