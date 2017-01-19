/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.Student
 * Revision:            1.0
 * Date:                09-09-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A student to be displayed to the world
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

public class Student implements java.io.Serializable
{
    private UUID studentId;
    private String name;
    private String parentPhone;
    private String parentName;
    private String driverPhone;
    private String oneTimePassword;
    private long generatedTime;

    public Student(String nm, String pp, String pn, String d)
    {
        studentId = UUID.randomUUID();
        name = nm;
        parentPhone = pp;
        parentName = pn;
        driverPhone = d;
        generateOTP();
    }

    public void generateOTP()
    {
        oneTimePassword = RandomStringUtils.randomNumeric(5);
        generatedTime = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public String getParentPhone() { return parentPhone; }
    public String getParentName() { return parentName; }
    public String getDriverPhone() { return driverPhone; }
    public UUID getStudentId() { return studentId; }

    public void resetOTP() { oneTimePassword = ""; }
}

