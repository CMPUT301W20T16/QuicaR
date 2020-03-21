package com.example.quicar;

import java.util.Date;

/**
 * This is the class that store complaint record generate by user with recordRid, reason, comment and date and time
 */
public class ComplaintRecord {
    private String recordRid;
    private String reason;
    private String comment;
    private User complaintUser;
    private Date dateTime;

    /**
     * This is an empty constructor (needed for storing into firebase directly)
     */
    public ComplaintRecord(){}

    /**
     * This is the constructor that takes in initial values to create an instance
     * @param recordRid
     *  if the user complain a driver then he should fill a recordRid
     * @param reason
     *  the reason that complain
     * @param comment
     *  detailed comment by the user
     * @param complaintUser
     *  the complainant user
     */
    public ComplaintRecord(String recordRid, String reason, String comment, User complaintUser){
        this.recordRid = recordRid;
        this.reason = reason;
        this.comment = comment;
        this.complaintUser = complaintUser;
        this.dateTime = new Date();
    }


    /**
     * This method return the recordRid of the complain
     * @return
     *  the recordRid of the complainant record
     */
    public String getRecordRid() {
        return recordRid;
    }

    /**
     * This method set recordRid of the complainant
     * @param recordRid
     *  the recordRid of the complainant record
     */
    public void setRecordRid(String recordRid) {
        this.recordRid = recordRid;
    }

    /**
     * This method return the reason of the complainant
     * @return
     *  the reason of the complainant record
     */
    public String getReason() {
        return reason;
    }

    /**
     * This method set reason of the complainant
     * @param reason
     *  the reason of the complainant record
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * This method return the comment of the complainant
     * @return
     *  the comment of the complainant record
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method set comment of the complainant
     * @param comment
     *  the comment of the complainant record
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * This method return the user of the complainant
     * @return
     *  the user of the complainant record
     */
    public User getComplaintUser() {
        return complaintUser;
    }

    /**
     * This method set user of the complainant
     * @param complaintUser
     *  the user of the complainant record
     */
    public void setComplaintUser(User complaintUser) {
        this.complaintUser = complaintUser;
    }

    /**
     * This method return the date and time as Date (data type)
     * @return
     *  date and time
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * This method set the date and time of the record
     * @param dateTime
     *  date and time in Date (data type)
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
