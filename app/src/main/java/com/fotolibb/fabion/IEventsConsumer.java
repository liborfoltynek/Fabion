package com.fotolibb.fabion;

import java.util.ArrayList;

/**
 * Created by Libb on 26.10.2017.
 */

interface IEventsConsumer {
    void ProcessData(ArrayList<FabionEvent> events);
}
