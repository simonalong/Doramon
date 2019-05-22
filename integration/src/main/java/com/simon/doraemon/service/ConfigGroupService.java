package com.simon.doraemon.service;

import com.simon.neo.Neo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author robot
 */
@Service
public class ConfigGroupService extends BaseService{

    @Autowired
    private Neo shareShop;

    @Override
    protected Neo getNeo() {
        return shareShop;
    }

    @Override
    protected String getTableName() {
        return "t_config_group";
    }
}