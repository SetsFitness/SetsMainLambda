package test.java;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.ServerSocket;

import main.java.testing.TestHelper;

/**
 * The JUnit Class Rule for initializing the DynamoDB Local instance. Every integration test must
 * include this in order to use the Local DynamoDB instance.
 */
public class LocalDynamoDBCreationRule extends ExternalResource {
    private DynamoDBProxyServer server;
    private String port;

    public LocalDynamoDBCreationRule() {
        // This one should be copied during test-compile time. If project's basedir does not contains a folder
        // named 'native-libs' please try '$ mvn clean install' from command line first
        TestHelper.setIfTesting(true);
        System.setProperty("sqlite4java.library.path", "native-libs");
    }

    @Override
    protected void before() throws Exception {
        port = getAvailablePort();
        server = ServerRunner.createServerFromCommandLineArgs(
                new String[]{"-inMemory", "-port", port});
        server.start();
        TestHelper.setPort(port);
    }

    @Override
    protected void after() {
        // This makes the tests stop being able to access the server after the first rule run???
//        this.stopUnchecked(server);
    }

    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getAvailablePort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return String.valueOf(serverSocket.getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException("Available port was not found", e);
        }
    }

    public String getPort() {
        return port;
    }
}
