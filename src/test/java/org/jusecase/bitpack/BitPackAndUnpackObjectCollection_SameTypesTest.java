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
        public Turn createObject() {
            return new Turn();
        }

        @Override
        public void serialize(BitWriter writer, Turn object) {
            writer.writeInt8(object.playerId);
            writer.writeInt32(object.turnNumber);
            writer.writeObjectsWithSameType(8, object.sameRequests);
        }

        @Override
        public void deserialize(BitReader reader, Turn object) {
            object.playerId = reader.readInt8();
            object.turnNumber = reader.readInt32();
            object.sameRequests = reader.readObjectsWithSameTypeAsList(8, SameRequest.class);
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
        public SameRequest createObject() {
            return new SameRequest();
        }

        @Override
        public void serialize(BitWriter writer, SameRequest object) {
            writer.writeLong(object.seed);
        }

        @Override
        public void deserialize(BitReader reader, SameRequest object) {
            object.seed = reader.readLong();
        }
    }
}
