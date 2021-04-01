package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface CategoryService {

    public List<Category> queryAllRootLevelCat();

    public  List<CategoryVO> getSubCatList(Integer rootCatId);

    //查询首页下的信息，通过id查询六条商品信息
    public List getSixNewItemsLazy(Integer rootCarId);


}
