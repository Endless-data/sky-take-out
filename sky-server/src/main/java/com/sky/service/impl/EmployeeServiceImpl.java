package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录人创建id和修改人id（暂时写死为10）
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

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
        // select * from employee limit ?, ? where name like '%xx%'
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page =  employeeMapper.pageQuery(employeePageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用或禁用员工账号
     *
     * @param status 员工状态
     * @param id     员工id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // update employee set status = ? where id = ?

        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id 员工id
     * @return 返回
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);

        // 为了安全起见，不返回密码字段
        employee.setPassword("****");
        return employee;
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO 前端传来的员工信息
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置当前记录的修改时间
//        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录的修改人id
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //根据id修改员工信息到数据库
        employeeMapper.update(employee);
    }

}
