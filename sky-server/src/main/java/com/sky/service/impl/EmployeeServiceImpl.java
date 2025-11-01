package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 前端传来的登录信息
     * @return 返回
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 前端传来的员工信息
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号状态为正常状态
        employee.setStatus(StatusConstant.ENABLE);

        //设置初始密码，并进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录人创建id和修改人id（暂时写死为10）
        // TODO : 后续改为动态获取当前登录用户的id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //保存员工信息到数据库
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO 前端传来的分页查询参数
     * @return 返回
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        Page<Employee> page = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        // 构建查询条件
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        // 如果name不为空，则添加模糊查询条件
        String name = employeePageQueryDTO.getName();
        if(name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        // 按照更新时间降序排序
        queryWrapper.orderByDesc("update_time");

        // 执行分页查询
        employeeMapper.selectPage(page, queryWrapper);

        return new PageResult(page.getTotal(), page.getRecords());
    }

    /**
     * 启用或禁用员工账号
     *
     * @param status 员工状态
     * @param id     员工id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", status);

        employeeMapper.update(null, updateWrapper);
    }

}
