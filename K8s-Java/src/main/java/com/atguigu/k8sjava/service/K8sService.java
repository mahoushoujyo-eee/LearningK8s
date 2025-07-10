package com.atguigu.k8sjava.service;

import io.kubernetes.client.Exec;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.PortForward;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.V1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
public class K8sService
{
    @Autowired
    private CoreV1Api coreV1Api;

    public void createPod(String podName, String imageName, String namespace) throws Exception
    {
        V1Pod v1Pod = new V1Pod();

        // 配置 Pod 元数据
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(podName); // 设置 Pod 名称
        v1Pod.setMetadata(metadata);

        // 配置 Pod 规格
        V1PodSpec podSpec = new V1PodSpec();

        // 配置容器
        V1Container container = new V1Container();
        container.setName(podName); // 设置容器名称
        container.setImage(imageName); // 设置容器镜像
        container.addPortsItem(new V1ContainerPort().containerPort(80)); // 设置容器端口

        // 将容器添加到 Pod 规格中
        podSpec.addContainersItem(container);

        // 配置 RuntimeClass
        podSpec.setRuntimeClassName("my-runtime-class");

        // 将 Pod 规格设置到 Pod 对象中
        v1Pod.setSpec(podSpec);

        V1Pod aDefault = coreV1Api.createNamespacedPod(namespace, v1Pod).execute();
        log.info("创建 Pod: {}", aDefault.getMetadata().getName());
    }

    public V1PodList listNameSpacePods() throws ApiException
    {
        V1PodList podList = coreV1Api.listNamespacedPod("default").execute();

        return podList;
    }

    public InputStream getPodLogs(String podName, String nameSpace) throws ApiException, IOException
    {
        PodLogs podLogs = new PodLogs();

        V1PodList list = coreV1Api.listNamespacedPod(nameSpace).execute();

        V1Pod v1Pod = list.getItems().stream().filter(pod -> pod.getMetadata().getName().equals(podName)).findFirst().get();

        return podLogs.streamNamespacedPodLog(v1Pod);
    }

    public InputStream execCommand(String nameSpace, String podName, String[] command, boolean stdin, boolean tty)
    {
        Exec exec = new Exec();
        Process process;

        try {
            process = exec.exec(nameSpace, podName, command, stdin, tty);
            return process.getInputStream();
        }
        catch (ApiException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    //暂时还不清楚怎么测试
    public PortForward.PortForwardResult portForward(String nameSpace, String podName, int localPort, int remotePort) throws IOException, ApiException
    {
        PortForward portForward = new PortForward();

        final PortForward.PortForwardResult result = portForward.forward(nameSpace, podName, Arrays.asList(localPort, remotePort));

        return result;
    }

}
