/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.xchg.online.student.Driver
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A driver associated with student
 *
 * ************************************************************
 * */

package org.xchg.online.student;

import org.apache.commons.lang.RandomStringUtils;

public class Driver implements java.io.Serializable
{
    private String driverPhone;
    private String driverName;
    private String vehicleNumber;
    private String oneTimePassword;
    private long generatedTime;

    public Driver(String dp, String dn, String vn)
    {
        driverPhone = dp;
        driverName = dn;
        vehicleNumber = vn;
        generateOTP();
    }

    public void generateOTP()
    {
        oneTimePassword = RandomStringUtils.randomNumeric(5);
        generatedTime = System.currentTimeMillis();
    }

    public String getDriverPhone() { return driverPhone; }
    public String getDriverName() { return driverName; }
    public String getVehicleNumber() { return vehicleNumber; }

    public boolean verifyOneTimePassword(String otp)
        throws Exception
    {
        if ((oneTimePassword == null) || (oneTimePassword.length() <= 0))
            throw new Exception("Already verified. Please reset.");

        long time = System.currentTimeMillis();
        //boolean ret = ((time - generatedTime) < (24 * 60 * 60 * 1000));  //valid for 1 day
        boolean ret = ((oneTimePassword !=  null) && (otp != null) && (oneTimePassword.equals(otp)));
        if (!ret)
            throw new Exception("Invalid OTP.");
        return ret;
    }

    public void resetOTP() { oneTimePassword = ""; }
}

