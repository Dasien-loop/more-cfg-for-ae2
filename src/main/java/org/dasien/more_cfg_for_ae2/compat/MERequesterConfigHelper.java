package org.dasien.more_cfg_for_ae2.compat;

import org.dasien.more_cfg_for_ae2.Config;

public final class MERequesterConfigHelper {
    private MERequesterConfigHelper() {
    }

    public static long requestAmountLimit() {
        Config config = Config.get();
        return config instanceof Config.MERequesterConfig requesterConfig
                ? requesterConfig.getMeRequesterRequestAmountLimit()
                : Config.ME_REQUESTER_DEFAULT_REQUEST_LIMIT;
    }

    public static long requestBatchLimit() {
        Config config = Config.get();
        return config instanceof Config.MERequesterConfig requesterConfig
                ? requesterConfig.getMeRequesterRequestBatchLimit()
                : Config.ME_REQUESTER_DEFAULT_REQUEST_LIMIT;
    }

    public static long clampRequestAmount(long amount) {
        return amount <= 0 ? amount : Math.min(amount, requestAmountLimit());
    }

    public static long clampRequestBatch(long batch) {
        return Math.max(1L, Math.min(batch, requestBatchLimit()));
    }
}
