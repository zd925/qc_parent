package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdMapper adMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveAdToRedisByPosition(String position) {
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("position", position);
        criteria.andLessThanOrEqualTo("startTime", new Date());// 开始时间小于等于当前时间
        criteria.andGreaterThanOrEqualTo("endTime", new Date());// 结束时间大于等于当前时间
        criteria.andEqualTo("status", "1");// 当前状态
        List<Ad> adsList = adMapper.selectByExample(example);
        redisTemplate.boundHashOps(CacheKey.AD).put(position, adsList);
    }

    private List<String> getPositionList() {
        ArrayList<String> poistionList = new ArrayList<>();
        List<Ad> ads = adMapper.selectAll();
        for (Ad ad : ads) {
            poistionList.add(ad.getPosition());
        }
        return poistionList;
    }

    @Override
    public void saveAllToRedis() {
        for (String position : getPositionList()) {
            saveAdToRedisByPosition(position);
        }
    }

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Ad> findAll() {
        return adMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Ad> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Ad> ads = (Page<Ad>) adMapper.selectAll();
        return new PageResult<Ad>(ads.getTotal(), ads.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Ad> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Ad> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Ad> ads = (Page<Ad>) adMapper.selectByExample(example);
        return new PageResult<Ad>(ads.getTotal(), ads.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Ad findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param ad
     */
    public void add(Ad ad) {
        adMapper.insert(ad);
        saveAdToRedisByPosition(ad.getPosition());
    }

    /**
     * 修改
     *
     * @param ad
     */
    public void update(Ad ad) {
        // 保存之前数据库中的值提取出来position
        String position = adMapper.selectByPrimaryKey(ad.getId()).getPosition();
       // 修改数据库的值
        adMapper.updateByPrimaryKeySelective(ad);
        saveAdToRedisByPosition(position);
        // 判断position的值是否相等
        if (position.equals(ad.getPosition())){
            saveAdToRedisByPosition(ad.getPosition());// 重新加载原来的position
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        String position = adMapper.selectByPrimaryKey(id).getPosition();
        saveAdToRedisByPosition(position);// 重新加载缓存
        adMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据广告位置查询列表
     *
     * @param position
     * @return
     */
    @Override
    public List<Ad> findByposition(String position) {
      /*  Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("position", position);// 位置
        criteria.andLessThanOrEqualTo("startTime", new Date());// 开始时间小于当前时间
        criteria.andGreaterThanOrEqualTo("endTime", new Date());// 截至时间大于当前时间
        criteria.andEqualTo("status", 1);// 状态有效
        List<Ad> ads = adMapper.selectByExample(example);
        return ads;*/
        // 从缓存中读取
        return (List<Ad>) redisTemplate.boundHashOps(CacheKey.AD).get(position);
    }


    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 广告名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 广告位置
            if (searchMap.get("position") != null && !"".equals(searchMap.get("position"))) {
                criteria.andLike("position", "%" + searchMap.get("position") + "%");
            }
            // 状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }
            // 图片地址
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // URL
            if (searchMap.get("url") != null && !"".equals(searchMap.get("url"))) {
                criteria.andLike("url", "%" + searchMap.get("url") + "%");
            }
            // 备注
            if (searchMap.get("remarks") != null && !"".equals(searchMap.get("remarks"))) {
                criteria.andLike("remarks", "%" + searchMap.get("remarks") + "%");
            }

            // ID
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }

        }
        return example;
    }

}
