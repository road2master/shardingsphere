/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.backend.text.distsql.ral.common.show;

import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.metadata.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseRow;
import org.apache.shardingsphere.proxy.backend.text.distsql.ral.common.show.executor.ShowTransactionRuleExecutor;
import org.apache.shardingsphere.transaction.config.TransactionRuleConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class ShowTransactionRuleExecutorTest {
    
    private final ShowTransactionRuleExecutor executor = new ShowTransactionRuleExecutor();
    
    @Test
    public void assertExecutorWithXA() throws SQLException {
        ContextManager contextManager = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        when(contextManager.getMetaDataContexts().getGlobalRuleMetaData()).thenReturn(getGlobalRuleMetaData("XA", "Atomikos"));
        ProxyContext.getInstance().init(contextManager);
        executor.execute();
        executor.next();
        QueryResponseRow queryResponseRow = executor.getQueryResponseRow();
        ArrayList<Object> data = new ArrayList<>(queryResponseRow.getData());
        assertThat(data.size(), is(2));
        assertThat(data.get(0), is("XA"));
        assertThat(data.get(1), is("Atomikos"));
    }
    
    @Test
    public void assertExecutorWithLocal() throws SQLException {
        ContextManager contextManager = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        when(contextManager.getMetaDataContexts().getGlobalRuleMetaData()).thenReturn(getGlobalRuleMetaData("LOCAL", null));
        ProxyContext.getInstance().init(contextManager);
        executor.execute();
        executor.next();
        QueryResponseRow queryResponseRow = executor.getQueryResponseRow();
        ArrayList<Object> data = new ArrayList<>(queryResponseRow.getData());
        assertThat(data.size(), is(2));
        assertThat(data.get(0), is("LOCAL"));
        assertThat(data.get(1), is(""));
    }
    
    private ShardingSphereRuleMetaData getGlobalRuleMetaData(final String defaultType, final String providerType) {
        RuleConfiguration transactionRuleConfiguration = new TransactionRuleConfiguration(defaultType, providerType);
        return new ShardingSphereRuleMetaData(Collections.singleton(transactionRuleConfiguration), Collections.emptyList());
    }
}