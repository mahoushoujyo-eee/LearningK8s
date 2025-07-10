package com.atguigu.k8sjava;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.List;

public class Example
{
    public static void main(String[] args) throws IOException, ApiException
    {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        CoreV1Api.APIlistPodForAllNamespacesRequest request = api.listPodForAllNamespaces();

        List<V1Pod> items = request.execute().getItems();

        for (V1Pod item : items)
        {
            System.out.println(item.getMetadata().getName());
        }
    }
}
