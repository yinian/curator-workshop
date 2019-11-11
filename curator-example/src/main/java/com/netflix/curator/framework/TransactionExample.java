package com.netflix.curator.framework;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;

import java.util.Collection;
import java.util.List;

/**
 * @Auther: viagra
 * @Date: 2019/11/8 17:06
 * @Description: 支持事务操作
 * https://colobu.com/2014/12/16/zookeeper-recipes-by-example-8/#%E4%BA%8B%E5%8A%A1
 */
public class TransactionExample {



    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer();
        CuratorFramework client = null;
        try {
            client = createSimple(server.getConnectString());
            client.start();

            ZKPaths.mkdirs(client.getZookeeperClient().getZooKeeper(),"/a");
            ZKPaths.mkdirs(client.getZookeeperClient().getZooKeeper(),"/another/path");
            ZKPaths.mkdirs(client.getZookeeperClient().getZooKeeper(),"/yet/another/path");


            transaction(client);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
            CloseableUtils.closeQuietly(server);
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

    public static Collection<CuratorTransactionResult> transaction(CuratorFramework client) throws Exception {

        // this example shows how to use Zookeeper's new transactions


        //		Collection<CuratorTransactionResult> results = client.inTransaction().create().forPath("/a/path", "some data".getBytes())
//				.and().setData().forPath("/another/path", "other data".getBytes())
//				.and().delete().forPath("/yet/another/path")
//				.and().commit(); // IMPORTANT!

        //inTransaction is deprecated. use transaction() instead
        List<CuratorTransactionResult>  results = client.transaction().forOperations(
                client.transactionOp().create().forPath("/a/path", "some data".getBytes()),
                client.transactionOp().setData().forPath("/another/path", "other data".getBytes()),
                client.transactionOp().delete().forPath("/yet/another/path"));

        // called
        for (CuratorTransactionResult result : results){
            System.out.println(result.getForPath() + " - " + result.getType());

        }
        return results;

    }


}
