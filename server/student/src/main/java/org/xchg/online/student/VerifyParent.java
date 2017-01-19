/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.VerifyParent
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A verification of parent to create user password
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class VerifyParent implements java.io.Serializable
{
    private String phone;
    private String name;
    private String oneTimePassword;
    private String password;

    private List<String> role;
    private String identityType;

    private List students;
    private Map<String, Object> search;

    public VerifyParent()
    {
    }

    public void setupSearch()
    {
        role = new ArrayList<String>();
        role.add("parent");
        identityType = "custom";
        search = new HashMap<String, Object>();
        search.put("parentPhone", phone);
        search.put("oneTimePassword", oneTimePassword);
        students = new ArrayList();
    }

    public String getOTP() { return oneTimePassword; }
    public List getStudents() { return students; }

    public String getPhone() { return phone; }
    public String getName() { return name; }
}

