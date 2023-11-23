package com.zhaizq.aio.config;

import org.apache.ibatis.type.EnumTypeHandler;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhaizq
 * @date 2021-10-15
 * <p>
 * 枚举默认转换失败时, 不抛出异常
 * <p>
 * 使用时需要在配置文件中指定默认枚举处理类
 * mybatis.configuration.default-enum-type-handler=com.hikvision.acloud.common.mybatis.DefaultEnumTypeHandler
 */
public class DefaultEnumTypeHandler<E extends Enum<E>> extends EnumTypeHandler<E> {
    public DefaultEnumTypeHandler(Class<E> type) {
        super(type);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return super.getNullableResult(rs, columnName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return super.getNullableResult(rs, columnIndex);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return super.getNullableResult(cs, columnIndex);
        } catch (Exception e) {
            return null;
        }
    }
}
