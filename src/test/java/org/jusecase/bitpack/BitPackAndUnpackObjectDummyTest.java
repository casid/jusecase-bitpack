package org.jusecase.bitpack;

import org.junit.Test;

public class BitPackAndUnpackObjectDummyTest extends BitPackAndUnpackObjectTest<BitPackAndUnpackObjectDummyTest.Dummy> {

    @Override
    public void setUp() {
        super.setUp();

        protocol.register(new DummySerializer());
    }

    @Test
    public void roundTrip1() {
        object.x = 100;
        object.y = 50;
        thenEntitySerializationWorks();
    }

    @Test
    public void roundTrip2() {
        object.x = -3019;
        object.y = 8120;
        thenEntitySerializationWorks();
    }

    public static class Dummy {
        public int x;
        public int y;
    }

    public static class DummySerializer implements BitSerializer<Dummy> {

        @Override
        public Dummy createObject() {
            return new Dummy();
        }

        @Override
        public void serialize(BitWriter writer, Dummy object) {
            writer.writeInt32(object.x);
            writer.writeInt32(object.y);
        }

        @Override
        public void deserialize(BitReader reader, Dummy object) {
            object.x = reader.readInt32();
            object.y = reader.readInt32();
        }
    }
}