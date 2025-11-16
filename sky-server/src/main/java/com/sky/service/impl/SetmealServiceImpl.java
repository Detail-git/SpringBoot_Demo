package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;

import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.sky.constant.StatusConstant.ENABLE;

/**
 * 套餐业务实现
 */
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 动态条件查寻套餐数据
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     *根据套餐id查询菜品选项，用于添加套餐功能中选择菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        List<DishItemVO> dishItemVOList = setmealMapper.getDishItemById(id);
        return dishItemVOList;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //数据库只能修改setmeal(基本表)表有的字段，所以只能用setmeal(基本表)来接封装数据，至于为什么不一开始就用setmeal作为参数：
        //数据安全：通过 DTO/VO 屏蔽不需要暴露的字段（如数据库内部字段、敏感信息），防止篡改;
        //灵活性高：前端需求变化时（如新增一个展示字段），只需修改 DTO/VO，无需改动 Entity 和数据库表；
        //降低耦合：前端和后端通过 DTO/VO 交互，双方的字段变化互不影响（比如后端 Entity 加字段，只要 DTO 不变，前端无需修改）。
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal); //调用mapper更新主表

        //修改完了套餐基础数据后，还需要修改菜品和套餐的关系，此时根据接口文档需要的参数调用对应方法
        //修改逻辑：先删除后插入(为了保证数据安全需加入注解@Transactional(事务), 保证此处的方法要么全部生效，要么全部失效)

        //先删除原有的套餐-菜品关联（避免旧数据残留）
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        //批量插入新的套餐-菜品关联
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();//先从DTO获取新的菜品列表
        if(setmealDishes != null && ! setmealDishes.isEmpty()){
            //为每个关联对象设置套餐ID，确保关联正确
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            //批量插入新关联
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQureuy(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 执行查询（查询结果会被PageHelper自动分页）
        // 注意：这里查询的是Setmeal实体，需根据DTO的其他条件（如name、categoryId）过滤
        Page<Setmeal> setmealPage = setmealMapper.pageQuery(setmealPageQueryDTO);

        // 4. 将Setmeal转换为SetmealVO（补充前端需要的额外信息）
        List<SetmealVO> setmealVOList = setmealPage.getResult().            // 步骤1：获取分页中的原始数据列表
                stream().                                                   // 步骤2：将List转换为Stream流
                map(setmeal -> {                                            // 步骤3：对流中每个元素进行转换（Setmeal → SetmealVO）
            SetmealVO vo = new SetmealVO();
            // 复制基本属性（用BeanUtils.copyProperties）
            BeanUtils.copyProperties(setmeal, vo);
            // 例如：补充分类名称（Setmeal中只有categoryId，VO需要categoryName）
            Category category = categoryMapper.getById(setmeal.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
            return vo;
        }).collect(Collectors.toList());

        //封装分页结果（总条数 + 当前页VO列表）
        PageResult pageResult = new PageResult();
        pageResult.setTotal(setmealPage.getTotal()); // 总条数
        pageResult.setRecords(setmealVOList); // 当前页数据（VO列表）

        return pageResult;
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向套餐表插入一条数据
        setmealMapper.insert(setmeal);

        //获取insert语句生成的主键
        Long setmealId = setmeal.getId();

        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if (dishes != null && !dishes.isEmpty()){
            //遍历菜品列表，转换为SetmealDish实体并设定套餐id
            List<SetmealDish> setmealDishes = dishes.stream().map(dishDTO -> {
                SetmealDish setmealDish = new SetmealDish();
                BeanUtils.copyProperties(dishDTO, setmealDish);
                setmealDish.setSetmealId(setmealId);//绑定套餐id
                return setmealDish;
            }).collect(Collectors.toList());

            //批量插入关联数据
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithMeal(Long id) {
        //根据id查询套餐数据
        Setmeal mealById = setmealMapper.getMealById(id);

        //根据id查询菜品数据
        List<SetmealDish> dishById = setmealDishMapper.getDishById(id);

        //将查询到的数据整合封装到SetmealVO,最后返回数据即可
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(mealById, setmealVO);
        setmealVO.setSetmealDishes(dishById);

        return setmealVO;
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteSetmeal(List<Long> ids) {
        //逻辑：根据id查询需要删除的套餐数据
        //还要删除setmeal_dish表存储的关联数据
        //判断套餐是否在售，在售要提示不能删
        for (Long id : ids) {
            Setmeal setmeal =  setmealMapper.getSetmealById(id);
            if (setmeal.getStatus() != null && setmeal.getStatus() == ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealMapper.deleteSetmeal(ids);
        setmealDishMapper.deleteSetmealsById(ids);
    }

    /**
     * 起售或者禁售套餐
     * @param id
     */
    @Override
    public void UserOrBan(Integer status, Long id) {
        //逻辑：根据套餐id查询数据库中套餐的售卖状态，是0则变为1，1则变为0
        // TODO: 后续需要修复自动填充 updateTime 和 updateUser 自动填充的问题，还有修改套餐功能也是（原因：当前方法参数为 Long id，切面无法反射调用 setter）

        //sql处做了状态反转，status参数在这里没啥用，也不需要再做多余的逻辑判断
            setmealMapper.UserOrBan(id);
    }

}
