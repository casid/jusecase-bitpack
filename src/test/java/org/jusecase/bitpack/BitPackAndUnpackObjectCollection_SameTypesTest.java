package org.jusecase.bitpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BitPackAndUnpackObjectCollection_SameTypesTest extends BitPackAndUnpackObjectTest<BitPackAndUnpackObjectCollection_SameTypesTest.Turn> {

    @Override
    public void setUp() {
        super.setUp();
        protocol.register(new TurnSerializer());
        protocol.register(new SameRequestSerializer());
    }

    @Test
    public void sameTypes() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.sameRequests = new ArrayList<>();

        SameRequest request1 = new SameRequest();
        request1.seed = 42;
        object.sameRequests.add(request1);

        SameRequest request2 = new SameRequest();
        request1.seed = 43;
        object.sameRequests.add(request2);

        thenEntitySerializationWorks();
    }

    @Test
    public void sameTypes_emptyList() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.sameRequests = new ArrayList<>();

        thenEntitySerializationWorks();
    }

    @Test
    public void sameTypes_nullEntityInList() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.sameRequests = new ArrayList<>();
        object.sameRequests.add(null);

        thenEntitySerializationWorks();
    }

    @Test
    public void sameTypes_null() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.sameRequests = null;

        thenEntitySerializationWorks();
    }

    public static class Turn {
        public int playerId;
        public int turnNumber;
        public List<SameRequest> sameRequests;
    }

    public static class TurnSerializer implements BitSerializer<Turn> {

        @Override
        public void serialize(BitPacker packer, Turn object) {
            packer.packInt8(object.playerId);
            packer.packInt32(object.turnNumber);
            packer.packObjectsWithSameType(object.sameRequests);
        }

        @Override
        public Turn deserialize(BitUnpacker unpacker) {
            Turn turn = new Turn();
            turn.playerId = unpacker.unpackInt8();
            turn.turnNumber = unpacker.unpackInt32();
            turn.sameRequests = unpacker.unpackObjectsWithSameTypeAsList(SameRequest.class);
            return turn;
        }
    }

    public static class SameRequest {

        public long seed;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SameRequest request1 = (SameRequest) o;
            return seed == request1.seed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(seed);
        }

        @Override
        public String toString() {
            return "SameRequest{" +
                    "seed=" + seed +
                    '}';
        }
    }

    public static class SameRequestSerializer implements BitSerializer<SameRequest> {

        @Override
        public void serialize(BitPacker packer, SameRequest object) {
            packer.packLong(object.seed);
        }

        @Override
        public SameRequest deserialize(BitUnpacker unpacker) {
            SameRequest request = new SameRequest();
            request.seed = unpacker.unpackLong();
            return request;
        }
    }
}
