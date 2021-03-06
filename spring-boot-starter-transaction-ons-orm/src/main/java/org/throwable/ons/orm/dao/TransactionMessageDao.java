package org.throwable.ons.orm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.throwable.mapper.DogeMapper;
import org.throwable.ons.orm.entity.TransactionMessage;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/20 15:38
 */
@Mapper
public interface TransactionMessageDao extends DogeMapper<Long, TransactionMessage> {

}
