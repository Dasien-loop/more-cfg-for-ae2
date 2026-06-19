package org.dasien.more_cfg_for_ae2.compat;

import appeng.client.gui.NumberEntryType;

public interface MERequesterNumberFieldBridge {
    String moreCfgForAe2$getName();

    NumberEntryType moreCfgForAe2$getType();

    long moreCfgForAe2$getValue();

    long moreCfgForAe2$getLimit();

    void moreCfgForAe2$setValue(long value);

    void moreCfgForAe2$submit(long value);
}
