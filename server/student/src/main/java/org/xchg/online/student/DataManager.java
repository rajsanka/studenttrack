/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.DataManager
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A manager for data in this flow
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.List;

public class DataManager
{
    public DataManager()
    {
    }

    public void createDriver(CreateDriver driver, Driver exist)
        throws Exception
    {
        if (exist != null)
        {
            new MessageResponse("Already exists.");
            return;
        }

        Driver d = new Driver(driver.getPhone(), driver.getName(), driver.getVehicle());
        new MessageResponse("Created Driver: " + driver.getPhone());
    }

    public void importStudents(ImportStudents students, Driver driver)
        throws Exception
    {
        List<Student> slist = students.getStudents(driver.getDriverPhone());
        new MessageResponse("Imported students for: " + driver.getDriverPhone());
    }

    public void verifyDriver(VerifyDriver vd, Driver driver)
        throws Exception
    {
        driver.verifyOneTimePassword(vd.getOTP());
        driver.resetOTP();
        vd.setRole();
        new MessageResponse("Verified. Please Login.");
    }

    public void createParent(VerifyParent v, Parent exist)
        throws Exception
    {
        Parent p = exist;
        if (p == null)
        {
            p = new Parent(v.getPhone(), v.getName());
            v.setupSearch();
        }
    }

    public void addStudents(VerifyParent v, Parent p)
        throws Exception
    {
        List students = v.getStudents();
        if ((students == null) || (students.size() <= 0))
            throw new Exception("No Student found with given phone and OTP.");

        for (int i = 0; i < students.size(); i++)
        {
            Student s = (Student)students.get(i);
            s.resetOTP();
            p.addStudent(s);
        }

        new MessageResponse("Added Student.");
    }

}

