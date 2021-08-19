package com.idc.common.service.impl;

import com.idc.common.annotation.VertxUrl;
import com.idc.common.po.AddressPo;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.service.UserAddressInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 描述：地址信息维护
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/19 ProjectName: vertxAndKafka
 */
@Service
@VertxUrl(interfaceName = "userAddressInfo")
public class UserAddressInfoImpl implements UserAddressInfo {
    private static Logger logger = LoggerFactory.getLogger(SayHelloImpl.class);

    /**
     * 获取住址
     *
     * @param params 用户名
     * @return 用户住址信息
     */
    @Override
    public AddressPo getAddress(VertxMessageReq params) {
        String content = (String) params.getContent();
        logger.info("获取地址信息，请求参数:{}", content);
        AddressPo addressPo = new AddressPo();
        addressPo.setName("zidan.lian");
        addressPo.setAddress("上海市浦东新区杨高南路759号陆家嘴世纪金融广场2号楼16楼");
        addressPo.setUserId("0790");
        addressPo.setTel("13127933306");
        return addressPo;
    }

    /**
     * 修改联系地址
     *
     * @param params 待修改的地址信息
     * @return 更新的地址信息
     */
    @Override
    public AddressPo updateAddress(VertxMessageReq params) {
        AddressPo addressPo = (AddressPo) params.getContent();
        addressPo.setAddress("上海市浦东信息陆家嘴环路未来资产大厦32F");
        return addressPo;
    }
}
