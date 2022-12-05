package com.jt.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jt.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
