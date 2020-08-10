package com;

import io.dvlopt.linux.i2c.I2CBuffer;
import io.dvlopt.linux.i2c.I2CBus;
import io.dvlopt.linux.i2c.I2CFunctionalities;
import io.dvlopt.linux.i2c.I2CFunctionality;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        checkUserRoot();
        //List<Data> startUp_DATA = getDataList("i2c_data_startUp");
        List<Data> mode3_1_DATA = getDataList("i2c_data_mode3_1");

        I2CBus bus = new I2CBus("/dev/i2c-1");
        I2CBuffer buffer = new I2CBuffer( 2 );

        //SLAVE ADRESS SEEMS TO BE dec80!!
        //dez 80 => hex 50
        //dez 20 => hex 14
        bus.selectSlave(80);
        //writeToDevice(args[0], startUp_DATA, bus, buffer);
        writeToDevice(args[0], mode3_1_DATA, bus, buffer);

        I2CFunctionalities functionalities = bus.getFunctionalities() ;
        Set<I2CFunctionality> availableFunx = getFunx(functionalities);

        System.out.println();
    }

    private static String readFromDevice(String arg, I2CBus bus, I2CBuffer buffer) {
        String asd = "";
        for(int i = 0;i<128;i++) {
            try {
                bus.read(buffer, 1);
                asd += "   " + buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return asd;
    }

    private static void writeToDevice(String arg, List<Data> mode3_1_DATA, I2CBus bus, I2CBuffer buffer) {
        mode3_1_DATA.stream().filter(data -> {
            final String allowedDataAdd = arg;
            return data.adress.equals(allowedDataAdd) && data.rw.equals(RW.WRITE);
        }).forEach(data -> {
            buffer.set(0, Integer.valueOf(data.data, 16).intValue());
            try {
                I2CBuffer tempBuf = buffer;
                bus.write( buffer ) ;
                bus.read(buffer, 2);
                if(!buffer.equals(tempBuf)) System.out.println(buffer);
            } catch (IOException e) {
                System.out.println(data.toString());
                e.printStackTrace();
            }
        });
    }

    private static Set<I2CFunctionality> getFunx(I2CFunctionalities functionalities) {
        Set<I2CFunctionality> funx = Set.of(I2CFunctionality.READ_BYTE, I2CFunctionality.BLOCK_PROCESS_CALL,
                I2CFunctionality.PROCESS_CALL, I2CFunctionality.PROTOCOL_MANGLING, I2CFunctionality.QUICK,
                I2CFunctionality.READ_BLOCK, I2CFunctionality.READ_BYTE_DIRECTLY, I2CFunctionality.READ_I2C_BLOCK,
                I2CFunctionality.READ_WORD, I2CFunctionality.SMBUS_PEC, I2CFunctionality.TEN_BIT_ADDRESSING,
                I2CFunctionality.TRANSACTIONS, I2CFunctionality.WRITE_BLOCK, I2CFunctionality.WRITE_BYTE,
                I2CFunctionality.WRITE_BYTE_DIRECTLY, I2CFunctionality.WRITE_I2C_BLOCK, I2CFunctionality.WRITE_WORD);
        return funx.stream().filter(func -> functionalities.can(func)).collect(Collectors.toSet());
    }

    private static void checkUserRoot() {
        if(!System.getProperty("user.home").equals("/root")) {
            throw new RuntimeException("Not started as root!");
        }
    }

    private static List<Data> getDataList(String fileName) {
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream(fileName);
        return Data.parse(stream);
    }
}
