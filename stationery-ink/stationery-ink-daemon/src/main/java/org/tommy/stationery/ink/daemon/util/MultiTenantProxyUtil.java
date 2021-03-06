package org.tommy.stationery.ink.daemon.util;

import net.hydromatic.linq4j.Linq4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tommy.stationery.ink.core.util.LinqQuery;
import org.tommy.stationery.ink.daemon.config.ParametersConfig;
import org.tommy.stationery.ink.domain.BaseStatement;
import org.tommy.stationery.ink.domain.SqlResults;
import org.tommy.stationery.ink.domain.cluster.Tenant;
import org.tommy.stationery.ink.util.serde.JsonSerde;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kun7788 on 15. 3. 12..
 */
@Component
public class MultiTenantProxyUtil {

    private JsonSerde jsonSerde = new JsonSerde();

    @Autowired
    ParametersConfig parametersConfig;

    public Tenant getOtherTenantInfo(List<BaseStatement> statements) {

        List<BaseStatement> useStatements = Linq4j.asEnumerable(statements).where(LinqQuery.USE_STATEMENT_GROUP_FILTER).toList();
        if (useStatements != null && useStatements.size() >= 1) {
            BaseStatement selectedUseStatement = useStatements.get(useStatements.size() - 1);
            Tenant tenant = new Tenant();
            String inkDaemonName = selectedUseStatement.getUseDef().getName();
            tenant.setName(inkDaemonName);

            String myInkDeamenName = parametersConfig.StringValueOf(ParametersConfig.DAEMON_ID);
            List<Map<String, String>> tenantInfos = parametersConfig.ListValueOf(ParametersConfig.MULTI_TENANTS);
            if (tenantInfos == null || tenantInfos.size() <= 0) {return null;}
            for (Map<String, String> tenantInfo : tenantInfos) {
                if (inkDaemonName.equals(tenantInfo.get(ParametersConfig.TENANT_NAME)) && !myInkDeamenName.equals(tenantInfo.get(ParametersConfig.TENANT_NAME))) {
                    tenant.setUrl(tenantInfo.get(ParametersConfig.TENANT_URL));
                    return tenant;
                }
            }
            return null;
        }
        return null;
    }



    public SqlResults proxyCall(String url, String user, String password, String sessionId, String sql) throws IOException {
        String result = "succeed";
        StringBuilder sb = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        try {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(ParametersConfig.SESSION_ID, sessionId));
            nameValuePairs.add(new BasicNameValuePair(ParametersConfig.USER, user));
            nameValuePairs.add(new BasicNameValuePair(ParametersConfig.PASSWORD, password));
            nameValuePairs.add(new BasicNameValuePair(ParametersConfig.SQL, sql.replace("\n", "")));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            }
            post.abort();

            result = sb.toString();

        } catch (Exception e) {
            result = e.getMessage();
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }

        return (SqlResults)jsonSerde.deserialize(result, new TypeReference<SqlResults>() {});
    }
}
