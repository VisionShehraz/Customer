package com.gotaxiride.driver.gmaps;

import com.google.android.gms.maps.model.LatLng;
import com.gotaxiride.driver.network.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GMapDirection
{
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";
    public final static String API_KEY = "API_KEY"; // please input API KEY Google Maps

    public GMapDirection() { }


    public String getUrl(LatLng start, LatLng end, String mode, boolean isAlternative) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving"
                + "&key=" + API_KEY;

        if(isAlternative)
            url += "&alternatives=true";

        Log.e("getUrl", url);
        return url;
    }

    public Document getDocument(LatLng start, LatLng end, String mode) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving"
                + "&key=" + API_KEY;

//        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
//                + "origin=" + start.latitude + "," + start.longitude
//                + "&destination=" + end.latitude + "," + end.longitude
//                + "&sensor=false&units=metric&mode=driving"+"alternatives=true";

        url = url.replace(" ", "+");

        Log.e("URL", url);

        try {
            URL ur = new URL(url);
            URLConnection connection = ur.openConnection();

            InputStream in = connection.getInputStream();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Document getDocument(String origin, String destination, String mode) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin + "&destination=" + destination
                + "&sensor=false&units=metric&mode=driving"
                + "&key=" + API_KEY;

        url = url.replace(" ", "+");
        Log.e("Query URL", url);

        try {
            URL ur = new URL(url);
            URLConnection connection = ur.openConnection();

            InputStream in = connection.getInputStream();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDurationText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDurationValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DurationValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getDistanceText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDistanceValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public List<Float> getDistanceValueInMeters (Document doc) {
        List<Float> list = new ArrayList<Float>();

        NodeList nl1 = doc.getElementsByTagName("distance");

        for(int x = 0; x < nl1.getLength(); x++) {
            Node node1 = nl1.item(x);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));

            Log.i("DistanceValue", node2.getTextContent());

            float val = Integer.parseInt(node2.getTextContent());

            list.add(val);
        }


        return list;
    }

    public String getStartAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getCopyRights (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    }


    public LatLng getStartLocation (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_location");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "lat"));
        Node node21 = nl2.item(getNodeIndex(nl2, "lng"));

        Log.i("DistanceValue", node2.getTextContent());
        return new LatLng(Float.parseFloat(node2.getTextContent()), Float.parseFloat(node21.getTextContent()) );
    }

    public LatLng getEndLocation (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_location");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "lat"));
        Node node21 = nl2.item(getNodeIndex(nl2, "lng"));

        Log.i("DistanceValue", node2.getTextContent());
        return new LatLng(Float.parseFloat(node2.getTextContent()), Float.parseFloat(node21.getTextContent()) );
    }

    public ArrayList<LatLng> getDirection (Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for(int j = 0 ; j < arr.size() ; j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    public ArrayList<Direction> getDirections (Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<Direction> listDirections = new ArrayList<Direction>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {

                Direction d = new Direction();

                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "duration"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "text"));
                d.durationText = latNode.getTextContent();


                locationNode = nl2.item(getNodeIndex(nl2, "html_instructions"));
                d.html_instructions = locationNode.getTextContent();

                locationNode = nl2.item(getNodeIndex(nl2, "distance"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "text"));
                d.distanceText = latNode.getTextContent();

                listDirections.add(d);
            }
        }

        return listDirections;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
}

