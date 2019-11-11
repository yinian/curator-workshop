package com.netflix.curator.framework;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
/**
 * @Auther: viagra
 * @Date: 2019/11/8 15:39
 * @Description: 创建连接的两种方式
 */
public class CreateClientExample {

    private static final String PATH = "/example/basic";


    public static void main(String[] args) throws Exception {
        String zkConnString = "127.0.0.1:2181";
        CuratorFramework client = null;

        try {
            client = createSimple(zkConnString);
            client.start();
            client.create().creatingParentsIfNeeded()
                    .forPath(PATH,"test".getBytes());
            CloseableUtils.closeQuietly(client);

            client = createWithOptions(zkConnString,
                    new ExponentialBackoffRetry(1000, 3), 1000, 1000);
            client.start();
            System.out.println(new String(client.getData().forPath(PATH)));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }


    }

    public static CuratorFramework createSimple(String connectionString) {

        // these are reasonable arguments for the ExponentialBackOffRetry.
        // The first retry will wait 1 second - the second will wait up to 2
        // seconds - the third will wait up to 4 seconds.

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000,3);

        // The simplest way to get a CuratorFramework instance. This will use default values.
        // The only required arguments are the connection string and the retry policy
        return CuratorFrameworkFactory.newClient(connectionString,retryPolicy);



    }

    public static CuratorFramework createWithOptions(String connectionString,
                                                     RetryPolicy retryPolicy, int connectionTimeoutMs,
                                                     int sessionTimeoutMs) {

        // using the CuratorFrameworkFactory.builder() gives fine grained control
        // over creation options. See the CuratorFrameworkFactory.Builder
        // javadoc details
        return CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(sessionTimeoutMs)
                // etc.etc.
                .build();
    }
}
