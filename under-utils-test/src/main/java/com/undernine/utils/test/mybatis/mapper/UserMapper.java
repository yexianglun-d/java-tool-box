package com.undernine.utils.test.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.undernine.utils.test.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author undernine
 * @since 2024-12-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
