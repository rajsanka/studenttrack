/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.ImportStudents
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * To import multiple students
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.List;
import java.util.ArrayList;

public class ImportStudents implements java.io.Serializable
{
    class StudentData
    {
        private String name;
        private String parentPhone;
        private String parentName;

        Student getStudent(String d)
        {
            Student s = new Student(name, parentPhone, parentName, d);
            return s;
        }
    }

    private List<StudentData> students;

    public ImportStudents()
    {
    }

    public List<Student> getStudents(String dPhone)
    {
        List<Student> slist = new ArrayList<Student>();
        for (StudentData sd : students)
        {
            Student s = sd.getStudent(dPhone);
            slist.add(s);
        }

        return slist;
    }
}

