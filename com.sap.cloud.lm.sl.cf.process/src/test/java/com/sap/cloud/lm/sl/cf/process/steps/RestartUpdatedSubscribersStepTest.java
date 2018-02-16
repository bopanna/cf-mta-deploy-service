package com.sap.cloud.lm.sl.cf.process.steps;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.sap.cloud.lm.sl.cf.client.ClientExtensions;

public class RestartUpdatedSubscribersStepTest extends SyncActivitiStepTest<RestartUpdatedSubscribersStep> {

    @Test
    public void testClientsForCorrectSpacesAreRequested() throws Exception {
        // Given:
        List<CloudApplication> updatedSubscribers = new ArrayList<>();
        updatedSubscribers.add(createCloudApplication("app", createCloudSpace("org", "space-foo")));
        updatedSubscribers.add(createCloudApplication("app", createCloudSpace("org", "space-bar")));
        StepsUtil.setUpdatedSubscribers(context, updatedSubscribers);

        // When:
        step.execute(context);

        // Then:
        Mockito.verify(execution, Mockito.atLeastOnce()).getCloudFoundryClient(eq("org"), eq("space-foo"));
        Mockito.verify(execution, Mockito.atLeastOnce()).getCloudFoundryClient(eq("org"), eq("space-bar"));
    }

    @Test
    public void testSubscribersAreRestartedWhenClientExtensionsAreNotSupported() throws Exception {
        // Given:
        List<CloudApplication> updatedSubscribers = new ArrayList<>();
        updatedSubscribers.add(createCloudApplication("app-1", createCloudSpace("org", "space-foo")));
        updatedSubscribers.add(createCloudApplication("app-2", createCloudSpace("org", "space-bar")));
        StepsUtil.setUpdatedSubscribers(context, updatedSubscribers);

        CloudFoundryOperations clientForSpaceFoo = Mockito.mock(CloudFoundryOperations.class);
        CloudFoundryOperations clientForSpaceBar = Mockito.mock(CloudFoundryOperations.class);
        Mockito.when(execution.getCloudFoundryClient(eq("org"), eq("space-foo"))).thenReturn(clientForSpaceFoo);
        Mockito.when(execution.getCloudFoundryClient(eq("org"), eq("space-bar"))).thenReturn(clientForSpaceBar);

        // When:
        step.execute(context);

        // Then:
        assertStepFinishedSuccessfully();
        Mockito.verify(clientForSpaceFoo).stopApplication("app-1");
        Mockito.verify(clientForSpaceFoo).startApplication("app-1");
        Mockito.verify(clientForSpaceBar).stopApplication("app-2");
        Mockito.verify(clientForSpaceBar).startApplication("app-2");
    }

    @Test
    public void testSubscribersAreRestartedWhenClientExtensionsAreSupported() throws Exception {
        // Given:
        List<CloudApplication> updatedSubscribers = new ArrayList<>();
        updatedSubscribers.add(createCloudApplication("app-1", createCloudSpace("org", "space-foo")));
        updatedSubscribers.add(createCloudApplication("app-2", createCloudSpace("org", "space-bar")));
        StepsUtil.setUpdatedSubscribers(context, updatedSubscribers);

        CloudFoundryOperations clientForSpaceFoo = Mockito.mock(CloudFoundryOperations.class);
        CloudFoundryOperations clientForSpaceBar = Mockito.mock(CloudFoundryOperations.class);

        Mockito.when(execution.getCloudFoundryClient(eq("org"), eq("space-foo"))).thenReturn(clientForSpaceFoo);
        Mockito.when(execution.getCloudFoundryClient(eq("org"), eq("space-bar"))).thenReturn(clientForSpaceBar);

        ClientExtensions clientExtensionsForSpaceFoo = Mockito.mock(ClientExtensions.class);
        ClientExtensions clientExtensionsForSpaceBar = Mockito.mock(ClientExtensions.class);
        Mockito.when(execution.getClientExtensions(eq("org"), eq("space-foo"))).thenReturn(clientExtensionsForSpaceFoo);
        Mockito.when(execution.getClientExtensions(eq("org"), eq("space-bar"))).thenReturn(clientExtensionsForSpaceBar);

        // When:
        step.execute(context);

        // Then:
        assertStepFinishedSuccessfully();
        Mockito.verify(clientForSpaceFoo).stopApplication("app-1");
        Mockito.verify(clientExtensionsForSpaceFoo).startApplication("app-1", false);
        Mockito.verify(clientForSpaceBar).stopApplication("app-2");
        Mockito.verify(clientExtensionsForSpaceBar).startApplication("app-2", false);
    }

    @Test
    public void testNothingHappensWhenThereAreNoSubscribersToRestart() throws Exception {
        // Given:
        StepsUtil.setUpdatedSubscribers(context, Collections.emptyList());

        // When:
        step.execute(context);

        // Then:
        assertStepFinishedSuccessfully();
    }

    @Test
    public void testOtherSubscribersAreRestartedWhenOneRestartFails() throws Exception {
        // Given:
        List<CloudApplication> updatedSubscribers = new ArrayList<>();
        updatedSubscribers.add(createCloudApplication("app-1", createCloudSpace(ORG_NAME, SPACE_NAME)));
        updatedSubscribers.add(createCloudApplication("app-2", createCloudSpace(ORG_NAME, SPACE_NAME)));
        StepsUtil.setUpdatedSubscribers(context, updatedSubscribers);

        Mockito.doThrow(new CloudFoundryException(HttpStatus.INTERNAL_SERVER_ERROR)).when(client).stopApplication("app-1");
        Mockito.when(execution.getClientExtensions(anyString(), anyString())).thenReturn(clientExtensions);
        // When:
        step.execute(context);

        // Then:
        assertStepFinishedSuccessfully();
        Mockito.verify(client).stopApplication("app-1");
        Mockito.verify(client).stopApplication("app-2");
        Mockito.verify(clientExtensions).startApplication("app-2", false);
    }

    private CloudApplication createCloudApplication(String appName, CloudSpace space) {
        CloudApplication app = new CloudApplication(null, appName);
        app.setSpace(space);
        return app;
    }

    private CloudSpace createCloudSpace(String orgName, String spaceName) {
        CloudOrganization org = new CloudOrganization(null, orgName);
        return new CloudSpace(null, spaceName, org);
    }

    @Override
    protected RestartUpdatedSubscribersStep createStep() {
        return new RestartUpdatedSubscribersStep();
    }

}
