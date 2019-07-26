package com.qingcheng.controller.business;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuSearchService;
import com.qingcheng.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SkuSearchController {

    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public String search(Model model, @RequestParam Map<String, String> searchMap) throws Exception {
        // 解决乱码的问题调用webutil方法
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);

        // 判断是否存在searchMap中pageNo
        if (searchMap.get("pageNo") == null) {
            searchMap.put("pageNo", "1");
        }

        // 排序
        if (searchMap.get("sort") == null) {
            searchMap.put("sort", "");
        }
        if (searchMap.get("sortOrder") == null) {
            searchMap.put("sortOrder", "DESC");
        }

        // 远程调用接口
        Map result = skuSearchService.search(searchMap);
        model.addAttribute("result", result);

        //通过search 的数据来渲染模板  search.html
        //url 当前访问的url地址  localhost:9102/search.do?  http://localhost:9102/search.do?keywords=华为&category=华为
        //url处理
        StringBuffer url = new StringBuffer("/search.do?");
        for (String key : searchMap.keySet()) {
            url.append("&" + key + "=" + searchMap.get(key));
        }
        model.addAttribute("url", url);

        model.addAttribute("searchMap", searchMap);

        //页码
        int pageNo = Integer.parseInt(searchMap.get("pageNo"));
        model.addAttribute("pageNo", pageNo);

        Long totalPages = (Long) result.get("totalPages"); // 得到总页数
        int startPage = 1;// 开始页码
        int endPage = totalPages.intValue(); // 截至代码
        if (totalPages > 5) {
            startPage = pageNo - 2;
            if (startPage < 1) {
                startPage = 1;
            }
            endPage = startPage + 4;
            if (endPage > totalPages) {
                endPage = totalPages.intValue();
            }
        }


        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "search";
    }


}
