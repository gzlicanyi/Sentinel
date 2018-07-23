package com.alibaba.csp.sentinel.adapter.hsf;

import java.util.Arrays;
import java.util.Map;

import com.alibaba.csp.sentinel.adapter.hsf.provider.HelloService2Impl;
import com.alibaba.csp.sentinel.adapter.hsf.provider.HelloServiceImpl;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

import com.taobao.hsf.lightapi.ServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * see http://gitlab.alibaba-inc.com/middleware/hsf-lightapi/wikis/how-to-use
 *
 * @author leyou
 * @since 2018/7/11
 */
public class SentinelHsfTest {
    //作为一个Services的容器类，这个必须要放在【入口】类，并且要【static】，是为了不污染classloader，见谅。
    //默认下载推荐sar包
    //public static ServiceFactory factory = ServiceFactory.getInstance();
    //指定下载sar包路径
    public static ServiceFactory factory = ServiceFactory.getInstanceFromURL(
        "http://pandora-repos.oss-cn-hangzhou-c.aliyuncs.com/sar/2018-03-release/taobao-hsf.tgz");
    // change 2018-03-release according to http://ops.jm.taobao.org:9999/pandora-web/sar/sarList.html?version=&type=-1
    //使用sar包路径启动Pandora
    //public static ServiceFactory factory = ServiceFactory.getInstanceWithPath("/home/admin/sar/2018-03-release");

    @Before
    public void initServices() {
        factory.provider("helloProvider")//参数是一个标识，初始化后，下次只需调用provider("helloProvider")即可拿出对应服务
            .service("com.alibaba.csp.sentinel.adapter.hsf.HelloService")//服务名 & 接口全类名
            .version("1.0.0.23.daily")//版本号
            .group("light")//组别
            // .writeMode("unit",0) //设置单元化服务的writeMode,非unit服务第二个参数随意
            .impl(new HelloServiceImpl())//对应的服务实现
            .publish();//发布服务，至少要调用service()和version()才可以发布服务
        factory.provider("helloProvider2")
            .service("com.alibaba.csp.sentinel.adapter.hsf.HelloService2")
            .version("1.0.0.23.daily")//版本号
            .group("light")//组别
            .impl(new HelloService2Impl())
            .publish();

        factory.consumer("helloConsumer")//参数是一个标识，初始化后，下次只需调用consumer("helloConsumer")即可直接拿出对应服务
            .service("com.alibaba.csp.sentinel.adapter.hsf.HelloService")//服务名 &　接口全类名
            .version("1.0.0.23.daily")//版本号
            .group("light")//组别
            .subscribe();//消费服务并获得服务的接口，至少要调用service()和version()才可以消费服务
        // 这里调用subscribe()是为了提前订阅地址，也可以在真正使用的时候在调用，如下方的main。
        //推荐还是在初始化的时候调用，可以让地址更快速地推送，虽然丑了一点。
    }

    @Test
    public void clusterNodeTest() throws Exception {
        HelloService helloWorldService = (HelloService)factory.consumer("helloConsumer")
            .maxWaitTimeForCsAddress(5000)//不配置默认等待地址推送3s
            .subscribe();//用ID取出对应服务，subscribe()方法返回对应的接口
        System.out.println(helloWorldService.sayHello("lanshan1", 10, 1.23));
        Map<ResourceWrapper, ClusterNode> map = ClusterBuilderSlot.getClusterNodeMap();
        System.out.println(map.size());
        Assert.assertTrue(map.size() > 0);
        //Thread.sleep(1000*1000);
    }

    @Test
    public void methodBlockTest() throws Throwable {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource(
            "com.alibaba.csp.sentinel.adapter.hsf.HelloService:sayHello(java.lang.String,java.lang.Integer,double)");
        flowRule.setCount(1);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setLimitApp("default");
        FlowRuleManager.loadRules(Arrays.asList(flowRule));
        //Thread.sleep(1000 * 1000);

        HelloService helloWorldService = (HelloService)factory.consumer("helloConsumer")
            .maxWaitTimeForCsAddress(5000).subscribe();
        final int times = 100;
        try {
            for (int i = 0; i < times; i++) {
                helloWorldService.sayHello("lanshan1", 10, 1.23);
                Thread.sleep(30);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(BlockException.isBlockException(e));
            return;
        }
        Assert.assertTrue(false);
    }

    @Test
    public void interfaceBlockTest() throws Throwable {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("com.alibaba.csp.sentinel.adapter.hsf.HelloService");
        flowRule.setCount(10);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setLimitApp("default");
        FlowRuleManager.loadRules(Arrays.asList(flowRule));

        HelloService helloWorldService = (HelloService)factory.consumer("helloConsumer")
            .maxWaitTimeForCsAddress(5000).subscribe();
        final int times = 100;
        try {
            for (int i = 0; i < times; i++) {
                helloWorldService.sayHello("lanshan1", 10, 1.23);
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(BlockException.isBlockException(e));
            return;
        }
        Assert.assertTrue(false);
    }
}
