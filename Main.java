/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmx_mon_proj;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author cchi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static final String JMX_URI = "service:jmx:rmi:///jndi/rmi://localhost:9426/jmxrmi";

    public static void main(String[] args) throws MalformedURLException, IOException, MalformedObjectNameException {
        try {
            // Get MbeanServerConnection
            MBeanServerConnection mbc;
            JMXServiceURL url = new JMXServiceURL(JMX_URI);
            JMXConnector jmxcon = JMXConnectorFactory.connect(url, null);
            mbc = jmxcon.getMBeanServerConnection();

            //Get GC Collectors
            Iterator<ObjectName> itr = mbc.queryNames(null, null).iterator();
            ArrayList gcList = new ArrayList();

            while( itr.hasNext() ) {
                String matchedName = itr.next().toString();
                if(matchedName.indexOf("GarbageCollector")>0){
                    gcList.add(matchedName);
                }
                
            }
            System.out.println(gcList);

            //Fetch GC type collection time
            for(int i=0;i<gcList.size();i++){
                ObjectName queryName = new ObjectName(gcList.get(i).toString());
                System.out.print(queryName + ":");
                System.out.println(mbc.getAttribute(queryName, "CollectionTime"));
            }
            

            ObjectName uptimeName = new ObjectName("java.lang:type=Runtime");
            System.out.print("Server Uptime" + ":");
            System.out.print(mbc.getAttribute(uptimeName, "Uptime"));
            System.out.println( " Seconds" );

            //Fetch heap Memory usage stat
            //committed=523501568, max=523501568, used=106156848
            CompositeDataSupport cdsHeap;
            ObjectName  heapMemoryUsage= new ObjectName("java.lang:type=Memory");
            System.out.println(cdsHeap = (CompositeDataSupport) mbc.getAttribute(heapMemoryUsage, "HeapMemoryUsage"));
            System.out.println(cdsHeap.get("used"));

            //Fetch nonheap Memory usage stat
            //committed=523501568, max=523501568, used=106156848
            CompositeDataSupport cdsNonHeap;
            ObjectName  nonHeapMemoryUsage= new ObjectName("java.lang:type=Memory");
            System.out.println(cdsNonHeap = (CompositeDataSupport) mbc.getAttribute(nonHeapMemoryUsage, "NonHeapMemoryUsage"));
            System.out.println(cdsNonHeap.get("used"));


        } catch (MBeanException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}