/*
 * This file is part of RskJ
 * Copyright (C) 2018 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.peg.federation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.rsk.bitcoinj.core.BtcECKey;
import org.ethereum.crypto.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FederationMemberTest {
    private BtcECKey btcKey;
    private ECKey rskKey;
    private ECKey mstKey;
    private FederationMember member;

    @BeforeEach
    void createFederationMember() {
        btcKey = new BtcECKey();
        rskKey = new ECKey();
        mstKey = new ECKey();
        member = new FederationMember(btcKey, rskKey, mstKey);
    }

    @Test
    void immutable() {
        Assertions.assertNotSame(btcKey, member.getBtcPublicKey());
        Assertions.assertArrayEquals(btcKey.getPubKey(), member.getBtcPublicKey().getPubKey());
        Assertions.assertNotSame(rskKey, member.getRskPublicKey());
        Assertions.assertArrayEquals(rskKey.getPubKey(), member.getRskPublicKey().getPubKey());
    }

    @Test
    void testEquals_basic() {
        assertEquals(member, member);

        assertNotEquals(null, member);
        assertNotEquals(member, new Object());
        assertNotEquals("something else", member);
    }

    @Test
    void testEquals_sameKeys() {
        FederationMember otherMember = new FederationMember(btcKey, rskKey, mstKey);

        assertEquals(member, otherMember);
    }

    @Test
    void testEquals_sameKeysDifferentCompression() {
        FederationMember uncompressedMember = new FederationMember(
                BtcECKey.fromPublicOnly(btcKey.getPubKeyPoint().getEncoded(false)),
                ECKey.fromPublicOnly(rskKey.getPubKey(false)),
                ECKey.fromPublicOnly(mstKey.getPubKey(false))
        );

        FederationMember compressedMember = new FederationMember(
                BtcECKey.fromPublicOnly(btcKey.getPubKeyPoint().getEncoded(true)),
                ECKey.fromPublicOnly(rskKey.getPubKey(true)),
                ECKey.fromPublicOnly(mstKey.getPubKey(true))
        );

        assertEquals(compressedMember, uncompressedMember);
        assertEquals(uncompressedMember, compressedMember);
    }

    @Test
    void testEquals_differentBtcKey() {
        FederationMember otherMember = new FederationMember(new BtcECKey(), rskKey, mstKey);

        assertNotEquals(member, otherMember);
    }

    @Test
    void testEquals_differentRskKey() {
        FederationMember otherMember = new FederationMember(btcKey, new ECKey(), mstKey);

        assertNotEquals(member, otherMember);
    }

    @Test
    void testEquals_differentMstKey() {
        FederationMember otherMember = new FederationMember(btcKey, rskKey, new ECKey());

        assertNotEquals(member, otherMember);
    }

    @Test
    void keyType_byValue() {
        assertEquals(FederationMember.KeyType.BTC, FederationMember.KeyType.byValue("btc"));
        assertEquals(FederationMember.KeyType.RSK, FederationMember.KeyType.byValue("rsk"));
        assertEquals(FederationMember.KeyType.MST, FederationMember.KeyType.byValue("mst"));
    }

    @Test
    void keyType_byValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> FederationMember.KeyType.byValue("whatever"));
    }

    @Test
    void serializeAndDeserializeFederationMember() {
        byte[] serializedMember = member.serialize();
        FederationMember deserializedSerializedMember = FederationMember.deserialize(serializedMember);

        assertEquals(deserializedSerializedMember, member);
    }
}
