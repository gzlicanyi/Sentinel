package com.alibaba.csp.sentinel.adapter.diamond.datasource;

import com.alibaba.csp.courier.log.RecordLog;
import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.parser.ConfigParser;
import com.taobao.diamond.client.Diamond;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.ManagerListenerAdapter;


/**
 * @author leyou
 * @since 2017/11/22
 */
public class DiamondDataSource<T> extends AbstractDataSource<String, T> {

    private String dataId;
    private String group;
    private ManagerListener diamondListener;
    //private DynamicSentinelProperty<T> property;

    //@Override
    //public SentinelProperty<T> getProperty() {
    //	return property;
    //}

    public DiamondDataSource(String dataId, String group, final ConfigParser<String, T> configParser) {
        super(configParser);
        this.dataId = dataId;
        this.group = group;

        try {
            T value = loadConfig();
            getProperty().updateValue(value);
        } catch (Exception e) {
            RecordLog.info(e.getMessage(), e);
        }

        diamondListener = new ManagerListenerAdapter() {
            @Override
            public void receiveConfigInfo(String conf) {
                try {
                    RecordLog.info("receive conf ->" + conf);
                    T newValue = loadConfig(conf);
                    //property.updateValue(newValue);
                    getProperty().updateValue(newValue);
                } catch (Exception e) {
                    RecordLog.info(e.getMessage(), e);
                }
            }
        };

        Diamond.addListener(dataId, group, diamondListener);
    }

    @Override
    public String readSource() throws Exception {
        String conf = Diamond.getConfig(dataId, group, 5000);
        RecordLog.info("read conf ->" + conf);
        return conf;
    }

    @Override
    public void close() {
        Diamond.removeListener(dataId, group, diamondListener);
    }
}
