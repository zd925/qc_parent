package com.qingcheng.service.goods;

import java.io.IOException;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-09    21:52
 */
/*
* elasticsearch
* */
public interface EsService {
    // 导入索引库
    public void importToEs() throws IOException;
}
