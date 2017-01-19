/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.VerifyDriver
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A verification of driver to create user password
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import java.util.List;
import java.util.ArrayList;

public class VerifyDriver implements java.io.Serializable
{
    private String oneTimePassword;
    private String password;

    private List<String> role;
    private String identityType;

    public VerifyDriver()
    {
    }

    public String getOTP() { return oneTimePassword; }
    public void setRole() 
    { 
        role = new ArrayList<String>();
        role.add("driver"); 
        identityType = "custom";
    }
}

