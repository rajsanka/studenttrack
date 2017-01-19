/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.MessageResponse
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A message response
 *
 * ************************************************************
 * */

package org.xchg.online.student;

public class MessageResponse implements java.io.Serializable
{
    private String message;

    public MessageResponse(String msg)
    {
        message = msg;
    }
}

