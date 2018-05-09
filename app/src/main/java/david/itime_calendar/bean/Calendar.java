package david.itime_calendar.bean;

/**
 * Created by David Liu on 24/09/2016.
 * lyhmelbourne@gmail.com
 */

public class Calendar {
    private String iCalUID;
    private String summary;
    private String color;
    private int access;
    private int status;
    private String calendarUid;
    private int groupUid;
    private int groupTitle;
    private int isShown;
    private String createdAt;
    private String updatedAt;

    public String getiCalUID() {
        return iCalUID;
    }

    public void setiCalUID(String iCalUID) {
        this.iCalUID = iCalUID;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCalendarUid() {
        return calendarUid;
    }

    public void setCalendarUid(String calendarUid) {
        this.calendarUid = calendarUid;
    }

    public int getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(int groupUid) {
        this.groupUid = groupUid;
    }

    public int getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(int groupTitle) {
        this.groupTitle = groupTitle;
    }

    public int getIsShown() {
        return isShown;
    }

    public void setIsShown(int isShown) {
        this.isShown = isShown;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "iCalUID='" + iCalUID + '\'' +
                ", summary='" + summary + '\'' +
                ", color='" + color + '\'' +
                ", access=" + access +
                ", status=" + status +
                ", calendarUid='" + calendarUid + '\'' +
                ", groupUid=" + groupUid +
                ", groupTitle=" + groupTitle +
                ", isShown=" + isShown +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
