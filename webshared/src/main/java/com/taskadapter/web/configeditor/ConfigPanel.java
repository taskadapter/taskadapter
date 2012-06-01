package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;

/**
 * Config panels must implement this interface to initialize config or to be initialized by config
 *
 * @Author: Alexander Kulik
 * @Date: 01.06.12 12:11
 */
public interface ConfigPanel {
    void setDataToConfig(ConnectorConfig config);
    void initDataByConfig(ConnectorConfig config);
}
