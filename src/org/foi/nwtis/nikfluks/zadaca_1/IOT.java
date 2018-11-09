package org.foi.nwtis.nikfluks.zadaca_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOT {
    String id;
    Map<String, Object> rjecnikIotUredaja;
    public static List<IOT> listaIotUredaja = new ArrayList<IOT>();

    public IOT() {
        rjecnikIotUredaja = new HashMap();
    }
}
