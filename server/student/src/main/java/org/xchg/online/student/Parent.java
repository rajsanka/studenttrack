/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.Parent
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A parent representation for the student
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;


public class Parent implements java.io.Serializable
{
    private static class StudentDriver
    {
        private UUID id;
        private String phone;

        StudentDriver(UUID sid, String p)
        {
            id = sid;
            phone = p;
        }

        public UUID getId() { return id; }
    }

    private String phone;
    private String name;
    private List<StudentDriver> trackDrivers;

    public Parent(String ph, String nm)
    {
        phone = ph;
        name = nm;
        trackDrivers = new ArrayList<StudentDriver>();
    }

    private boolean studentExists(Student s)
    {
        for (StudentDriver sd : trackDrivers)
        {
            if (sd.getId().equals(s.getStudentId()))
                return true;
        }

        return false;
    }

    public void addStudent(Student s)
    {
        if (!studentExists(s))
        {
            StudentDriver sd = new StudentDriver(s.getStudentId(), s.getDriverPhone());
            trackDrivers.add(sd);
        }
    }

}

