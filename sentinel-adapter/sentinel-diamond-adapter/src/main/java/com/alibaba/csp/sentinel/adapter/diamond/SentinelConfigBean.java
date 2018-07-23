package com.alibaba.csp.sentinel.adapter.diamond;

import java.util.List;

import com.alibaba.csp.courier.config.CourierConfig;
import com.alibaba.csp.courier.log.RecordLog;
import com.alibaba.csp.sentinel.adapter.diamond.datasource.DiamondDataSource;
import com.alibaba.csp.sentinel.datasource.DataSource;
import com.alibaba.csp.sentinel.datasource.parser.ConfigParser;
import com.alibaba.csp.sentinel.datasource.parser.JSONAuthorityRuleListParser;
import com.alibaba.csp.sentinel.datasource.parser.JSONDegradeRuleListParser;
import com.alibaba.csp.sentinel.datasource.parser.JSONFlowRuleListParser;
import com.alibaba.csp.sentinel.datasource.parser.JSONSystemRuleListParser;
import com.alibaba.csp.sentinel.datasource.parser.JsonIntegerParser;
import com.alibaba.csp.sentinel.node.IntervalProperty;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;

/*
 * this bean should be intializie
 */
public class SentinelConfigBean {
	final static String DIAMOND_GROUP = "asp-sentinel";
	final static String DEGRADE_SUFFIX = "-degrade";
	final static String AURHTORITY_SUFFIX = "-authority";
	final static String FLOW_SUFFIX = "-flow";
	final static String SYSTEM_SUFFIX = "-system";
	final static String FLOW_INTERVAL = "-flowinterval";


	
	public static void init() {

		try {
			final String appName = CourierConfig.getAppName();
			if (appName == null) {
				RecordLog.info("appName is null, can not init Sentinel Manager");
				return;
			}

			// 规则文件配置文件获取
			ConfigParser<String, List<AuthorityRule>> degradeParser = new JSONAuthorityRuleListParser();
			DataSource<String, List<AuthorityRule>> authorityDS = new DiamondDataSource<List<AuthorityRule>>(
					appName + AURHTORITY_SUFFIX, DIAMOND_GROUP, degradeParser);

			AuthorityRuleManager.register2Property(authorityDS.getProperty());

			DataSource<String, List<DegradeRule>> degradeDS = new DiamondDataSource<List<DegradeRule>>(
					appName + DEGRADE_SUFFIX, DIAMOND_GROUP, new JSONDegradeRuleListParser());
			DegradeRuleManager.register2Property(degradeDS.getProperty());

			DataSource<String, List<FlowRule>> flowRuleDS = new DiamondDataSource<List<FlowRule>>(appName + FLOW_SUFFIX,
					DIAMOND_GROUP, new JSONFlowRuleListParser());
			FlowRuleManager.register2Property(flowRuleDS.getProperty());

			DataSource<String, List<SystemRule>> systemRuleDS = new DiamondDataSource<List<SystemRule>>(
					appName + SYSTEM_SUFFIX, DIAMOND_GROUP, new JSONSystemRuleListParser());
			SystemRuleManager.register2Property(systemRuleDS.getProperty());

			DataSource<String, Integer> flowInterVel = new DiamondDataSource<Integer>(appName + FLOW_INTERVAL,
					DIAMOND_GROUP, new JsonIntegerParser());
			IntervalProperty.init(flowInterVel.getProperty());

		} catch (Throwable e) {
			RecordLog.info("sentinel init bean got error, ", e);
		}
	}
}
