package com.atguigu.k8sjava;

import com.atguigu.k8sjava.service.K8sService;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.PortForward;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@SpringBootTest
@Slf4j
class K8sJavaApplicationTests {
    @Autowired
    K8sService k8sService;

    @Test
    void contextLoads() {
    }

    @Test
    void listPods()
    {
        try
        {
            V1PodList v1PodList = k8sService.listNameSpacePods();
            for (int i = 0; i < v1PodList.getItems().size(); i++)
            {
                log.info("podName: {}", v1PodList.getItems().get(i).getMetadata().getName());
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPod()
    {
        try
        {
            k8sService.createPod("redis-test", "redis:latest", "default");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void LogPod()
    {
        try
        {
            InputStream stream = k8sService.getPodLogs("mysql-747h8", "default");
            IOUtils.copy(stream, System.out);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void execCommand()
    {
        try
        {
            InputStream stream = k8sService.execCommand("default", "mysql-747h8", new String[]{"sh", "-c", "ls"}, true, true);
            IOUtils.copy(stream, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void portForward()
    {
        try
        {
            PortForward.PortForwardResult portForwardResult = k8sService.portForward("default", "mysql-747h8", 8080, 3306);
            portForwardResult.init();
            portForwardResult.getInputStream(8080).transferTo(System.out);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        } catch (ApiException e)
        {
            throw new RuntimeException(e);
        }
    }

}
