/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.traffic.track.UploadResponse
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A response for upload
 *
 * ************************************************************
 * */

package org.xchg.online.traffic.track;

public class UploadResponse implements java.io.Serializable
{
    private String message;
    private String tripName;

    public UploadResponse(String msg, String tname)
    {
        message = msg;
        tripName = tname;
    }
}

