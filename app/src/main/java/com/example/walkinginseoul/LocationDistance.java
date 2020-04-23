package com.example.walkinginseoul;

import android.util.Log;

public class LocationDistance {
    public LocationDistance() {

    }

    public static double distance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        // 미터 단위.
        dist = dist * 1609.344;

        return dist;


    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static int[] transfer(Double[] arr1, Double[] arr2){
        int[] result = new int[15];

        for(int i=0; i<arr2.length; i++){
            for(int j=0; j<arr2.length; j++){
                if(arr2[i].equals(arr1[j])){
                    result[i] = j;
                    Log.v("distance@@", String.valueOf(result[i]));
                }
            }
        }

        return result;
    }
}

