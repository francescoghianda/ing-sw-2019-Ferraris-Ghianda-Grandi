package it.polimi.se2019;

import it.polimi.se2019.utils.network.NetworkUtils;
import junit.framework.TestCase;
import org.junit.Test;

public class NetworkUtilsTest
{

    @Test
    public void correctMinPortTest()
    {
        TestCase.assertTrue(NetworkUtils.isValidPort(1024));
    }

    @Test
    public void incorrectMinPortTest()
    {
        TestCase.assertFalse(NetworkUtils.isValidPort(1023));
    }

    @Test
    public void correctMaxPortTest()
    {
        TestCase.assertTrue(NetworkUtils.isValidPort(65535));
    }

    @Test
    public void incorrectMaxPortTest()
    {
        TestCase.assertFalse(NetworkUtils.isValidPort(65536));
    }

    @Test
    public void correctIpTest()
    {
        TestCase.assertTrue(NetworkUtils.isIp("127.0.0.1"));
        TestCase.assertTrue(NetworkUtils.isIp("255.255.255.255"));
        TestCase.assertTrue(NetworkUtils.isIp("0.0.0.0"));
    }

    @Test
    public void incorrectIpTest()
    {
        TestCase.assertFalse(NetworkUtils.isIp("127.0.0.1.1"));
        TestCase.assertFalse(NetworkUtils.isIp("256.255.255.255"));
        TestCase.assertFalse(NetworkUtils.isIp("-1.0.0.0"));
        TestCase.assertFalse(NetworkUtils.isIp("0.0.0.a"));
    }
}
