package com.sap.cloud.lm.sl.cf.process.util;

import org.cloudfoundry.client.lib.domain.PackageState;
import org.cloudfoundry.client.lib.domain.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface StagingState {

    PackageState getState();

    @Nullable
    String getError();

}
