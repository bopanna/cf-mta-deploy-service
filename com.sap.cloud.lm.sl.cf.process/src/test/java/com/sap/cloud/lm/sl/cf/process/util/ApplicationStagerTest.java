package com.sap.cloud.lm.sl.cf.process.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.cloudfoundry.client.lib.CloudControllerClient;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudBuild;
import org.cloudfoundry.client.lib.domain.CloudBuild.DropletInfo;
import org.cloudfoundry.client.lib.domain.ImmutableCloudApplication;
import org.cloudfoundry.client.lib.domain.ImmutableCloudBuild;
import org.cloudfoundry.client.lib.domain.ImmutableCloudBuild.ImmutableDropletInfo;
import org.cloudfoundry.client.lib.domain.ImmutableCloudMetadata;
import org.cloudfoundry.client.lib.domain.ImmutableUploadToken;
import org.cloudfoundry.client.lib.domain.PackageState;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.process.Messages;
import com.sap.cloud.lm.sl.cf.process.steps.ExecutionWrapper;
import com.sap.cloud.lm.sl.cf.process.steps.StepPhase;
import com.sap.cloud.lm.sl.common.util.JsonUtil;

public class ApplicationStagerTest {

    private static final UUID BUILD_GUID = UUID.fromString("8e4da443-f255-499c-8b47-b3729b5b7432");
    private static final UUID DROPLET_GUID = UUID.fromString("9e4da443-f255-499c-8b47-b3729b5b7439");
    private static final UUID APP_GUID = UUID.fromString("1e4da443-f255-499c-8b47-b3729b5b7431");
    private static final UUID PACKAGE_GUID = UUID.fromString("2e4da443-f255-499c-8b47-b3729b5b7432");
    private static final String APP_NAME = "anatz";

    private ApplicationStager applicationStager;
    private CloudControllerClient client;
    private ExecutionWrapper execution;
    private DelegateExecution context;
    private StepLogger stepLogger;

    @BeforeEach
    public void setUp() {
        client = Mockito.mock(CloudControllerClient.class);
        execution = Mockito.mock(ExecutionWrapper.class);
        context = Mockito.mock(DelegateExecution.class);
        applicationStager = new ApplicationStager(client);
        Mockito.when(context.getVariable(Constants.VAR_BUILD_GUID))
               .thenReturn(BUILD_GUID);
        Mockito.when(execution.getContext())
               .thenReturn(context);
        Mockito.when(execution.getControllerClient())
               .thenReturn(client);
        stepLogger = Mockito.mock(StepLogger.class);
        setUploadTokenVariable();
    }

    @Test
    public void testBuildStateFailed() {
        CloudBuild build = ImmutableCloudBuild.builder()
                                              .state(CloudBuild.State.FAILED)
                                              .error("Error occurred while creating a build!")
                                              .build();
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenReturn(build);
        StagingState stagingState = applicationStager.getStagingState(execution.getContext());
        assertEquals(PackageState.FAILED, stagingState.getState());
        assertEquals("Error occurred while creating a build!", stagingState.getError());
    }

    @Test
    public void testBuildStateNotFoundAppNotFound() {
        ImmutableCloudApplication application = ImmutableCloudApplication.builder()
                                                                         .name(APP_NAME)
                                                                         .build();
        Mockito.when(context.getVariable(Constants.VAR_APP_TO_PROCESS))
               .thenReturn(JsonUtil.toJson(application));
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenThrow(new CloudOperationException(HttpStatus.NOT_FOUND));
        Mockito.when(client.getApplication(APP_NAME))
               .thenThrow(new CloudOperationException(HttpStatus.NOT_FOUND));
        try {
            applicationStager.getStagingState(execution.getContext());
            fail("Staging should fail!");
        } catch (CloudOperationException e) {
            Mockito.verify(client)
                   .getBuild(BUILD_GUID);
            Mockito.verify(client)
                   .getApplication(APP_NAME);
        }
    }

    @Test
    public void testBuildStateNotFoundAppFound() {
        ImmutableCloudApplication application = ImmutableCloudApplication.builder()
                                                                         .name(APP_NAME)
                                                                         .build();
        Mockito.when(context.getVariable(Constants.VAR_APP_TO_PROCESS))
               .thenReturn(JsonUtil.toJson(application));
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenThrow(new CloudOperationException(HttpStatus.NOT_FOUND));
        Mockito.when(client.getApplication(APP_NAME))
               .thenReturn(application);
        try {
            applicationStager.getStagingState(execution.getContext());
            fail("Staging should fail!");
        } catch (CloudOperationException e) {
            Mockito.verify(client, Mockito.times(1))
                   .getBuild(BUILD_GUID);
            Mockito.verify(client, Mockito.times(1))
                   .getApplication(APP_NAME);
        }
    }

    @Test
    public void testBuildStateStaged() {
        CloudBuild build = ImmutableCloudBuild.builder()
                                              .state(CloudBuild.State.STAGED)
                                              .build();
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenReturn(build);
        StagingState stagingState = applicationStager.getStagingState(execution.getContext());
        assertEquals(PackageState.STAGED, stagingState.getState());
        assertNull(stagingState.getError());
    }

    @Test
    public void testBuildStateStaging() {
        CloudBuild build = ImmutableCloudBuild.builder()
                                              .state(CloudBuild.State.STAGING)
                                              .build();
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenReturn(build);
        StagingState stagingState = applicationStager.getStagingState(execution.getContext());
        assertEquals(PackageState.PENDING, stagingState.getState());
        assertNull(stagingState.getError());
    }

    @Test
    public void testIsApplicationStagedCorrectlyMetadataIsNull() {
        CloudApplication app = createApplication();
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Collections.singletonList(Mockito.mock(CloudBuild.class)));
        Assertions.assertFalse(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testIsApplicationStagedCorrectlyNoLastBuild() {
        CloudApplication app = createApplication();
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Collections.emptyList());
        Assertions.assertFalse(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testIsApplicationStagedCorrectlyValidBuild() {
        CloudApplication app = createApplication();
        CloudBuild build = createBuild(CloudBuild.State.STAGED, Mockito.mock(DropletInfo.class), null);
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Collections.singletonList(build));
        Assertions.assertTrue(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testIsApplicationStagedCorrectlyBuildStagedFailed() {
        CloudApplication app = createApplication();
        CloudBuild build = createBuild(CloudBuild.State.FAILED, null, null);
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Collections.singletonList(build));
        Assertions.assertFalse(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testIsApplicationStagedCorrectlyDropletInfoIsNull() {
        CloudApplication app = createApplication();
        CloudBuild build = createBuild(CloudBuild.State.STAGED, null, null);
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Collections.singletonList(build));
        Assertions.assertFalse(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testIsApplicationStagedCorrectlyBuildErrorNotNull() {
        CloudApplication app = createApplication();
        ImmutableDropletInfo dropletInfo = ImmutableDropletInfo.of(DROPLET_GUID);
        CloudBuild build1 = createBuild(CloudBuild.State.STAGED, dropletInfo, null, new Date(0));
        CloudBuild build2 = createBuild(CloudBuild.State.FAILED, dropletInfo, "error", new Date(1));
        Mockito.when(client.getBuildsForApplication(any(UUID.class)))
               .thenReturn(Arrays.asList(build1, build2));
        Assertions.assertFalse(applicationStager.isApplicationStagedCorrectly(stepLogger, app));
    }

    @Test
    public void testBindDropletToApp() {
        CloudBuild build = ImmutableCloudBuild.builder()
                                              .dropletInfo(ImmutableDropletInfo.builder()
                                                                               .guid(DROPLET_GUID)
                                                                               .build())
                                              .build();
        Mockito.when(client.getBuild(BUILD_GUID))
               .thenReturn(build);
        applicationStager.bindDropletToApplication(execution.getContext(), APP_GUID);
        Mockito.verify(client)
               .bindDropletToApp(DROPLET_GUID, APP_GUID);
    }

    @Test
    public void testStageAppIfThereIsNoUploadToken() {
        Mockito.when(context.getVariable(Constants.VAR_UPLOAD_TOKEN))
               .thenReturn(null);
        assertEquals(StepPhase.DONE, applicationStager.stageApp(context, null, null));
    }

    @Test
    public void testStageAppWithValidParameters() {
        StepLogger stepLogger = Mockito.mock(StepLogger.class);
        CloudApplication app = ImmutableCloudApplication.builder()
                                                        .name(APP_NAME)
                                                        .build();
        setUploadTokenVariable();
        CloudBuild build = createBuild();
        Mockito.when(client.createBuild(PACKAGE_GUID))
               .thenReturn(build);
        StepPhase stepPhase = applicationStager.stageApp(context, app, stepLogger);
        assertEquals(StepPhase.POLL, stepPhase);
        Mockito.verify(context)
               .setVariable(Constants.VAR_BUILD_GUID, BUILD_GUID);
        Mockito.verify(stepLogger)
               .info(Messages.STAGING_APP, APP_NAME);
    }

    @Test
    public void testStageIfNotFoundExceptionIsThrown() {
        CloudApplication app = createApplication();
        CloudOperationException cloudOperationException = Mockito.mock(CloudOperationException.class);
        Mockito.when(cloudOperationException.getStatusCode())
               .thenReturn(HttpStatus.NOT_FOUND);
        Mockito.when(client.createBuild(any(UUID.class)))
               .thenThrow(cloudOperationException);
        Assertions.assertThrows(CloudOperationException.class, () -> applicationStager.stageApp(context, app, stepLogger));
    }

    @Test
    public void testStageIfUnprocessableEntityExceptionIsThrownNoPreviousBuilds() {
        CloudApplication app = createApplication();
        CloudOperationException cloudOperationException = Mockito.mock(CloudOperationException.class);
        Mockito.when(cloudOperationException.getStatusCode())
               .thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
        Mockito.when(client.createBuild(any(UUID.class)))
               .thenThrow(cloudOperationException);
        CloudOperationException thrownException = Assertions.assertThrows(CloudOperationException.class,
                                                                          () -> applicationStager.stageApp(context, app, stepLogger));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
    }

    @Test
    public void testIfBuildGuidDoesNotExist() {
        Mockito.when(execution.getContext()
                              .getVariable(Constants.VAR_BUILD_GUID))
               .thenReturn(null);
        StagingState stagingState = applicationStager.getStagingState(execution.getContext());
        assertEquals(PackageState.STAGED, stagingState.getState());
        assertNull(stagingState.getError());
    }

    @Test
    public void testStageIfUnprocessableEntityExceptionIsThrownSetPreviousBuildGuid() {
        CloudApplication app = createApplication();
        Mockito.when(client.createBuild(any(UUID.class)))
               .thenThrow(new CloudOperationException(HttpStatus.UNPROCESSABLE_ENTITY));
        CloudBuild build = createBuild(CloudBuild.State.STAGING, Mockito.mock(DropletInfo.class), null);
        Mockito.when(client.getBuildsForPackage(any(UUID.class)))
               .thenReturn(Collections.singletonList(build));
        applicationStager.stageApp(context, app, stepLogger);
        Mockito.verify(context)
               .setVariable(Constants.VAR_BUILD_GUID, build.getMetadata()
                                                           .getGuid());
    }

    private void setUploadTokenVariable() {
        String uploadTokenJson = JsonUtil.toJson(ImmutableUploadToken.builder()
                                                                     .packageGuid(PACKAGE_GUID)
                                                                     .build());
        Mockito.when(context.getVariable(Constants.VAR_UPLOAD_TOKEN))
               .thenReturn(uploadTokenJson);
    }

    private CloudApplication createApplication() {
        return ImmutableCloudApplication.builder()
                                        .metadata(ImmutableCloudMetadata.builder()
                                                                        .guid(APP_GUID)
                                                                        .createdAt(new Date())
                                                                        .updatedAt(new Date())
                                                                        .build())
                                        .name(APP_NAME)
                                        .build();
    }

    private CloudBuild createBuild() {
        return createBuild(null, null, null);
    }

    private CloudBuild createBuild(CloudBuild.State state, DropletInfo dropletInfo, String error) {
        return createBuild(state, dropletInfo, error, new Date());
    }

    private CloudBuild createBuild(CloudBuild.State state, DropletInfo dropletInfo, String error, Date timestamp) {
        return ImmutableCloudBuild.builder()
                                  .metadata(ImmutableCloudMetadata.builder()
                                                                  .guid(BUILD_GUID)
                                                                  .createdAt(timestamp)
                                                                  .updatedAt(timestamp)
                                                                  .build())
                                  .state(state)
                                  .error(error)
                                  .dropletInfo(dropletInfo)
                                  .build();
    }
}