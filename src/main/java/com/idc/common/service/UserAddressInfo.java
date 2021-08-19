package com.idc.common.service;

import com.idc.common.po.AddressPo;
import com.idc.common.po.VertxMessageReq;

/**
 * 描述：地址信息业务管理
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/19 ProjectName: vertxAndKafka
 */
public interface UserAddressInfo {

    /**
     * 获取住址
     *
     * @param name 用户名
     * @return 用户住址信息
     */
    AddressPo getAddress(VertxMessageReq name);

    /**
     * 修改联系地址
     *
     * @param addressPo 待修改的地址信息
     * @return 更新的地址信息
     */
    AddressPo updateAddress(AddressPo addressPo);

}
