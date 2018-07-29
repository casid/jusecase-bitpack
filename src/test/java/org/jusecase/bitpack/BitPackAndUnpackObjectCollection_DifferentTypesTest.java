package org.jusecase.bitpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BitPackAndUnpackObjectCollection_DifferentTypesTest extends BitPackAndUnpackObjectTest<BitPackAndUnpackObjectCollection_DifferentTypesTest.Turn> {

    @Override
    public void setUp() {
        super.setUp();

        protocol.register(new TurnSerializer());
        protocol.register(new SimulationRequest1Serializer());
        protocol.register(new SimulationRequest2Serializer());
    }

    @Test
    public void subTypes() {

        object.playerId = 5;
        object.turnNumber = 300;
        object.simulationRequests = new ArrayList<>();

        SimulationRequest1 request1 = new SimulationRequest1();
        request1.seed = 42;
        object.simulationRequests.add(request1);

        SimulationRequest2 request2 = new SimulationRequest2();
        request2.x = 132;
        request2.y = 410;
        object.simulationRequests.add(request2);

        thenEntitySerializationWorks();
    }

    @Test
    public void subTypes_nullEntityInList() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.simulationRequests = new ArrayList<>();
        object.simulationRequests.add(null);

        thenEntitySerializationWorks();
    }

    @Test
    public void subTypes_emptyList() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.simulationRequests = new ArrayList<>();

        thenEntitySerializationWorks();
    }

    @Test
    public void subTypes_null() {
        object.playerId = 5;
        object.turnNumber = 300;
        object.simulationRequests = null;

        thenEntitySerializationWorks();
    }

    public static class Turn {
        public int playerId;
        public int turnNumber;
        public List<SimulationRequest> simulationRequests;
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
            writer.writeObjectsWithDifferentTypes(object.simulationRequests);
        }

        @Override
        public void deserialize(BitReader reader, Turn object) {
            object.playerId = reader.readInt8();
            object.turnNumber = reader.readInt32();
            object.simulationRequests = reader.readObjectsWithDifferentTypesAsList();
        }
    }

    public static abstract class SimulationRequest {
    }

    public static class SimulationRequest1 extends SimulationRequest {

        public long seed;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimulationRequest1 request1 = (SimulationRequest1) o;
            return seed == request1.seed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(seed);
        }

        @Override
        public String toString() {
            return "SimulationRequest1{" +
                    "seed=" + seed +
                    '}';
        }
    }

    public static class SimulationRequest1Serializer implements BitSerializer<SimulationRequest1> {

        @Override
        public SimulationRequest1 createObject() {
            return new SimulationRequest1();
        }

        @Override
        public void serialize(BitWriter writer, SimulationRequest1 object) {
            writer.writeLong(object.seed);
        }

        @Override
        public void deserialize(BitReader reader, SimulationRequest1 object) {
            object.seed = reader.readLong();
        }
    }

    public static class SimulationRequest2 extends SimulationRequest {

        public int x;
        public int y;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimulationRequest2 that = (SimulationRequest2) o;
            return x == that.x &&
                    y == that.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "SimulationRequest2{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static class SimulationRequest2Serializer implements BitSerializer<SimulationRequest2> {

        @Override
        public SimulationRequest2 createObject() {
            return new SimulationRequest2();
        }

        @Override
        public void serialize(BitWriter writer, SimulationRequest2 object) {
            writer.writeInt32(object.x);
            writer.writeInt32(object.y);
        }

        @Override
        public void deserialize(BitReader reader, SimulationRequest2 object) {
            object.x = reader.readInt32();
            object.y = reader.readInt32();
        }
    }
}
