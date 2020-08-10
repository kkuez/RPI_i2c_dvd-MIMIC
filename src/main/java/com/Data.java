package com;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Data {
    public final String adress;
    public final String data;
    public final ACK ack;
    public final RW rw;

    public Data(String adress, String data, RW rw) {
        this.rw = rw;
        this.adress = adress;
        this.data = data;
        ack = ACK.UNKNOWN;
    }

    public Data(String adress, String data, ACK ack, RW rw) {
        this.rw = rw;
        this.adress = adress;
        this.data = data;
        this.ack = ack;
    }

    public static List<Data> parse(InputStream inputStream) {
        List<Data> dataList = new ArrayList<>();
        try(inputStream;
            Scanner scanner = new Scanner(inputStream);) {
            boolean validLine = false;
            String line = "";
            while(scanner.hasNext()) {
                line = scanner.nextLine();
                validLine = line.contains("Adress") && (line.contains("READ") || line.contains("WRITE")) && line
                        .contains("Data");
                if(validLine) {
                    dataList.add(parse(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private static Data parse(String line) {
        String addr = line.substring(line.indexOf(" ") + 1, line.indexOf(" ") + 3);
        RW rw = RW.valueOf(line.substring(line.indexOf(addr + ' ') + 3, line.indexOf(',')));
        String data;
        if(line.endsWith(" ")){
            data = line.substring(line.indexOf("Data: ") + 6, line.lastIndexOf(" "));
        } else {

            data = line.substring(line.indexOf("Data: ") + 6);
        }
        return new Data(addr, data, rw);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Adress: ").append(adress).append(" ").append(rw).append(", Data: ")
                .append(data).append(" ").toString();
    }

    public String getAdress() {
        return adress;
    }

    public String getData() {
        return data;
    }

    public ACK getAck() {
        return ack;
    }

    public RW getRw() {
        return rw;
    }
}
