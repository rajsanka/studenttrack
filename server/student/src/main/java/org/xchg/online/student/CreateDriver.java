/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.CreateDriver
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An event to create driver
 *
 * ************************************************************
 * */

package org.xchg.online.student;

public class CreateDriver implements java.io.Serializable
{
    private String phone;
    private String name;
    private String vehicle;

    public CreateDriver()
    {
    }

    public String getPhone() { return phone; }
    public String getName() { return name; }
    public String getVehicle() { return vehicle; }
}

