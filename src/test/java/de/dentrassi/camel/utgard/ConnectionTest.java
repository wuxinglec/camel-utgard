/*
 * Copyright (C) 2018 Red Hat Inc
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.dentrassi.camel.utgard;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://openscada.atlassian.net/wiki/spaces/OP/pages/6094892/HowToStartWithUtgard
//https://openscada.atlassian.net/wiki/spaces/OP/pages/6094892/HowToStartWithUtgard
public class ConnectionTest {
    public static void main(final String[] args) throws Exception {


        testWriteKepserver();

        //testWriteKepserver();
        //TMT
        final ConnectionInformation ci = new ConnectionInformation();

        ci.setHost("192.168.1.110");
        ci.setDomain("");
        ci.setUser("ADMINISTRATOR");
        ci.setPassword("123");
        ci.setProgId("Kepware.KEPServerEX.V5");
        //B3AF0BF6-4C0C-4804-A122-6F3B160F4397
        ci.setClsid("B3AF0BF6-4C0C-4804-A122-6F3B160F4397"); // if ProgId is not working, try it using the Clsid instead

        final String itemId = "Channel1.Device1.Tag1";




    }


    public static void testWriteKepserver()throws Exception{

        //test01();
        //test
        final ConnectionInformation ci = new ConnectionInformation();

        ci.setHost("192.168.1.110");
        ci.setDomain("");
        ci.setUser("ADMINISTRATOR");
        ci.setPassword("123");
        ci.setProgId("Kepware.KEPServerEX.V5");
        //B3AF0BF6-4C0C-4804-A122-6F3B160F4397
        ci.setClsid("B3AF0BF6-4C0C-4804-A122-6F3B160F4397"); // if ProgId is not working, try it using the Clsid instead

        final String itemId = "Channel1.Device1.Tag1";

        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // connect to server
            server.connect();
            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 500);
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    // also dump value
                    try {
                        if (state.getValue().getType() == JIVariant.VT_UI4) {
                            System.out.println("<<< " + state + " / value = " + state.getValue().getObjectAsUnsigned().getValue());
                        } else {
                            System.out.println("<<< " + state + " / value = " + state.getValue().getObject());
                        }
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Add a new group
            final Group group = server.addGroup("test");
            // Add a new item to the group
            final Item item = group.addItem(itemId);

            // start reading
            access.bind();

            // add a thread for writing a value every 3 seconds
            ScheduledExecutorService writeThread = Executors.newSingleThreadScheduledExecutor();

            final AtomicInteger i = new AtomicInteger(0);

            writeThread.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    final JIVariant value = new JIVariant(i.incrementAndGet());
                    try {
                        System.out.println(">>> " + "writing value " + i.get());
                        item.write(value);
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            }, 5, 3, TimeUnit.SECONDS);

            // wait a little bit
            Thread.sleep(20 * 100000);
            writeThread.shutdownNow();
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }




    }





    //read ---kepserver-》》》》》》》check status is ok
    public static void testReadKepserver()throws Exception{

        //test
        final ConnectionInformation ci = new ConnectionInformation();

        ci.setHost("192.168.1.110");
        ci.setDomain("");
        ci.setUser("ADMINISTRATOR");
        ci.setPassword("123");
        ci.setProgId("Kepware.KEPServerEX.V5");
        //B3AF0BF6-4C0C-4804-A122-6F3B160F4397
        ci.setClsid("B3AF0BF6-4C0C-4804-A122-6F3B160F4397"); // if ProgId is not working, try it using the Clsid instead

        final String itemId = "_System._Time_Second";

        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // connect to server
            server.connect();

            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 500);

            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    System.out.println("----->>>>>>>"+state);
                }
            });


            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(10 * 100000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }





    }



    public static void testSource(final String[] args)throws Exception{

        Logger.getLogger("org.jinterop").setLevel(Level.OFF);

        final ConnectionKey connectionKey = new ConnectionKey();

        connectionKey.setClsid("B3AF0BF6-4C0C-4804-A122-6F3B160F4397");
        connectionKey.setDomain("");
        connectionKey.setHost("192.168.1.110");
        connectionKey.setUser("ADMINISTRATOR");//administrator ADMINISTRATOR
        connectionKey.setPassword("123");

        // ci.setClsid("680DFBF7-C92D-484D-84BE-06DC3DECCD68"); // if ProgId is not working, try it using the Clsid instead


        final UtgardConnection connection = new UtgardConnection(connectionKey);


        //connection.removeItem("Triangle Waves.Int4");

        connection.addItem("Triangle Waves.Int4", v -> {
            System.out.println(v.getValue());
        });

        Thread.sleep(10_000);



        Thread.sleep(10_000);



    }


}
