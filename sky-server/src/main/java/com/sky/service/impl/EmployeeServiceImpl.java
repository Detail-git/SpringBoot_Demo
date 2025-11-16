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
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
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
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
     * 新增员工业务方法
     *
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //使用对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账号的状态
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，初始默认密码123456
        employee.setPassword(PasswordConstant.DEFAULT_PASSWORD);
        //设置当前记录的创建时间和修改时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录创建人id和修改人id

        //employee.setCreateUser(BaseContext.getCurrentId());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }


    /**
     *员工分页查询
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0,10
        //使用PageHelper插件，只需在代码中简单调用PageHelper.startPage(pageNum, pageSize) （pageNum 是页码，pageSize 是每页记录数 ）
        // 就能自动为 SQL 查询添加分页逻辑，无需关注具体数据库分页语法细节，极大简化开发流程。
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 启用或禁用员工账号
     * @param id
     * @param status
     * @return
     */
    @Override
    public void UseOrBan(Long id, Integer status) {

        /**
         * 这种方式的缺点很明显：
         * 代码冗长：如果对象有很多属性，需要写大量 setXxx() 方法，代码显得臃肿。
         * 对象状态不连贯：创建对象时，employee 先被实例化（此时属性可能是默认值），再通过 set 方法逐个赋值。
         * 如果赋值过程中被其他线程访问，可能导致对象状态不一致（比如只赋值了 id，还没赋值 status，对象就被使用了）。
         * 可读性差：一堆 set 方法堆在一起，很难一眼看清对象的完整属性。
         */
//        Employee employee = new Employee();
//        employee.setStatus(status);
//        employee.setId(id);


        /**
         * 对比传统写法，Builder 模式的好处很突出：
         * 链式调用，代码简洁优雅：通过 .属性名(值) 的链式写法，一行代码就能完成对象构建，可读性极高（像 “搭积木” 一样清晰）。
         * 对象状态一致：只有调用 build() 方法时，才会真正创建 Employee 对象。在此之前，所有属性设置都在 Builder 内部进行，避免了对象状态不完整的问题。
         * 支持必填属性校验：可以在 Builder 的构造函数中强制传入必填属性（比如 id），确保对象创建时关键属性不会缺失（后面会举例）。
         * 兼容多版本属性扩展：如果后续给 Employee 增加新属性（比如 email），只需在 Builder 中添加 email(String email) 方法即可，无需修改原有代码（符合 “开闭原则”）。
         */
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     */
    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.select(id);
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);
    }
}
