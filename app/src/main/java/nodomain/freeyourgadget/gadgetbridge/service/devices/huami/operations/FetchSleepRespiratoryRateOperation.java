/*  Copyright (C) 2023 José Rebelo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.GregorianCalendar;

import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;

/**
 * An operation that fetches sleep respiratory rate data.
 */
public class FetchSleepRespiratoryRateOperation extends AbstractRepeatingFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger(FetchSleepRespiratoryRateOperation.class);

    public FetchSleepRespiratoryRateOperation(final HuamiSupport support) {
        super(support, HuamiService.COMMAND_ACTIVITY_DATA_TYPE_SLEEP_RESPIRATORY_RATE, "sleep respiratory rate data");
    }

    @Override
    protected boolean handleActivityData(final GregorianCalendar timestamp, final byte[] bytes) {
        if (bytes.length % 8 != 0) {
            LOG.error("Unexpected length for sleep respiratory rate data {}, not divisible by 8", bytes.length);
            return false;
        }

        final ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        while (buf.position() < bytes.length) {
            final long timestampSeconds = buf.getInt();
            final byte unknown1 = buf.get(); // always 4?
            final int respiratoryRate = buf.get() & 0xff;
            final byte unknown2 = buf.get(); // always 0?
            final byte unknown3 = buf.get(); // mostly 1, sometimes 2, 4 when waking up?

            timestamp.setTimeInMillis(timestampSeconds * 1000L);

            LOG.debug("Sleep Respiratory Rate at {}: respiratoryRate={} unknown1={} unknown2={} unknown3={}", timestamp.getTime(), respiratoryRate, unknown1, unknown2, unknown3);
            // TODO save
        }

        return true;
    }

    @Override
    protected String getLastSyncTimeKey() {
        return "lastSleepRespiratoryRateTimeMillis";
    }
}
