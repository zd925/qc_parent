package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {

    /**
     * 查询规范
     * @param categoryName
     * @return
     */
    @Select("SELECT name,options FROM tb_spec WHERE template_id IN " +
            "(SELECT template_id FROM tb_category WHERE name = #{categoryName}) " +
            "ORDER BY seq")
    public List<Map> findListByCategoryName(@Param("categoryName") String categoryName);

}
